package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UserHandler;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
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
                        .title("Autenticación API")
                        .version("1.0.0")
                        .description("API para registrar usuarios en el sistema")
                        .contact(new Contact()
                                .name("Equipo Bootcamp")
                                .email("soporte@pragma.com")
                        )
                );
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = { "application/json" },
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registrarUsuario",
                    operation = @Operation(
                            operationId = "registrarUsuario",
                            summary = "Registrar un nuevo usuario",
                            description = "Crea un usuario en el sistema con datos personales básicos",
                            tags = {"Usuarios"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = UserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado",
                                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> router(UserHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/usuarios", handler::registrarUsuario)
                .build();
    }
}
