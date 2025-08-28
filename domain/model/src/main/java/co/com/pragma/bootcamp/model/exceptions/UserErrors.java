package co.com.pragma.bootcamp.model.exceptions;

public enum UserErrors {
    DOCUMENT_OR_EMAIL_ALREADY_REGISTERED(
            BusinessErrorCode.BR_409_CONFLICT,
            "The provided identification document or email address is already in use"
    );

    private final BusinessErrorCode errorCode;
    private final String message;

    UserErrors(BusinessErrorCode errorCode, String message) {
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