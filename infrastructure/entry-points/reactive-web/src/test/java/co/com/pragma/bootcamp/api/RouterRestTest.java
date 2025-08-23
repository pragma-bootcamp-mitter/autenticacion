package co.com.pragma.bootcamp.api;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;

    @BeforeEach
    void setup() {
        when(userUseCase.registrarUsuario(any(User.class)))
                .thenReturn(Mono.just(new User(
                        "12345",
                        "John",
                        "Doe",
                        LocalDate.of(1990, 5, 15),
                        "",
                        "",
                        "",
                        new BigDecimal("50000.00"))));
    }

    @Test
    void testListenPOSTUseCase() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new User())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isNotNull();
                        }
                );
    }
}
