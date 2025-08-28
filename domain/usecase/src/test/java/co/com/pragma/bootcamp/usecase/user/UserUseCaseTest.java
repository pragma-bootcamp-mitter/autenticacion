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
    void registerUser_shouldRegisterUser_whenUserDoesNotExist() {
        // Given
        when(userRepository.existsByEmailOrIdentificationDocument(anyString(), anyString()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(validUser));

        // When
        Mono<User> result = userUseCase.registerUser(validUser);

        // Then
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowException_whenUserAlreadyExists() {
        // Given
        when(userRepository.existsByEmailOrIdentificationDocument(anyString(), anyString()))
                .thenReturn(Mono.just(true));

        // When
        Mono<User> result = userUseCase.registerUser(validUser);

        // Then
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
        // Given
        when(userRepository.findAll()).thenReturn(Flux.just(validUser));

        // When
        Flux<User> result = userUseCase.listUsers();

        // Then
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void listUsers_shouldReturnEmptyFlux_whenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<User> result = userUseCase.listUsers();

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository).findAll();
    }
}
