package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.login.gateways.PasswordGateway;
import co.com.pragma.bootcamp.model.rol.Role;
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

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.DOCUMENT_OR_EMAIL_ALREADY_REGISTERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordGateway passwordGateway;

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
                .password("password123")
                .roleId(1)
                .build();
    }

    @Test
    void registerUser_shouldRegisterUser_whenUserDoesNotExist() {
        when(userRepository.existsByEmailOrIdentificationDocument(anyString(), anyString()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(validUser));

        Mono<User> result = userUseCase.registerUser(validUser);

        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowException_whenUserAlreadyExists() {
        when(userRepository.existsByEmailOrIdentificationDocument(anyString(), anyString()))
                .thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.registerUser(validUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getUserError().equals(DOCUMENT_OR_EMAIL_ALREADY_REGISTERED)
                )
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void listUsers_shouldReturnUsers_whenTheyExist() {
        when(userRepository.findAll()).thenReturn(Flux.just(validUser));

        Flux<User> result = userUseCase.listUsers();

        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void listUsers_shouldReturnEmptyFlux_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        Flux<User> result = userUseCase.listUsers();

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void getUserByDocument_shouldReturnUser_whenUserExists() {
        String document = "12345";
        when(userRepository.findByIdentificationDocument(document))
                .thenReturn(Mono.just(validUser));

        Mono<User> result = userUseCase.getUserByDocument(document);

        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findByIdentificationDocument(document);
    }

    @Test
    void getUserByDocument_shouldReturnEmptyMono_whenUserDoesNotExist() {
        String document = "99999";
        when(userRepository.findByIdentificationDocument(document))
                .thenReturn(Mono.empty());

        Mono<User> result = userUseCase.getUserByDocument(document);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository).findByIdentificationDocument(document);
    }
}
