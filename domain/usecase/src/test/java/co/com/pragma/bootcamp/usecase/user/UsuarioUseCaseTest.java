package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.usecase.helper.UserError;
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
class UsuarioUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

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
        when(userRepository.existePorCorreoElectronico(validUsuario.getCorreoElectronico()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(validUsuario))
                .thenReturn(Mono.just(validUsuario));

        StepVerifier.create(userUseCase.registrarUsuario(validUsuario))
                .expectNext(validUsuario)
                .verifyComplete();

        verify(userRepository).save(validUsuario);
    }

    @Test
    void registrarUsuario_debeFallarCuandoCorreoYaExiste() {
        when(userRepository.existePorCorreoElectronico(validUsuario.getCorreoElectronico()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registrarUsuario(validUsuario))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(UserError.EMAIL_ALREADY_EXISTS.getMessage());
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void listarUsuarios_debeRetornarUsuariosCuandoExisten() {
        when(userRepository.findAll()).thenReturn(Flux.just(validUsuario));

        StepVerifier.create(userUseCase.listarUsuarios())
                .expectNext(validUsuario)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void listarUsuarios_debeRetornarVacioCuandoNoExistenUsuarios() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userUseCase.listarUsuarios())
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarUsuarioCuandoExiste() {
        when(userRepository.buscarPorDocumentoIdentidad("12345"))
                .thenReturn(Mono.just(validUsuario));

        StepVerifier.create(userUseCase.obtenerUsuarioPorDocumento("12345"))
                .expectNext(validUsuario)
                .verifyComplete();

        verify(userRepository).buscarPorDocumentoIdentidad("12345");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeFallarCuandoNoExiste() {
        when(userRepository.buscarPorDocumentoIdentidad("12345"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.obtenerUsuarioPorDocumento("12345"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(UserError.USER_NOT_FOUND.getMessage());
                })
                .verify();

        verify(userRepository).buscarPorDocumentoIdentidad("12345");
    }

    @Test
    void registrarUsuario_debeFallarCuandoSalarioEsMayorAlMaximo() {
        Usuario usuarioConSalarioAlto = validUsuario.toBuilder()
                .salarioBase(BigDecimal.valueOf(16_000_000))
                .build();

        StepVerifier.create(userUseCase.registrarUsuario(usuarioConSalarioAlto))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals("El salario base no puede superar 15000000");
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void registrarUsuario_debeFallarCuandoSalarioEsNegativo() {
        Usuario usuarioConSalarioNegativo = validUsuario.toBuilder()
                .salarioBase(BigDecimal.valueOf(-5000))
                .build();

        StepVerifier.create(userUseCase.registrarUsuario(usuarioConSalarioNegativo))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals("El salario base no puede ser negativo");
                })
                .verify();

        verify(userRepository, never()).save(any());
    }
}
