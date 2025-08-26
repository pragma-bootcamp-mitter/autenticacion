package co.com.pragma.bootcamp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaApi<T> {
    private boolean exito;
    private String mensaje;
    private T datos;
}