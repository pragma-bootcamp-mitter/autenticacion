package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UserHandler;
import co.com.pragma.bootcamp.api.UserRouter;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapperImpl;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserRouter.class, UserHandler.class, UserDtoMapperImpl.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @BeforeEach
    void setup() {
        when(userUseCase.registrarUsuario(any(User.class)))
                .thenReturn(Mono.just(User.builder()
                        .id("12345")
                        .nombres("John")
                        .apellidos("Doe")
                        .fechaNacimiento(LocalDate.of(1990, 5, 15))
                        .salarioBase(new BigDecimal("50000.00"))
                        .build()));
    }

    @Test
    void corsAndSecurityHeadersShouldBeApplied() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .bodyValue(User.builder().build())
                .exchange()
                .expectStatus().isOk()
                // Security headers
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