package co.com.pragma.bootcamp.api.web.user;

import co.com.pragma.bootcamp.api.dto.user.UserRequest;
import co.com.pragma.bootcamp.api.dto.user.UserResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.DOCUMENT_OR_EMAIL_ALREADY_REGISTERED;
import static co.com.pragma.bootcamp.model.exceptions.UserErrors.USER_NOT_FOUND;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @InjectMocks
    private UserHandler userHandler;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private ValidatorUtil validatorUtil;

    private User testUserDomain;
    private UserRequest testUserRequest;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {

        testUserRequest = new UserRequest();
        testUserRequest.setIdentificationDocument("12345");
        testUserRequest.setFirstName("Juan");
        testUserRequest.setLastName("Pérez");
        testUserRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUserRequest.setAddress("Calle 123");
        testUserRequest.setPhoneNumber("3001234567");
        testUserRequest.setEmail("juan@test.com");
        testUserRequest.setBaseSalary(BigDecimal.valueOf(1000));

        testUserDomain = User.builder()
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

        testUserResponse = new UserResponse();
        testUserResponse.setId("1");
        testUserResponse.setIdentificationDocument("12345");
        testUserResponse.setFirstName("Juan");
        testUserResponse.setLastName("Pérez");
        testUserResponse.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUserResponse.setAddress("Calle 123");
        testUserResponse.setPhoneNumber("3001234567");
        testUserResponse.setEmail("juan@test.com");
        testUserResponse.setBaseSalary(BigDecimal.valueOf(1000));

    }


    @Test
    void registerUser_shouldReturnCreated_whenSuccessful() {
        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(testUserRequest));
        when(validatorUtil.validate(testUserRequest)).thenReturn(Mono.just(testUserRequest));
        when(userMapper.toDomain(testUserRequest)).thenReturn(testUserDomain);
        when(userUseCase.registerUser(testUserDomain)).thenReturn(Mono.just(testUserDomain));
        when(userMapper.toResponse(testUserDomain)).thenReturn(testUserResponse);

        Mono<ServerResponse> responseMono = userHandler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.CREATED)
                )
                .verifyComplete();
    }

    @Test
    void registerUser_shouldReturnConflict_whenUserAlreadyExists() {
        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(testUserRequest));
        when(validatorUtil.validate(testUserRequest)).thenReturn(Mono.just(testUserRequest));
        when(userMapper.toDomain(testUserRequest)).thenReturn(testUserDomain);
        when(userUseCase.registerUser(testUserDomain))
                .thenReturn(Mono.error(new BusinessException(DOCUMENT_OR_EMAIL_ALREADY_REGISTERED)));

        Mono<ServerResponse> responseMono = userHandler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getUserError().equals(DOCUMENT_OR_EMAIL_ALREADY_REGISTERED)
                )
                .verify();
    }


    @Test
    void registerUser_shouldReturnBadRequest_whenValidationFails() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UserRequest invalidUserRequest = new UserRequest();
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(invalidUserRequest);

        ConstraintViolationException validationException = new ConstraintViolationException(violations);

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(invalidUserRequest));
        when(validatorUtil.validate(invalidUserRequest)).thenReturn(Mono.error(validationException));

        Mono<ServerResponse> responseMono = userHandler.registerUser(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(ConstraintViolationException.class::isInstance
                )
                .verify();
    }

    @Test
    void listUsers_shouldReturnOk_whenUsersExist() {
        when(userUseCase.listUsers()).thenReturn(Flux.just(testUserDomain));
        when(userMapper.toResponse(testUserDomain)).thenReturn(testUserResponse);

        Mono<ServerResponse> responseMono = userHandler.listUsers(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }

    @Test
    void listUsers_shouldReturnOk_whenNoUsersExist() {
        when(userUseCase.listUsers()).thenReturn(Flux.empty());

        Mono<ServerResponse> responseMono = userHandler.listUsers(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }

    @Test
    void getUserByDocument_shouldReturnOk_whenUserExists() {
        String document = "12345";
        when(serverRequest.pathVariable("document")).thenReturn(document);
        when(userUseCase.getUserByDocument(document)).thenReturn(Mono.just(testUserDomain));
        when(userMapper.toResponse(testUserDomain)).thenReturn(testUserResponse);

        Mono<ServerResponse> responseMono = userHandler.getUserByDocument(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.OK)
                )
                .verifyComplete();
    }

    @Test
    void getUserByDocument_shouldReturnNotFound_whenUserDoesNotExist() {
        String document = "99999";
        when(serverRequest.pathVariable("document")).thenReturn(document);
        when(userUseCase.getUserByDocument(document)).thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = userHandler.getUserByDocument(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getUserError().equals(USER_NOT_FOUND)
                )
                .verify();
    }
}
