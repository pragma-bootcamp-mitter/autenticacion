package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.Validator;

import static co.com.pragma.bootcamp.usecase.helper.UserErrors.DOCUMENT_ALREADY_REGISTERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HandlerTest {

    @InjectMocks
    private Handler handler;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private Validator validator;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRequest = new UserRequest();
        userRequest.setIdentificationDocument("12345");
        userRequest.setFirstName("Juan");
        userRequest.setLastName("Pérez");
        userRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userRequest.setAddress("Calle 123");
        userRequest.setPhoneNumber("3001234567");
        userRequest.setEmail("juan@test.com");
        userRequest.setBaseSalary(BigDecimal.valueOf(1000));

        user = User.builder()
                .id("1")
                .identificationDocument("12345")
                .firstName("Juan")
                .lastName("Pérez")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@test.com")
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        userResponse = new UserResponse();
        userResponse.setId("1");
        userResponse.setIdentificationDocument("12345");
        userResponse.setFirstName("Juan");
        userResponse.setLastName("Pérez");
        userResponse.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userResponse.setAddress("Calle 123");
        userResponse.setPhoneNumber("3001234567");
        userResponse.setEmail("juan@test.com");
        userResponse.setBaseSalary(BigDecimal.valueOf(1000));

        validator = Mockito.mock(Validator.class);
        when(validator.validate(userRequest)).thenReturn(Collections.emptySet());
        handler = new Handler(userUseCase, userMapper, validator);
    }


    @Test
    void registerUser_success() {
        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(userMapper.toDomain(userRequest)).thenReturn(user);
        when(userUseCase.registerUser(user)).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Mono<ServerResponse> responseMono = handler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(userUseCase).registerUser(user);
    }

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_validationError() {
        ConstraintViolation<UserRequest> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Invalid email");

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(Set.of(violation));

        Mono<ServerResponse> responseMono = handler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assert response.statusCode().is4xxClientError();
                })
                .verifyComplete();

        verify(userUseCase, never()).registerUser(any());
    }

    @Test
    void listUsers_success() {
        when(userUseCase.listUsers()).thenReturn(Flux.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Mono<ServerResponse> responseMono = handler.listUsers(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(userUseCase).listUsers();
    }

    @Test
    void getUserByDocument_success() {
        when(serverRequest.pathVariable("document")).thenReturn("12345");
        when(userUseCase.getUserByDocument("12345")).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Mono<ServerResponse> responseMono = handler.getUserByDocument(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(userUseCase).getUserByDocument("12345");
    }

    @Test
    void registerUser_errorBusinessException() {
        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(Collections.emptySet());
        when(userMapper.toDomain(userRequest)).thenReturn(user);

        when(userUseCase.registerUser(user))
                .thenReturn(Mono.error(new BusinessException(DOCUMENT_ALREADY_REGISTERED.getMessage())));

        Mono<ServerResponse> responseMono = handler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assert response.statusCode().is4xxClientError();
                    assert response.statusCode().value() == 409; // CONFLICT
                })
                .verifyComplete();
    }
}
