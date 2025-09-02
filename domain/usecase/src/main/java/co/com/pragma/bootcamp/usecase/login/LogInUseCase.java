package co.com.pragma.bootcamp.usecase.login;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.exceptions.login.LoginBusinessException;
import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.model.login.gateways.PasswordGateway;
import co.com.pragma.bootcamp.model.login.gateways.TokenGateway;
import co.com.pragma.bootcamp.model.rol.gateways.RoleRepository;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.USER_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.login.LoginErrors.INVALID_CREDENTIALS;
import static co.com.pragma.bootcamp.model.exceptions.login.LoginErrors.ROLE_NOT_FOUND;

@RequiredArgsConstructor
public class LogInUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public Mono<LogIn> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new BusinessException(USER_NOT_FOUND)))
                .filter(user -> passwordGateway.matches(rawPassword, user.getPassword()))
                .switchIfEmpty(Mono.error(new LoginBusinessException(INVALID_CREDENTIALS)))
                .flatMap(user -> roleRepository.findById(user.getRoleId())
                        .switchIfEmpty(Mono.error(new LoginBusinessException(ROLE_NOT_FOUND)))
                        .flatMap(role -> tokenGateway.generateToken(user.getEmail(), role.getName()))
                );
    }
}