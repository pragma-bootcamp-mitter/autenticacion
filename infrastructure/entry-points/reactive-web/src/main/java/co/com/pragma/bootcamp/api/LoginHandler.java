package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.LoginRequest;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.LogInMapper;
import co.com.pragma.bootcamp.usecase.login.LogInUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final LogInUseCase logInUseCase;
    private final ValidatorUtil validatorUtil;
    private final LogInMapper logInMapper;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(validatorUtil::validate)
                .flatMap(logInRequest ->
                        logInUseCase.login(logInRequest.getEmail(), logInRequest.getPassword()))
                .map(logInMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(response)));
    }


}