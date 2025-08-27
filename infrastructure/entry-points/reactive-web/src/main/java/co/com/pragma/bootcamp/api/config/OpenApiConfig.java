package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.Handler;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Authentication API")
                        .version("1.0.0")
                        .description("API for registering users in the system")
                        .contact(new Contact()
                                .name("Bootcamp Team")
                                .email("soporte@pragma.com")
                        )
                );
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    produces = { "application/json" },
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
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
            ),
            @RouterOperation(
                    path = "/api/v1/users",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
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
            ),
            @RouterOperation(
                    path = "/api/v1/users/{document}",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getUserByDocument",
                    operation = @Operation(
                            operationId = "getUserByDocument",
                            summary = "Get user by identification document",
                            tags = {"Users"},
                            parameters = {
                                    @Parameter(
                                            name = "document",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Document number",
                                            example = "123456789"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "User found",
                                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "User not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> router(Handler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/users", handler::registerUser)
                .GET("/api/v1/users", handler::listUsers)
                .GET("/api/v1/users/{document}", handler::getUserByDocument)
                .build();
    }
}