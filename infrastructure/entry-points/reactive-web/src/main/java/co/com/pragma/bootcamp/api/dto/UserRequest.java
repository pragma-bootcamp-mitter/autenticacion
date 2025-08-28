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
public class UserRequest {

    @NotBlank(message = "The identification document is mandatory")
    private String identificationDocument;

    @NotBlank(message = "The first name is mandatory")
    private String firstName;

    @NotBlank(message = "The last name is mandatory")
    private String lastName;

    @NotNull(message = "The date of birth is mandatory")
    private LocalDate dateOfBirth;

    @NotBlank(message = "The address is mandatory")
    private String address;

    @NotBlank(message = "The phone number is mandatory")
    private String phoneNumber;

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email does not have a valid format")
    private String email;

    @NotNull(message = "The base salary is mandatory")
    @DecimalMin(value = "0.0", message = "The base salary cannot be negative")
    @DecimalMax(value = "15000000.0", message = "The base salary cannot exceed 15 million")
    private BigDecimal baseSalary;
}