package co.com.pragma.bootcamp.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserRequest {
    private String documentoIdentidad;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private BigDecimal salarioBase;
}
