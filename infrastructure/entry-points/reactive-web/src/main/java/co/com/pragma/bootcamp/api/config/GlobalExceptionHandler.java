package co.com.pragma.bootcamp.api.config;


import co.com.pragma.bootcamp.api.dto.ApiResponse;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.exceptions.UserErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
@Order(-2)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Handling exception: {}", ex.getMessage());
        HttpStatus status;
        ApiResponse<?> apiResponse;

        if (ex instanceof BusinessException businessEx) {
            status = mapUserErrorToHttpStatus(businessEx.getUserError());
            apiResponse = ApiResponse.businessError(
                    businessEx.getUserError().getErrorCode().getCode(),
                    businessEx.getUserError().getMessage(),
                    "Bad Request"
            );
        } else if (ex instanceof ConstraintViolationException validationEx) {
            status = HttpStatus.BAD_REQUEST;
            List<Map<String, String>> errors = validationEx.getConstraintViolations().stream()
                    .map(v -> Map.of("field", v.getPropertyPath().toString(), "error", v.getMessage()))
                    .toList();
            apiResponse = ApiResponse.validationError(errors);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            apiResponse = ApiResponse.businessError(
                    "GEN_500",
                    "An unexpected error has occurred",
                    "Internal Server Error"
            );
            log.error("Unexpected error during request processing", ex);
        }

        return buildErrorResponse(exchange, status, apiResponse);
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

    private HttpStatus mapUserErrorToHttpStatus(UserErrors userError) {
        return switch (userError.getErrorCode()) {
            case BR_409_CONFLICT -> HttpStatus.CONFLICT;
            case BR_404_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BR_400_BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}