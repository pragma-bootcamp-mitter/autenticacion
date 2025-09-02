package co.com.pragma.bootcamp.api.web.user;

import co.com.pragma.bootcamp.api.config.GlobalExceptionHandler;
import co.com.pragma.bootcamp.api.dto.user.UserRequest;
import co.com.pragma.bootcamp.api.dto.user.UserResponse;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_CODE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_MESSAGE;
import static co.com.pragma.bootcamp.api.util.AuthConstants.SUCCESS_TITLE;
import static co.com.pragma.bootcamp.model.exceptions.UserErrors.DOCUMENT_OR_EMAIL_ALREADY_REGISTERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {UserRouter.class, UserHandler.class, ValidatorUtil.class, GlobalExceptionHandler.class})
class UserRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private Validator validator;

    private User expectedUser;
    private UserResponse expectedResponse;

    @BeforeEach
    void setup() {
        expectedUser = User.builder()
                .id("12345")
                .identificationDocument("12345")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("Calle Falsa 123")
                .phoneNumber("3001234567")
                .email("john.doe@test.com")
                .baseSalary(new BigDecimal("50000.00"))
                .build();

        expectedResponse = new UserResponse();
        expectedResponse.setId("12345");
        expectedResponse.setIdentificationDocument("12345");
        expectedResponse.setFirstName("John");
        expectedResponse.setLastName("Doe");
        expectedResponse.setDateOfBirth(LocalDate.of(1990, 5, 15));
        expectedResponse.setAddress("Calle Falsa 123");
        expectedResponse.setPhoneNumber("3001234567");
        expectedResponse.setEmail("john.doe@test.com");
        expectedResponse.setBaseSalary(new BigDecimal("50000.00"));
    }

    @Test
    void registerUser_shouldReturnCreatedUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setIdentificationDocument("12345");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        userRequest.setAddress("Calle Falsa 123");
        userRequest.setPhoneNumber("3001234567");
        userRequest.setEmail("john.doe@test.com");
        userRequest.setBaseSalary(new BigDecimal("50000.00"));

        when(userMapper.toDomain(any(UserRequest.class))).thenReturn(expectedUser);
        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(expectedUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(SUCCESS_CODE)
                .jsonPath("$.message").isEqualTo(SUCCESS_MESSAGE)
                .jsonPath("$.title").isEqualTo(SUCCESS_TITLE)
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.firstName").isEqualTo("John");
    }

    @Test
    void listUsers_shouldReturnListOfUsers() {
        when(userUseCase.listUsers()).thenReturn(Flux.just(expectedUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        webTestClient.get()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(SUCCESS_CODE)
                .jsonPath("$.message").isEqualTo(SUCCESS_MESSAGE)
                .jsonPath("$.title").isEqualTo(SUCCESS_TITLE)
                .jsonPath("$.data[0].id").isEqualTo("12345")
                .jsonPath("$.data[0].firstName").isEqualTo("John");
    }



    @Test
    void registerUser_shouldReturnConflict_whenUserAlreadyExists() {
        UserRequest userRequest = new UserRequest();
        userRequest.setIdentificationDocument("12345");
        userRequest.setFirstName("Juan");
        userRequest.setLastName("Pérez");
        userRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userRequest.setAddress("Calle 123");
        userRequest.setPhoneNumber("3001234567");
        userRequest.setEmail("juan@test.com");
        userRequest.setBaseSalary(BigDecimal.valueOf(1000));

        when(userMapper.toDomain(any(UserRequest.class))).thenReturn(expectedUser);
        when(userUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.error(new BusinessException(DOCUMENT_OR_EMAIL_ALREADY_REGISTERED)));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.code").isEqualTo("BR_409_CONFLICT")
                .jsonPath("$.message").isEqualTo("The provided identification document or email address is already in use")
                .jsonPath("$.title").isEqualTo("Conflict with existing data")
                .jsonPath("$.data").doesNotExist()
                .jsonPath("$.errors").doesNotExist();
    }

    @Test
    void getUserByDocument_shouldReturnUser_whenUserExists() {
        String document = "12345";
        when(userUseCase.getUserByDocument(anyString())).thenReturn(Mono.just(expectedUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        webTestClient.get()
                .uri("/api/v1/users/{document}", document)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(SUCCESS_CODE)
                .jsonPath("$.message").isEqualTo(SUCCESS_MESSAGE)
                .jsonPath("$.title").isEqualTo(SUCCESS_TITLE)
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.firstName").isEqualTo("John");
    }

    @Test
    void getUserByDocument_shouldReturnNotFound_whenUserDoesNotExist() {
        String document = "99999";
        when(userUseCase.getUserByDocument(anyString())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/users/{document}", document)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("BR_404_NOT_FOUND")
                .jsonPath("$.message").isEqualTo("User not found")
                .jsonPath("$.title").isEqualTo("Resource not found")
                .jsonPath("$.data").doesNotExist()
                .jsonPath("$.errors").doesNotExist();
    }
}
