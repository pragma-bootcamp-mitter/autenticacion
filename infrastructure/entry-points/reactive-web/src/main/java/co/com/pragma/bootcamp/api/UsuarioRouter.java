package co.com.pragma.bootcamp.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UsuarioRouter {

    private static final String RUTA_BASE  = "/api/v1/usuarios";

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UsuarioHandler handler) {
        return route(POST(RUTA_BASE).and(accept(MediaType.APPLICATION_JSON)), handler::registrarUsuario)
                .andRoute(GET(RUTA_BASE), handler::listarUsuarios)
                .andRoute(GET(RUTA_BASE + "/{documento}"), handler::obtenerUsuarioPorDocumento);
    }
}