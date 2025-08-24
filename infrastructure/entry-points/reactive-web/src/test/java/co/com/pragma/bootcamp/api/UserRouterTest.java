package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserRouter.class, UserHandler.class})
@WebFluxTest
class UserRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    private User expectedUser() {
        return new User(
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
    }

    @BeforeEach
    void setup() {
        User domainUser = expectedUser();

        when(userDtoMapper.toDomain(any()))
                .thenReturn(domainUser);

        when(userUseCase.registrarUsuario(any(User.class)))
                .thenReturn(Mono.just(domainUser));

        when(userDtoMapper.toResponse(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    UserResponse response = new UserResponse();
                    response.setId(u.getId());
                    response.setNombres(u.getNombres());
                    response.setApellidos(u.getApellidos());
                    response.setFechaNacimiento(u.getFechaNacimiento());
                    response.setSalarioBase(u.getSalarioBase());
                    return response;
                });
    }



    @Test
    void testListenPOSTUseCase() {
        Map<String, Object> body = new HashMap<>();
        body.put("nombres", "John");
        body.put("apellidos", "Doe");
        body.put("fechaNacimiento", "1990-05-15");
        body.put("salarioBase", "50000.00");

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(userResponse -> {
                    Assertions.assertThat(userResponse.getId()).isEqualTo("12345");
                    Assertions.assertThat(userResponse.getNombres()).isEqualTo("John");
                });
    }
}
