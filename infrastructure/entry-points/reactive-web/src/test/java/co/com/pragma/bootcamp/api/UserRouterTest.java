package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@ContextConfiguration(classes = {UserRouter.class, UserHandler.class, UserUseCase.class, UserDtoMapper.class})
//@WebFluxTest
class UserRouterTest {

    //@Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserDtoMapper userDtoMapper;

    private User expectedUser() {
        return new User(
                "12345",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "",
                "",
                "",
                new BigDecimal("50000.00")
        );
    }

    //@BeforeEach
    void setup() {
        when(userUseCase.registrarUsuario(any(User.class)))
                .thenReturn(Mono.just(expectedUser()));
    }



    //@Test
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
                .expectBody(String.class)
                .value(userResponse -> Assertions.assertThat(userResponse).isNotNull());
    }
}
