package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessErrorCode;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.exceptions.login.LoginBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Handling exception: {}", ex.getMessage());

        return switch (ex) {
            case BusinessException businessEx -> {
                BusinessErrorCode errorCode = businessEx.getUserError().getErrorCode();
                HttpStatus status = mapBusinessErrorCodeToHttpStatus(errorCode);
                ApiResponse<?> apiResponse = ApiResponse.businessError(
                        errorCode.getCode(),
                        businessEx.getUserError().getMessage(),
                        errorCode.getDefaultMessage()
                );
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            case LoginBusinessException loginEx -> {
                BusinessErrorCode errorCode = loginEx.getLoginError().getErrorCode();
                HttpStatus status = mapBusinessErrorCodeToHttpStatus(errorCode);
                ApiResponse<?> apiResponse = ApiResponse.businessError(
                        errorCode.getCode(),
                        loginEx.getLoginError().getMessage(),
                        errorCode.getDefaultMessage()
                );
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            case ConstraintViolationException validationEx -> {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                List<Map<String, String>> errors = validationEx.getConstraintViolations().stream()
                        .map(v -> Map.of("field", v.getPropertyPath().toString(), "error", v.getMessage()))
                        .toList();
                ApiResponse<?> apiResponse = ApiResponse.validationError(errors);
                yield buildErrorResponse(exchange, status, apiResponse);
            }
            default -> {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ApiResponse<?> apiResponse = ApiResponse.businessError(
                        "GEN_500",
                        "An unexpected error has occurred",
                        "Internal Server Error"
                );
                log.error("Unexpected error during request processing", ex);
                yield buildErrorResponse(exchange, status, apiResponse);
            }
        };
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, ApiResponse<?> apiResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private HttpStatus mapBusinessErrorCodeToHttpStatus(BusinessErrorCode errorCode) {
        return switch (errorCode) {
            case BR_409_CONFLICT -> HttpStatus.CONFLICT;
            case BR_404_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BR_400_BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case BR_401_UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}