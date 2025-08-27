package co.com.pragma.bootcamp.usecase.helper;

public enum ErroresUsuario {
    CAMPOS_FALTANTES("Campos obligatorios faltantes"),
    CORREO_INVALIDO("Correo inválido"),
    SALARIO_FUERA_DE_RANGO("Salario fuera de rango"),
    CORREO_YA_REGISTRADO("El correo ya está registrado"),
    USUARIO_NO_ENCONTRADO("Usuario no encontrado"),
    SALARIO_NEGATIVO("El salario base no puede ser negativo"),
    SALARIO_SUPERA_MAXIMO("El salario base no puede superar "),
    DOCUMENTO_YA_REGISTRADO("Ese documento de identidad ya está registrado")
    ;

    private final String mensaje;

    ErroresUsuario(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}