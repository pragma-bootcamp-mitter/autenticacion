package co.com.pragma.bootcamp.api;


import co.com.pragma.bootcamp.api.dto.RespuestaUsuario;
import co.com.pragma.bootcamp.api.dto.SolicitudUsuario;
import co.com.pragma.bootcamp.api.mapper.MapeadorUsuarioDto;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.usecase.user.UsuarioCasoDeUso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsuarioHandlerTest {

    @InjectMocks
    private UsuarioHandler usuarioHandler;

    @Mock
    private UsuarioCasoDeUso usuarioCasoDeUso;

    @Mock
    private MapeadorUsuarioDto mapeadorUsuarioDto;

    @Mock
    private ServerRequest serverRequest;

    private Usuario usuario;
    private SolicitudUsuario solicitudUsuario;
    private RespuestaUsuario respuestaUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        solicitudUsuario = new SolicitudUsuario();
        solicitudUsuario.setDocumentoIdentidad("12345");
        solicitudUsuario.setNombres("Juan");
        solicitudUsuario.setApellidos("Pérez");
        solicitudUsuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        solicitudUsuario.setDireccion("Calle 123");
        solicitudUsuario.setTelefono("3001234567");
        solicitudUsuario.setCorreoElectronico("juan@test.com");
        solicitudUsuario.setSalarioBase(BigDecimal.valueOf(1000));

        usuario = Usuario.builder()
                .id("1")
                .documentoIdentidad("12345")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("Calle 123")
                .telefono("3001234567")
                .correoElectronico("juan@test.com")
                .salarioBase(BigDecimal.valueOf(1000))
                .build();

        respuestaUsuario = new RespuestaUsuario();
        respuestaUsuario.setId("1");
        respuestaUsuario.setDocumentoIdentidad("12345");
        respuestaUsuario.setNombres("Juan");
        respuestaUsuario.setApellidos("Pérez");
        respuestaUsuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        respuestaUsuario.setDireccion("Calle 123");
        respuestaUsuario.setTelefono("3001234567");
        respuestaUsuario.setCorreoElectronico("juan@test.com");
        respuestaUsuario.setSalarioBase(BigDecimal.valueOf(1000));
    }


    @Test
    void registrarUsuario_exito() {
        when(serverRequest.bodyToMono(SolicitudUsuario.class)).thenReturn(Mono.just(solicitudUsuario));
        when(mapeadorUsuarioDto.aDominio(solicitudUsuario)).thenReturn(usuario);
        when(usuarioCasoDeUso.registrarUsuario(usuario)).thenReturn(Mono.just(usuario));
        when(mapeadorUsuarioDto.aRespuesta(usuario)).thenReturn(respuestaUsuario);

        Mono<ServerResponse> responseMono = usuarioHandler.registrarUsuario(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(usuarioCasoDeUso).registrarUsuario(usuario);
    }

    @Test
    void listarUsuarios_exito() {
        when(usuarioCasoDeUso.listarUsuarios()).thenReturn(Flux.just(usuario));
        when(mapeadorUsuarioDto.aRespuesta(usuario)).thenReturn(respuestaUsuario);

        Mono<ServerResponse> responseMono = usuarioHandler.listarUsuarios(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(usuarioCasoDeUso).listarUsuarios();
    }

    @Test
    void obtenerUsuarioPorDocumento_exito() {
        when(serverRequest.pathVariable("documento")).thenReturn("12345");
        when(usuarioCasoDeUso.obtenerUsuarioPorDocumento("12345")).thenReturn(Mono.just(usuario));
        when(mapeadorUsuarioDto.aRespuesta(usuario)).thenReturn(respuestaUsuario);

        Mono<ServerResponse> responseMono = usuarioHandler.obtenerUsuarioPorDocumento(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(usuarioCasoDeUso).obtenerUsuarioPorDocumento("12345");
    }
}
