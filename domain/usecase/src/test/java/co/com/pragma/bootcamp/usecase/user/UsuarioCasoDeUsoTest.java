package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioUsuario;
import co.com.pragma.bootcamp.usecase.helper.ErroresUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioCasoDeUsoTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private UsuarioCasoDeUso usuarioCasoDeUso;

    private Usuario validUsuario;

    @BeforeEach
    void setUp() {
        validUsuario = Usuario.builder()
                .id("1")
                .documentoIdentidad("12345")
                .nombres("Juan")
                .apellidos("Pérez")
                .correoElectronico("juan@example.com")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();
    }

    @Test
    void registrarUsuario_debeGuardarUsuarioCuandoEsValido() {
        when(repositorioUsuario.existePorCorreoElectronico(validUsuario.getCorreoElectronico()))
                .thenReturn(Mono.just(false));
        when(repositorioUsuario.save(validUsuario))
                .thenReturn(Mono.just(validUsuario));

        StepVerifier.create(usuarioCasoDeUso.registrarUsuario(validUsuario))
                .expectNext(validUsuario)
                .verifyComplete();

        verify(repositorioUsuario).save(validUsuario);
    }

    @Test
    void registrarUsuario_debeFallarCuandoCorreoYaExiste() {
        when(repositorioUsuario.existePorCorreoElectronico(validUsuario.getCorreoElectronico()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(usuarioCasoDeUso.registrarUsuario(validUsuario))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(ErroresUsuario.CORREO_YA_REGISTRADO.getMensaje());
                })
                .verify();

        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void listarUsuarios_debeRetornarUsuariosCuandoExisten() {
        when(repositorioUsuario.findAll()).thenReturn(Flux.just(validUsuario));

        StepVerifier.create(usuarioCasoDeUso.listarUsuarios())
                .expectNext(validUsuario)
                .verifyComplete();

        verify(repositorioUsuario).findAll();
    }

    @Test
    void listarUsuarios_debeRetornarVacioCuandoNoExistenUsuarios() {
        when(repositorioUsuario.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(usuarioCasoDeUso.listarUsuarios())
                .verifyComplete();

        verify(repositorioUsuario).findAll();
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarUsuarioCuandoExiste() {
        when(repositorioUsuario.buscarPorDocumentoIdentidad("12345"))
                .thenReturn(Mono.just(validUsuario));

        StepVerifier.create(usuarioCasoDeUso.obtenerUsuarioPorDocumento("12345"))
                .expectNext(validUsuario)
                .verifyComplete();

        verify(repositorioUsuario).buscarPorDocumentoIdentidad("12345");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeFallarCuandoNoExiste() {
        when(repositorioUsuario.buscarPorDocumentoIdentidad("12345"))
                .thenReturn(Mono.empty());

        StepVerifier.create(usuarioCasoDeUso.obtenerUsuarioPorDocumento("12345"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(ErroresUsuario.USUARIO_NO_ENCONTRADO.getMensaje());
                })
                .verify();

        verify(repositorioUsuario).buscarPorDocumentoIdentidad("12345");
    }

    @Test
    void registrarUsuario_debeFallarCuandoSalarioEsMayorAlMaximo() {
        Usuario usuarioConSalarioAlto = validUsuario.toBuilder()
                .salarioBase(BigDecimal.valueOf(16_000_000))
                .build();

        StepVerifier.create(usuarioCasoDeUso.registrarUsuario(usuarioConSalarioAlto))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals("El salario base no puede superar 15000000");
                })
                .verify();

        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void registrarUsuario_debeFallarCuandoSalarioEsNegativo() {
        Usuario usuarioConSalarioNegativo = validUsuario.toBuilder()
                .salarioBase(BigDecimal.valueOf(-5000))
                .build();

        StepVerifier.create(usuarioCasoDeUso.registrarUsuario(usuarioConSalarioNegativo))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals("El salario base no puede ser negativo");
                })
                .verify();

        verify(repositorioUsuario, never()).save(any());
    }
}
