package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UsuarioHandler;
import co.com.pragma.bootcamp.api.UsuarioRouter;
import co.com.pragma.bootcamp.api.dto.RespuestaUsuario;
import co.com.pragma.bootcamp.api.dto.SolicitudUsuario;
import co.com.pragma.bootcamp.api.mapper.MapeadorUsuarioDto;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.usecase.user.UsuarioCasoDeUso;
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

@ContextConfiguration(classes = {UsuarioRouter.class, UsuarioHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioCasoDeUso usuarioCasoDeUso;

    @MockitoBean
    private MapeadorUsuarioDto mapeadorUsuarioDto;

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

        when(usuarioCasoDeUso.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(sampleUsuario));

        when(usuarioCasoDeUso.listarUsuarios())
                .thenReturn(Flux.fromIterable(List.of(sampleUsuario)));

        when(usuarioCasoDeUso.obtenerUsuarioPorDocumento("12345"))
                .thenReturn(Mono.just(sampleUsuario));

        when(mapeadorUsuarioDto.aDominio(any(SolicitudUsuario.class)))
                .thenAnswer(invocation -> sampleUsuario);

        when(mapeadorUsuarioDto.aRespuesta(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    RespuestaUsuario resp = new RespuestaUsuario();
                    resp.setId(u.getId());
                    resp.setNombres(u.getNombres());
                    resp.setApellidos(u.getApellidos());
                    resp.setFechaNacimiento(u.getFechaNacimiento());
                    resp.setSalarioBase(u.getSalarioBase());
                    return resp;
                });

    }

    @Test
    void corsYEncabezadosDeSeguridadSeAplicanEnPost() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .bodyValue(Usuario.builder().build())
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



    @Test
    void corsYEncabezadosDeSeguridadSeAplicanEnGetTodos() {
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
    void corsYEncabezadosDeSeguridadSeAplicanEnGetPorDocumento() {
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