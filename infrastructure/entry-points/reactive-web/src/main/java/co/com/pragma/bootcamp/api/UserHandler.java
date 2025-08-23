package co.com.pragma.bootcamp.api;


import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
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
                        .bodyValue(response))
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }
}