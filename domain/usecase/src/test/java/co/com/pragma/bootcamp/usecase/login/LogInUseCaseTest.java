package co.com.pragma.bootcamp.usecase.login;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.exceptions.login.LoginBusinessException;
import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.model.login.gateways.PasswordGateway;
import co.com.pragma.bootcamp.model.login.gateways.TokenGateway;
import co.com.pragma.bootcamp.model.rol.Role;
import co.com.pragma.bootcamp.model.rol.gateways.RoleRepository;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.USER_NOT_FOUND;
import static co.com.pragma.bootcamp.model.exceptions.login.LoginErrors.INVALID_CREDENTIALS;
import static co.com.pragma.bootcamp.model.exceptions.login.LoginErrors.ROLE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogInUseCaseTest {

    @InjectMocks
    private LogInUseCase logInUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordGateway passwordGateway;

    @Mock
    private TokenGateway tokenGateway;

    private User testUser;
    private Role testRole;
    private LogIn testLogIn;
    private String email;
    private String rawPassword;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        rawPassword = "password123";
        encodedPassword = "encodedPassword123";
        String generatedToken = "generated-jwt-token";
        String identificationDocument = "1234567890";

        testUser = User.builder()
                .email(email)
                .password(encodedPassword)
                .identificationDocument(identificationDocument)
                .roleId(1)
                .build();

        testRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .build();

        testLogIn = LogIn.builder()
                .email(email)
                .token(generatedToken)
                .build();
    }

    @Test
    void login_shouldReturnLogIn_whenCredentialsAreValid() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordGateway.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(roleRepository.findById(testUser.getRoleId())).thenReturn(Mono.just(testRole));
        when(tokenGateway.generateToken(anyString(), anyString(), anyString())).thenReturn(Mono.just(testLogIn));

        Mono<LogIn> result = logInUseCase.login(email, rawPassword);

        StepVerifier.create(result)
                .expectNext(testLogIn)
                .verifyComplete();
    }

    @Test
    void login_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        Mono<LogIn> result = logInUseCase.login(email, rawPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getUserError().equals(USER_NOT_FOUND)
                )
                .verify();
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordGateway.matches(rawPassword, encodedPassword)).thenReturn(false);

        Mono<LogIn> result = logInUseCase.login(email, rawPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof LoginBusinessException &&
                                ((LoginBusinessException) throwable).getLoginError().equals(INVALID_CREDENTIALS)
                )
                .verify();
    }

    @Test
    void login_shouldThrowRoleNotFoundException_whenRoleDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordGateway.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(roleRepository.findById(testUser.getRoleId())).thenReturn(Mono.empty());

        Mono<LogIn> result = logInUseCase.login(email, rawPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof LoginBusinessException &&
                                ((LoginBusinessException) throwable).getLoginError().equals(ROLE_NOT_FOUND)
                )
                .verify();
    }

    @Test
    void login_shouldThrowException_whenTokenGenerationFails() {
        RuntimeException tokenGenerationException = new RuntimeException("Token generation failed");

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(passwordGateway.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(roleRepository.findById(testUser.getRoleId())).thenReturn(Mono.just(testRole));
        when(tokenGateway.generateToken(anyString(), anyString(), anyString())).thenReturn(Mono.error(tokenGenerationException));

        Mono<LogIn> result = logInUseCase.login(email, rawPassword);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Token generation failed"))
                .verify();
    }
}