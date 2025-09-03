package co.com.pragma.bootcamp.model.exceptions.login;

import lombok.Getter;
import java.util.List;

@Getter
public class LoginBusinessException extends RuntimeException {
    private final LoginErrors loginError;
    private final List<String> messages;

    public LoginBusinessException(LoginErrors loginError, List<String> messages) {
        super(loginError.getMessage());
        this.loginError = loginError;
        this.messages = messages;
    }

    public LoginBusinessException(LoginErrors loginError) {
        this(loginError, List.of(loginError.getMessage()));
    }
}