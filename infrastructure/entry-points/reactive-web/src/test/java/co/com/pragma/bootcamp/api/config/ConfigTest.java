package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UserHandler;
import co.com.pragma.bootcamp.api.UserRouter;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapperImpl;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
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
import java.util.List;

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

    private Usuario sampleUsuario;

    @BeforeEach
    void setup() {
        sampleUsuario = Usuario.builder()
                .id("12345")
                .nombres("John")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .salarioBase(new BigDecimal("50000.00"))
                .build();

        when(userUseCase.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(sampleUsuario));

        when(userUseCase.listarUsuarios())
                .thenReturn(Flux.fromIterable(List.of(sampleUsuario)));

        when(userUseCase.obtenerUsuarioPorDocumento("12345"))
                .thenReturn(Mono.just(sampleUsuario));
    }

    @Test
    void corsAndSecurityHeadersShouldBeApplied() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .bodyValue(Usuario.builder().build())
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



    @Test
    void corsAndSecurityHeadersShouldBeAppliedOnGetAll() {
        webTestClient.get()
                .uri("/api/v1/usuarios")
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

    @Test
    void corsAndSecurityHeadersShouldBeAppliedOnGetByDocumento() {
        webTestClient.get()
                .uri("/api/v1/usuarios/12345")
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