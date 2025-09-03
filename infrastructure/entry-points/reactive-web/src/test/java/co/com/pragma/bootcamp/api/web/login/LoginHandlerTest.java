package co.com.pragma.bootcamp.api.web.login;

import co.com.pragma.bootcamp.api.dto.login.LoginRequest;
import co.com.pragma.bootcamp.api.dto.login.LoginResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.LogInMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.usecase.login.LogInUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Set;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @InjectMocks
    private LoginHandler loginHandler;

    @Mock
    private LogInUseCase logInUseCase;

    @Mock
    private ValidatorUtil validatorUtil;

    @Mock
    private LogInMapper logInMapper;

    @Mock
    private ServerRequest serverRequest;

    private LoginRequest testLoginRequest;
    private LoginResponse testLoginResponse;
    private LogIn testLogIn;

    @BeforeEach
    void setUp() {
        testLoginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testLogIn = LogIn.builder()
                .email("test@example.com")
                .token("generated-token")
                .build();

        testLoginResponse = LoginResponse.builder()
                .token("generated-token")
                .build();
    }

    @Test
    void login_shouldReturnCreated_whenSuccessful() {
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(testLoginRequest));
        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.just(testLoginRequest));
        when(logInUseCase.login(anyString(), anyString())).thenReturn(Mono.just(testLogIn));
        when(logInMapper.toResponse(any(LogIn.class))).thenReturn(testLoginResponse);

        Mono<ServerResponse> responseMono = loginHandler.login(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED)
                )
                .verifyComplete();
    }

    @Test
    void login_shouldReturnBadRequest_whenValidationFails() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException validationException = new ConstraintViolationException(violations);

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(testLoginRequest));
        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.error(validationException));

        Mono<ServerResponse> responseMono = loginHandler.login(serverRequest)
                .onErrorResume(ConstraintViolationException.class, e -> ServerResponse.badRequest().build());

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void login_shouldHandle_whenUserNotFound() {
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(testLoginRequest));
        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.just(testLoginRequest));
        when(logInUseCase.login(anyString(), anyString()))
                .thenReturn(Mono.error(new BusinessException(USER_NOT_FOUND)));

        Mono<ServerResponse> responseMono = loginHandler.login(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getUserError().equals(USER_NOT_FOUND)
                )
                .verify();
    }
}