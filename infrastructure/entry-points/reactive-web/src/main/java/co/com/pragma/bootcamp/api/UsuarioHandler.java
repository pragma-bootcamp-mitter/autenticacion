package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.RespuestaApi;
import co.com.pragma.bootcamp.api.dto.SolicitudUsuario;
import co.com.pragma.bootcamp.api.mapper.MapeadorUsuarioDto;
import co.com.pragma.bootcamp.usecase.user.UsuarioCasoDeUso;
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
public class UsuarioHandler {

    private final UsuarioCasoDeUso usuarioCasoDeUso;
    private final MapeadorUsuarioDto mapeadorUsuarioDto;

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(SolicitudUsuario.class)
                .map(mapeadorUsuarioDto::aDominio)
                .flatMap(usuarioCasoDeUso::registrarUsuario)
                .map(mapeadorUsuarioDto::aRespuesta)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new RespuestaApi<>(true, "Usuario creado exitosamente", response))
                )
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }


    public Mono<ServerResponse> listarUsuarios(ServerRequest request) {
        return usuarioCasoDeUso.listarUsuarios()
                .map(mapeadorUsuarioDto::aRespuesta)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new RespuestaApi<>(true, "Usuarios obtenidos correctamente", users))
                );
    }

    public Mono<ServerResponse> obtenerUsuarioPorDocumento(ServerRequest request) {
        String documento = request.pathVariable("documento");
        log.info("Solicitud para obtener usuario con documento: {}", documento);

        return usuarioCasoDeUso.obtenerUsuarioPorDocumento(documento)
                .map(mapeadorUsuarioDto::aRespuesta)
                .doOnNext(user -> log.info("Usuario encontrado: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new RespuestaApi<>(true, "Usuario encontrado", user))
                )
                .switchIfEmpty(
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new RespuestaApi<>(false,
                                        "Usuario con documento " + documento + " no encontrado",
                                        null))
                                .doOnSuccess(r -> log.warn("Usuario con documento {} no encontrado", documento))
                )
                .doOnError(e -> log.error("Error al consultar usuario con documento {}", documento, e));
    }
}