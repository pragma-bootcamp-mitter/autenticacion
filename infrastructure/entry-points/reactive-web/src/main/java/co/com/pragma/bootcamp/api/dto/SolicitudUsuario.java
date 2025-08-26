package co.com.pragma.bootcamp.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SolicitudUsuario  {

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String correoElectronico;

    @NotNull(message = "El salario base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El salario base no puede ser negativo")
    @DecimalMax(value = "15000000.0", message = "El salario base no puede superar 15 millones")
    private BigDecimal salarioBase;
}
