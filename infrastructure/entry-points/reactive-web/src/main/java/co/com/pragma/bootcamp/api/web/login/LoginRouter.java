package co.com.pragma.bootcamp.api.web.login;

import co.com.pragma.bootcamp.api.dto.login.LoginRequest;
import co.com.pragma.bootcamp.api.dto.login.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class LoginRouter {

    private static final String BASE_PATH = "/api/v1/login";

    @Bean
    @RouterOperation(
            path = BASE_PATH,
            produces = {"application/json"},
            method = RequestMethod.POST,
            beanClass = LoginHandler.class,
            beanMethod = "login",
            operation = @Operation(
                    operationId = "login",
                    summary = "Authenticate a user",
                    description = "Authenticates an existing user and returns a JWT token",
                    tags = {"Authentication"},
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = LoginRequest.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Authentication successful",
                                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                            @ApiResponse(responseCode = "401", description = "Invalid credentials")
                    }
            )
    )
    public RouterFunction<ServerResponse> loginRoutes(LoginHandler handler) {
        return RouterFunctions.route(POST(BASE_PATH), handler::login);
    }
}