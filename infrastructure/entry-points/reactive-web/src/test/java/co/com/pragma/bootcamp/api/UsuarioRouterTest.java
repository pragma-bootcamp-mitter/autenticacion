package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.RespuestaUsuario;
import co.com.pragma.bootcamp.api.mapper.MapeadorUsuarioDto;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.usecase.user.UsuarioCasoDeUso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UsuarioRouter.class, UsuarioHandler.class})
@WebFluxTest
class UsuarioRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioCasoDeUso usuarioCasoDeUso;

    @MockitoBean
    private MapeadorUsuarioDto mapeadorUsuarioDto;

    private Usuario expectedUsuario;

    @BeforeEach
    void setup() {
        expectedUsuario = new Usuario(
                "12345",
                "12345",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "Calle Falsa 123",
                "3001234567",
                "john.doe@test.com",
                new BigDecimal("50000.00")
        );

        when(mapeadorUsuarioDto.aDominio(any()))
                .thenReturn(expectedUsuario);

        when(usuarioCasoDeUso.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(expectedUsuario));

        when(mapeadorUsuarioDto.aRespuesta(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    RespuestaUsuario response = new RespuestaUsuario();
                    response.setId(u.getId());
                    response.setNombres(u.getNombres());
                    response.setApellidos(u.getApellidos());
                    response.setFechaNacimiento(u.getFechaNacimiento());
                    response.setSalarioBase(u.getSalarioBase());
                    return response;
                });
    }

    @Test
    void registrarUsuario_debeRetornarUsuarioCreado() {
        Map<String, Object> body = new HashMap<>();
        body.put("nombres", "John");
        body.put("apellidos", "Doe");
        body.put("fechaNacimiento", "1990-05-15");
        body.put("salarioBase", "50000.00");

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.exito").isEqualTo(true)
                .jsonPath("$.mensaje").isEqualTo("Usuario creado exitosamente")
                .jsonPath("$.datos.id").isEqualTo("12345")
                .jsonPath("$.datos.nombres").isEqualTo("John");
    }

    @Test
    void listarUsuarios_debeRetornarListaDeUsuarios() {
        when(usuarioCasoDeUso.listarUsuarios()).thenReturn(Flux.just(expectedUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.exito").isEqualTo(true)
                .jsonPath("$.mensaje").isEqualTo("Usuarios obtenidos correctamente")
                .jsonPath("$.datos[0].id").isEqualTo("12345")
                .jsonPath("$.datos[0].nombres").isEqualTo("John");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarUsuarioCuandoExiste() {
        String documento = "12345";
        when(usuarioCasoDeUso.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.just(expectedUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.exito").isEqualTo(true)
                .jsonPath("$.mensaje").isEqualTo("Usuario encontrado")
                .jsonPath("$.datos.id").isEqualTo("12345")
                .jsonPath("$.datos.nombres").isEqualTo("John");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarNotFoundCuandoNoExiste() {
        String documento = "99999";
        when(usuarioCasoDeUso.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
