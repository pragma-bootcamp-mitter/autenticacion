package co.com.pragma.bootcamp.api.config;

import co.com.pragma.bootcamp.api.dto.RespuestaApi;
import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<RespuestaApi<Object>>> handleValidationErrors(ServerWebInputException ex) {
        log.error("Error de validación: {}", ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(RespuestaApi.error("Datos de entrada inválidos: " + ex.getReason()))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<RespuestaApi<Object>>> handleIllegalArgument(IllegalArgumentException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(RespuestaApi.error(ex.getMessage()))
        );
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<RespuestaApi<Object>>> handleBusiness(BusinessException ex) {
        log.error("Error de negocio: {}", ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(RespuestaApi.error(ex.getMessage()))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<RespuestaApi<Object>>> handleGeneric(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(RespuestaApi.error("Ha ocurrido un error inesperado"))
        );
    }
}
