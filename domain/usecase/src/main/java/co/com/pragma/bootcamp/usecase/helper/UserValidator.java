package co.com.pragma.bootcamp.usecase.helper;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.User;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final BigDecimal MAX_SALARIO = new BigDecimal("15000000");

    public static void validate(User user) {
        if (isNullOrEmpty(user.getNombres()) ||
                isNullOrEmpty(user.getApellidos()) ||
                isNullOrEmpty(user.getCorreoElectronico()) ||
                user.getSalarioBase() == null) {
            throw new BusinessException(UserError.MISSING_FIELDS.getMessage());
        }

        if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico()).matches()) {
            throw new BusinessException(UserError.INVALID_EMAIL.getMessage());
        }

//        if (user.getDocumentoIdentidad() == null || user.getDocumentoIdentidad().isBlank()) {
//            throw new BusinessException(UserError.MISSING_DOCUMENT.getMessage());
//        }

        BigDecimal salario = user.getSalarioBase();
        if (salario.compareTo(BigDecimal.ZERO) < 0 || salario.compareTo(MAX_SALARIO) > 0) {
            throw new BusinessException(UserError.SALARY_OUT_OF_RANGE.getMessage());
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
