package co.com.pragma.bootcamp.usecase.helper;

public enum UserError {
    MISSING_FIELDS("Campos obligatorios faltantes"),
    INVALID_EMAIL("Correo inválido"),
    SALARY_OUT_OF_RANGE("Salario fuera de rango"),
    EMAIL_ALREADY_EXISTS("El correo ya está registrado");

    private final String message;

    UserError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
