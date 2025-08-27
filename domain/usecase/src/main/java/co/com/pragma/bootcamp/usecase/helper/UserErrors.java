package co.com.pragma.bootcamp.usecase.helper;

public enum UserErrors {
    EMAIL_ALREADY_REGISTERED("This email is already registered"),
    USER_NOT_FOUND("User not found"),
    DOCUMENT_ALREADY_REGISTERED("This identification document is already registered");

    private final String message;

    UserErrors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}