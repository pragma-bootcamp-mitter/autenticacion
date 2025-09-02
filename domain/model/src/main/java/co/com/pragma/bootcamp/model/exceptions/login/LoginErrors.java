package co.com.pragma.bootcamp.model.exceptions.login;

import co.com.pragma.bootcamp.model.exceptions.BusinessErrorCode;

public enum LoginErrors {
    INVALID_CREDENTIALS(
            BusinessErrorCode.BR_401_UNAUTHORIZED,
            "Invalid credentials provided"
    ),
    ROLE_NOT_FOUND(
            BusinessErrorCode.BR_404_NOT_FOUND,
            "Role not found"
    );

    private final BusinessErrorCode errorCode;
    private final String message;

    LoginErrors(BusinessErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}