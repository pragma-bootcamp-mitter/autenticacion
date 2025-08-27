package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.usecase.helper.UserErrors.EMAIL_ALREADY_REGISTERED;
import static co.com.pragma.bootcamp.usecase.helper.UserErrors.DOCUMENT_ALREADY_REGISTERED;
import static co.com.pragma.bootcamp.usecase.helper.UserErrors.USER_NOT_FOUND;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> registerUser(User user) {
        return userRepository.findByIdentificationDocument(user.getIdentificationDocument())
                .flatMap(existingUser -> Mono.<User>error(
                        new BusinessException(DOCUMENT_ALREADY_REGISTERED.getMessage())
                ))
                .switchIfEmpty(
                        userRepository.existsByEmail(user.getEmail())
                                .flatMap(existsByEmail  -> {
                                    if (Boolean.TRUE.equals(existsByEmail )) {
                                        return Mono.error(new BusinessException(EMAIL_ALREADY_REGISTERED.getMessage()));
                                    }
                                    return userRepository.save(user);
                                })
                );
    }

    public Flux<User> listUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserByDocument(String identificationDocument) {
        return userRepository.findByIdentificationDocument(identificationDocument)
                .switchIfEmpty(Mono.error(
                        new BusinessException(USER_NOT_FOUND.getMessage())));
    }
}