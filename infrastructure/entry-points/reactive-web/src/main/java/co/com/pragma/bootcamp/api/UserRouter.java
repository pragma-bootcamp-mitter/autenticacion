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
public class UserRouter {

    private static final String BASE_PATH   = "/api/v1/users";

    @Bean
    public RouterFunction<ServerResponse> userRoutes(Handler handler) {
        return route(POST(BASE_PATH ).and(accept(MediaType.APPLICATION_JSON)), handler::registerUser)
                .andRoute(GET(BASE_PATH ), handler::listUsers)
                .andRoute(GET(BASE_PATH  + "/{document}"), handler::getUserByDocument);
    }
}