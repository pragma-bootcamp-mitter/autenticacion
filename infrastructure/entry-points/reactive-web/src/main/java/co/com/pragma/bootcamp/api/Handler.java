package co.com.pragma.bootcamp.api;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.mapper.UserMapper;
import co.com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final Validator validator;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        log.info("Received request to register a new user.");

        return request.bodyToMono(UserRequest.class)
                .doOnNext(userRequest -> log.info("Attempting to validate user data: {}", userRequest))
                .flatMap(userRequest -> {
                    Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
                    if (!violations.isEmpty()) {
                        Map<String, String> errors = violations.stream()
                                .collect(Collectors.toMap(
                                        v -> v.getPropertyPath().toString(),
                                        ConstraintViolation::getMessage
                                ));
                        log.warn("Validation failed for user request. Errors: {}", errors);
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.error("Validation error", errors));
                    }
                    log.info("Validation successful. Saving new user to the database.");
                    return userUseCase.registerUser(userMapper.toDomain(userRequest))
                            .map(userMapper::toResponse)
                            .flatMap(response -> {
                                log.info("User successfully created with ID: {}", response.getId());
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(ApiResponse.success("User created successfully", response));
                            });
                })
                .doOnError(BusinessException.class, e -> log.error("Business error during user registration: {}", e.getMessage()))
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.error(e.getMessage()))
                )
                .doOnError(Exception.class, e -> log.error("Unexpected error during user registration: {}", e.getMessage(), e));
    }

    public Mono<ServerResponse> listUsers(ServerRequest request) {
        return userUseCase.listUsers()
                .map(userMapper::toResponse)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success("Users retrieved successfully", users))
                );
    }

    public Mono<ServerResponse> getUserByDocument(ServerRequest request) {
        String document = request.pathVariable("documento");
        log.info("Request to get user with document: {}", document);

        return userUseCase.getUserByDocument(document)
                .map(userMapper::toResponse)
                .doOnNext(user -> log.info("User found: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.success("User found", user))
                )
                .switchIfEmpty(
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ApiResponse<>(false,
                                        "User with document " + document + " not found",
                                        null))
                                .doOnSuccess(r -> log.warn("User with document {} not found", document))
                )
                .doOnError(e -> log.error("Error when querying user with document {}", document, e));
    }
}