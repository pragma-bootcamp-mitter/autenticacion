package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.helper.ValidatorUtil;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.model.exceptions.UserErrors.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHandler {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final ValidatorUtil validatorUtil;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(validatorUtil::validate)
                .map(userMapper::toDomain)
                .flatMap(userUseCase::registerUser)
                .map(userMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(response)));
    }

    public Mono<ServerResponse> listUsers(ServerRequest request) {
        return userUseCase.listUsers()
                .map(userMapper::toResponse)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(users))
                );
    }

    public Mono<ServerResponse> getUserByDocument(ServerRequest serverRequest) {
        String document = serverRequest.pathVariable("document");

        return userUseCase.getUserByDocument(document)
                .switchIfEmpty(Mono.error(new BusinessException(USER_NOT_FOUND)))
                .map(userMapper::toResponse)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success(user))
                );
    }
}