package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.usecase.helper.UserError;
import co.com.pragma.bootcamp.usecase.helper.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> registrarUsuario(User user) {
        try {
            UserValidator.validate(user);
        } catch (BusinessException e) {
            return Mono.error(e);
        }

        return userRepository.findByCorreoElectronico(user.getCorreoElectronico())
                .flatMap(existing -> Mono.<User>error(new BusinessException(UserError.EMAIL_ALREADY_EXISTS.getMessage())))
                .switchIfEmpty(Mono.defer(() -> userRepository.save(user)));
    }
}