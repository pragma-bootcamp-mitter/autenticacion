package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
@Component
@RequiredArgsConstructor
@Slf4j
public class UserHandler {

    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en el sistema con datos personales básicos.")
    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        log.info("Iniciando registro de usuario");
        return request.bodyToMono(UserRequest.class)
                .doOnNext(req -> log.debug("Payload recibido: {}", req))
                .map(userDtoMapper::toDomain)
                .flatMap(userUseCase::registrarUsuario)
                .map(userDtoMapper::toResponse)
                .doOnNext(res -> log.info("Usuario registrado exitosamente con ID: {}", res.getId()))
                .doOnNext(
                        res -> log.info("Usuario registrado exitosamente con documento: {}",
                                res.getDocumentoIdentidad()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(e -> log.error("Error registrando usuario", e))
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }

    @Operation(summary = "Listar todos los usuarios")
    public Mono<ServerResponse> listarUsuarios(ServerRequest request) {
        log.info("Solicitud para listar todos los usuarios");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        userUseCase.listarUsuarios()
                                .map(userDtoMapper::toResponse)
                                .doOnNext(user -> log.debug("Usuario encontrado: {}", user))
                                .doOnComplete(() -> log.info("Finalizada consulta de usuarios")),
                        UserResponse.class
                );
    }

    @Operation(summary = "Obtener usuario por documento de identidad")
    public Mono<ServerResponse> obtenerUsuarioPorDocumento(ServerRequest request) {
        String documento = request.pathVariable("documento");
        log.info("Solicitud para obtener usuario con documento: {}", documento);

        return userUseCase.obtenerUsuarioPorDocumento(documento)
                .map(userDtoMapper::toResponse)
                .doOnNext(user -> log.info("Usuario encontrado: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(
                        ServerResponse.notFound().build()
                                .doOnSuccess(r -> log.warn("Usuario con documento {} no encontrado", documento))
                )
                .doOnError(e -> log.error("Error al consultar usuario con documento {}", documento, e));
    }
}