package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Authentication API",
                version = "1.0.0",
                description = "API for registering users in the system",
                contact = @Contact(name = "Bootcamp Team", email = "soporte@pragma.com")
        )
)
public class UserRouter {

    private static final String BASE_PATH = "/api/v1/users";

    @Bean
    @RouterOperation(
            path = BASE_PATH,
            produces = {"application/json"},
            method = RequestMethod.POST,
            beanClass = UserHandler.class,
            beanMethod = "registerUser",
            operation = @Operation(
                    operationId = "registerUser",
                    summary = "Register a new user",
                    description = "Creates a user in the system with basic personal data",
                    tags = {"Users"},
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = UserRequest.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "User created",
                                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
                            @ApiResponse(responseCode = "409", description = "Email already registered")
                    }
            )
    )
    public RouterFunction<ServerResponse> registerUserRoute(UserHandler userHandler) {
        return route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), userHandler::registerUser);
    }

    @Bean
    @RouterOperation(
            path = "/api/v1/users",
            produces = {"application/json"},
            method = RequestMethod.GET,
            beanClass = UserHandler.class,
            beanMethod = "listUsers",
            operation = @Operation(
                    operationId = "listUsers",
                    summary = "List all users",
                    tags = {"Users"},
                    responses = {
                            @ApiResponse(responseCode = "200", description = "List of users",
                                    content = @Content(schema = @Schema(implementation = UserResponse.class)))
                    }
            )
    )
    public RouterFunction<ServerResponse> listUsersRoute(UserHandler userHandler) {
        return route(GET(BASE_PATH), userHandler::listUsers);
    }

    @Bean
    @RouterOperation(
            path = BASE_PATH + "/{document}",
            produces = {"application/json"},
            method = RequestMethod.GET,
            beanClass = UserHandler.class,
            beanMethod = "getUserByDocument",
            operation = @Operation(
                    operationId = "getUserByDocument",
                    summary = "Get user by document",
                    description = "Retrieves a user's details by their identification document.",
                    tags = {"Users"},
                    parameters = {
                            @Parameter(in = ParameterIn.PATH, name = "document", required = true, description = "Identification document of the user to search for")
                    },
                    responses = {
                            @ApiResponse(responseCode = "200", description = "User found",
                                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
                            @ApiResponse(responseCode = "404", description = "User not found")
                    }
            )
    )
    public RouterFunction<ServerResponse> getUserByDocumentRoute(UserHandler userHandler) {
        return route(GET(BASE_PATH + "/{document}"), userHandler::getUserByDocument);
    }
}