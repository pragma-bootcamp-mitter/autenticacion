package co.com.pragma.bootcamp.api.dto.user;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserResponse {
    private String id;
    private String identificationDocument;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;
    private String password;
    private Integer roleId;
}