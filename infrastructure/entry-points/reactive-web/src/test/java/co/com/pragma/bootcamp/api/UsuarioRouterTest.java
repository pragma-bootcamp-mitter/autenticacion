package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
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

@ContextConfiguration(classes = {UserRouter.class, UserHandler.class})
@WebFluxTest
class UsuarioRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

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

        when(userDtoMapper.toDomain(any()))
                .thenReturn(expectedUsuario);

        when(userUseCase.registrarUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(expectedUsuario));

        when(userDtoMapper.toResponse(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    UserResponse response = new UserResponse();
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
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Usuario creado exitosamente")
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.nombres").isEqualTo("John");
    }

    @Test
    void listarUsuarios_debeRetornarListaDeUsuarios() {
        when(userUseCase.listarUsuarios()).thenReturn(Flux.just(expectedUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Usuarios obtenidos correctamente")
                .jsonPath("$.data[0].id").isEqualTo("12345")
                .jsonPath("$.data[0].nombres").isEqualTo("John");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarUsuarioCuandoExiste() {
        String documento = "12345";
        when(userUseCase.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.just(expectedUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Usuario encontrado")
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.nombres").isEqualTo("John");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarNotFoundCuandoNoExiste() {
        String documento = "99999";
        when(userUseCase.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/usuarios/{documento}", documento)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
