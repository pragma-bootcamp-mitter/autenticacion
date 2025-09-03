package co.com.pragma.bootcamp.api.web.login;

import co.com.pragma.bootcamp.api.config.GlobalExceptionHandler;
import co.com.pragma.bootcamp.api.dto.login.LoginRequest;
import co.com.pragma.bootcamp.api.dto.login.LoginResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.LogInMapper;
import co.com.pragma.bootcamp.model.exceptions.login.LoginBusinessException;
import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.usecase.login.LogInUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.Set;

import static co.com.pragma.bootcamp.api.util.AuthConstants.BAD_REQUEST_TITLE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_CODE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_MESSAGE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_TITLE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.VALIDATION_ERROR_CODE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.VALIDATION_ERROR_MESSAGE;
import static co.com.pragma.bootcamp.model.exceptions.BusinessErrorCode.BR_401_UNAUTHORIZED;
import static co.com.pragma.bootcamp.model.exceptions.login.LoginErrors.INVALID_CREDENTIALS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {LoginRouter.class, LoginHandler.class, ValidatorUtil.class, GlobalExceptionHandler.class})
class LoginRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LogInUseCase logInUseCase;

    @MockitoBean
    private LogInMapper logInMapper;

    @MockitoBean
    private ValidatorUtil validatorUtil;

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
                .token("generated-jwt-token")
                .build();

        testLoginResponse = LoginResponse.builder()
                .token("generated-jwt-token")
                .build();
    }

    @Test
    void login_shouldReturnCreatedAndToken_whenSuccessful() {
        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.just(testLoginRequest));
        when(logInUseCase.login(anyString(), anyString())).thenReturn(Mono.just(testLogIn));
        when(logInMapper.toResponse(any(LogIn.class))).thenReturn(testLoginResponse);

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testLoginRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(SUCCESS_CODE)
                .jsonPath("$.message").isEqualTo(SUCCESS_MESSAGE)
                .jsonPath("$.title").isEqualTo(SUCCESS_TITLE)
                .jsonPath("$.data.token").isEqualTo("generated-jwt-token");
    }

    @Test
    void login_shouldReturnUnauthorized_whenInvalidCredentials() {
        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.just(testLoginRequest));
        when(logInUseCase.login(anyString(), anyString()))
                .thenReturn(Mono.error(new LoginBusinessException(INVALID_CREDENTIALS)));

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testLoginRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(BR_401_UNAUTHORIZED)
                .jsonPath("$.message").isEqualTo("Invalid credentials provided")
                .jsonPath("$.title").isEqualTo("Authentication error");
    }


    @Test
    void login_shouldReturnBadRequest_whenValidationFails() {
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("invalid-email")
                .password("")
                .build();

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("The email is mandatory");
        when(propertyPath.toString()).thenReturn("email");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException validationException = new ConstraintViolationException(violations);

        when(validatorUtil.validate(any(LoginRequest.class))).thenReturn(Mono.error(validationException));

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(VALIDATION_ERROR_CODE)
                .jsonPath("$.message").isEqualTo(VALIDATION_ERROR_MESSAGE)
                .jsonPath("$.title").isEqualTo(BAD_REQUEST_TITLE);
    }
}