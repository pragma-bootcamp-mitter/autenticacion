package co.com.pragma.bootcamp.model.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final UserErrors userError;
    private final List<String> messages;

    public BusinessException(UserErrors userError, List<String> messages) {
        super(userError.getMessage());
        this.userError = userError;
        this.messages = messages;
    }

    public BusinessException(UserErrors userError) {
        this(userError, List.of(userError.getMessage()));
    }
}