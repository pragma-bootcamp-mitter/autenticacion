package co.com.pragma.bootcamp.api.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email does not have a valid format")
    private String email;

    @NotBlank(message = "The password is mandatory")
    private String password;
}