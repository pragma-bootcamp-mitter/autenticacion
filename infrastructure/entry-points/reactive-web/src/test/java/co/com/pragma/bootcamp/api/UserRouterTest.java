package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ContextConfiguration(classes = {UserRouter.class, Handler.class})
@WebFluxTest
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

    @BeforeEach
    void setup() {
        expectedUser = new User(
                "12345",
                "12345",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "Calle Falsa 123",
                "3001234567",
                "john.doe@test.com",
                new BigDecimal("50000.00")
        );

        when(userMapper.toDomain(any()))
                .thenReturn(expectedUser);

        when(userUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.just(expectedUser));

        when(userMapper.toResponse(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    UserResponse response = new UserResponse();
                    response.setId(u.getId());
                    response.setFirstName(u.getFirstName());
                    response.setLastName(u.getLastName());
                    response.setDateOfBirth(u.getDateOfBirth());
                    response.setBaseSalary(u.getBaseSalary());
                    return response;
                });

        when(validator.validate(any())).thenReturn(Collections.emptySet());
    }

    @Test
    void registerUser_shouldReturnCreatedUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "John");
        body.put("lastName", "Doe");
        body.put("dateOfBirth", "1990-05-15");
        body.put("baseSalary", new BigDecimal("50000.00"));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("User created successfully")
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.firstName").isEqualTo("John");
    }
    @Test
    void listUsers_shouldReturnListOfUsers() {
        when(userUseCase.listUsers()).thenReturn(Flux.just(expectedUser));

        webTestClient.get()
                .uri("/api/v1/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Users retrieved successfully")
                .jsonPath("$.data[0].id").isEqualTo("12345")
                .jsonPath("$.data[0].firstName").isEqualTo("John");
    }

    @Test
    void getUserByDocument_shouldReturnUserWhenExists() {
        String document = "12345";
        when(userUseCase.getUserByDocument(document)).thenReturn(Mono.just(expectedUser));

        webTestClient.get()
                .uri("/api/v1/users/{document}", document)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("User found")
                .jsonPath("$.data.id").isEqualTo("12345")
                .jsonPath("$.data.firstName").isEqualTo("John");
    }

    @Test
    void obtenerUsuarioPorDocumento_debeRetornarNotFoundCuandoNoExiste() {
        String documento = "99999";
        when(userUseCase.getUserByDocument(documento)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/users/{document}", documento)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
