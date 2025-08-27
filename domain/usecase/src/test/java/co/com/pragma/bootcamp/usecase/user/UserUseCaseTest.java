package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
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

import static co.com.pragma.bootcamp.usecase.helper.UserErrors.DOCUMENT_ALREADY_REGISTERED;
import static co.com.pragma.bootcamp.usecase.helper.UserErrors.EMAIL_ALREADY_REGISTERED;
import static co.com.pragma.bootcamp.usecase.helper.UserErrors.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
                .identificationDocument("12345")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                .baseSalary(BigDecimal.valueOf(5000000))
                .build();
    }

    @Test
    void registerUserCuandoEsValido() {
        when(userRepository.findByIdentificationDocument(validUser.getIdentificationDocument()))
                .thenReturn(Mono.empty());
        when(userRepository.existsByEmail(validUser.getEmail()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(validUser))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.registerUser(validUser))
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).save(validUser);
    }

    @Test
    void registerUser_debeFallarCuandoCorreoYaExiste() {
        when(userRepository.findByIdentificationDocument(validUser.getIdentificationDocument()))
                .thenReturn(Mono.empty());
        when(userRepository.existsByEmail(validUser.getEmail()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(validUser))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(EMAIL_ALREADY_REGISTERED.getMessage());
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_debeFallarCuandoDocumentoYaExiste() {
        when(userRepository.findByIdentificationDocument(validUser.getIdentificationDocument()))
                .thenReturn(Mono.just(validUser));

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.registerUser(validUser))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(DOCUMENT_ALREADY_REGISTERED.getMessage());
                })
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void listUsersCuandoExisten() {
        when(userRepository.findAll()).thenReturn(Flux.just(validUser));

        StepVerifier.create(userUseCase.listUsers())
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void listUsers() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userUseCase.listUsers())
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarUsuarioCuandoExiste() {
        when(userRepository.findByIdentificationDocument("12345"))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.getUserByDocument("12345"))
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findByIdentificationDocument("12345");
    }

    @Test
    void getUserByDocument_debeFallarCuandoNoExiste() {
        when(userRepository.findByIdentificationDocument("12345"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByDocument("12345"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof BusinessException;
                    assert error.getMessage().equals(USER_NOT_FOUND.getMessage());
                })
                .verify();

        verify(userRepository).findByIdentificationDocument("12345");
    }
}
