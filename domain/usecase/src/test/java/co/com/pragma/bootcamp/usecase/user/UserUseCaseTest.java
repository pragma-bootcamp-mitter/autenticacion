package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.usecase.helper.UserError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
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
        when(userRepository.findByCorreoElectronico(validUser.getCorreoElectronico()))
                .thenReturn(Mono.empty());
        when(userRepository.save(validUser))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.registrarUsuario(validUser))
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).save(validUser);
    }

    @Test
    void registrarUsuario_debeFallarCuandoCorreoYaExiste() {
        when(userRepository.findByCorreoElectronico(validUser.getCorreoElectronico()))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.registrarUsuario(validUser))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert ((BusinessException) error).getMessage()
                            .equals(UserError.EMAIL_ALREADY_EXISTS.getMessage());
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void registrarUsuario_debeFallarCuandoUsuarioInvalido() {
        User invalidUser = User.builder()
                .nombres("")
                .apellidos("Pérez")
                .correoElectronico("correo-malo")
                .salarioBase(BigDecimal.valueOf(-1000))
                .build();

        StepVerifier.create(userUseCase.registrarUsuario(invalidUser))
                .expectError(BusinessException.class)
                .verify();

        verify(userRepository, never()).save(any());
    }
}
