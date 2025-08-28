package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.DOCUMENT_OR_EMAIL_ALREADY_REGISTERED;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> registerUser(User user) {
        return userRepository.existsByEmailOrIdentificationDocument(user.getEmail(), user.getIdentificationDocument())
                .filter(exists -> !Boolean.TRUE.equals(exists))
                .switchIfEmpty(Mono.error(new BusinessException(DOCUMENT_OR_EMAIL_ALREADY_REGISTERED)))
                .flatMap(exists -> userRepository.save(user));
    }

    public Flux<User> listUsers() {
        return userRepository.findAll();
    }
}