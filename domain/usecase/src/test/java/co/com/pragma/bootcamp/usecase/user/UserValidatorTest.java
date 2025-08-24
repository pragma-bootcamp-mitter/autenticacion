package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.usecase.helper.UserError;
import co.com.pragma.bootcamp.usecase.helper.UserValidator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {

    private User createValidUser() {
        return User.builder()
                .id("1")
                .nombres("Juan")
                .apellidos("Pérez")
                .correoElectronico("juan.perez@test.com")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();
    }

    @Test
    void debeValidarUsuarioCorrecto() {
        User user = createValidUser();
        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void debeFallarCuandoFaltanNombres() {
        User user = createValidUser().toBuilder().nombres(null).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.MISSING_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoNombresEsVacio() {
        User user = createValidUser().toBuilder().nombres("").build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.MISSING_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoFaltanApellidos() {
        User user = createValidUser().toBuilder().apellidos(null).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.MISSING_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoFaltaCorreo() {
        User user = createValidUser().toBuilder().correoElectronico(null).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.MISSING_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoFaltaSalario() {
        User user = createValidUser().toBuilder().salarioBase(null).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.MISSING_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoCorreoEsInvalido() {
        User user = createValidUser().toBuilder().correoElectronico("correo-invalido").build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.INVALID_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoSalarioEsNegativo() {
        User user = createValidUser().toBuilder().salarioBase(new BigDecimal("-100")).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.SALARY_OUT_OF_RANGE.getMessage(), exception.getMessage());
    }

    @Test
    void debeFallarCuandoSalarioSuperaLimite() {
        User user = createValidUser().toBuilder().salarioBase(new BigDecimal("20000000")).build();
        BusinessException exception = assertThrows(BusinessException.class, () -> UserValidator.validate(user));
        assertEquals(UserError.SALARY_OUT_OF_RANGE.getMessage(), exception.getMessage());
    }
}
