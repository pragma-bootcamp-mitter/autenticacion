package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.UsuarioHandler;
import co.com.pragma.bootcamp.api.dto.SolicitudUsuario;
import co.com.pragma.bootcamp.api.dto.RespuestaUsuario;
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
                    beanClass = UsuarioHandler.class,
                    beanMethod = "registrarUsuario",
                    operation = @Operation(
                            operationId = "registrarUsuario",
                            summary = "Registrar un nuevo usuario",
                            description = "Crea un usuario en el sistema con datos personales básicos",
                            tags = {"Usuarios"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = SolicitudUsuario.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario creado",
                                            content = @Content(schema = @Schema(implementation = RespuestaUsuario.class))),
                                    @ApiResponse(responseCode = "409", description = "Correo ya registrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listarUsuarios",
                    operation = @Operation(
                            operationId = "listarUsuarios",
                            summary = "Listar todos los usuarios",
                            tags = {"Usuarios"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado de usuarios",
                                            content = @Content(schema = @Schema(implementation = RespuestaUsuario.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{documento}",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "obtenerUsuarioPorDocumento",
                    operation = @Operation(
                            operationId = "obtenerUsuarioPorDocumento",
                            summary = "Obtener usuario por documento de identidad",
                            tags = {"Usuarios"},
                            parameters = {
                                    @Parameter(
                                            name = "documento",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Número de documento",
                                            example = "123456789"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = RespuestaUsuario.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> router(UsuarioHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/usuarios", handler::registrarUsuario)
                .build();
    }
}
