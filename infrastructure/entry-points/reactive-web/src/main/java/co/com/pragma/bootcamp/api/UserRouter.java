package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.http.MediaType;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    private static final String BASE_PATH = "/api/v1/usuarios";

    @Bean
    public RouterFunction<?> userRoutes(UserHandler handler) {
        return route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::registrarUsuario)
                .andRoute(GET(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::listarUsuarios)
                .andRoute(GET(BASE_PATH + "/{documento}").and(accept(MediaType.APPLICATION_JSON)), handler::obtenerUsuarioPorDocumento);
    }
}