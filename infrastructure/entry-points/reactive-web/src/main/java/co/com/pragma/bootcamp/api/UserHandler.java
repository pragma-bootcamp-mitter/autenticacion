package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHandler {

    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
                .map(userDtoMapper::toDomain)
                .flatMap(userUseCase::registrarUsuario)
                .map(userDtoMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ApiResponse<>(true, "Usuario creado exitosamente", response))
                )
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }


    public Mono<ServerResponse> listarUsuarios(ServerRequest request) {
        return userUseCase.listarUsuarios()
                .map(userDtoMapper::toResponse)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ApiResponse<>(true, "Usuarios obtenidos correctamente", users))
                );
    }

    public Mono<ServerResponse> obtenerUsuarioPorDocumento(ServerRequest request) {
        String documento = request.pathVariable("documento");
        log.info("Solicitud para obtener usuario con documento: {}", documento);

        return userUseCase.obtenerUsuarioPorDocumento(documento)
                .map(userDtoMapper::toResponse)
                .doOnNext(user -> log.info("Usuario encontrado: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ApiResponse<>(true, "Usuario encontrado", user))
                )
                .switchIfEmpty(
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ApiResponse<>(false,
                                        "Usuario con documento " + documento + " no encontrado",
                                        null))
                                .doOnSuccess(r -> log.warn("Usuario con documento {} no encontrado", documento))
                )
                .doOnError(e -> log.error("Error al consultar usuario con documento {}", documento, e));
    }
}