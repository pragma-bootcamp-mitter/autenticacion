package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UserHandler;
import co.com.pragma.bootcamp.api.UserRouter;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
@ContextConfiguration(classes = {UserRouter.class, UserHandler.class, ValidatorUtil.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private Validator validator;

    private User sampleUser;

    @BeforeEach
    void setup() {
        sampleUser = User.builder()
                .id("12345")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .baseSalary(new BigDecimal("50000.00"))
                .build();

        when(userUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.just(sampleUser));

        when(userUseCase.listUsers())
                .thenReturn(Flux.fromIterable(List.of(sampleUser)));

        when(userMapper.toDomain(any(UserRequest.class)))
                .thenAnswer(invocation -> sampleUser);

        when(userMapper.toResponse(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    UserResponse resp = new UserResponse();
                    resp.setId(u.getId());
                    resp.setFirstName(u.getFirstName());
                    resp.setLastName(u.getLastName());
                    resp.setDateOfBirth(u.getDateOfBirth());
                    resp.setBaseSalary(u.getBaseSalary());
                    return resp;
                });
        when(validator.validate(any())).thenReturn(Collections.emptySet());
    }

    @Test
    void corsAndSecurityHeadersAreAppliedOnPost() {
        webTestClient.post()
                .uri("/api/v1/users")
                .bodyValue(User.builder().build())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }



    @Test
    void corsAndSecurityHeadersAreAppliedOnGetAll() {
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}