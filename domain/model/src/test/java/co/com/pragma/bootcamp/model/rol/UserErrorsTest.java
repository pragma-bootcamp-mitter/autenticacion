package co.com.pragma.bootcamp.model.rol;

import co.com.pragma.bootcamp.model.exceptions.UserErrors;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

class UserErrorsTest {

    @Test
    void userErrors_shouldHaveNonNullMessages() {
        for (UserErrors error : UserErrors.values()) {
            assertNotNull(error.getMessage(), "Message for " + error.name() + " should not be null");
        }
    }
}
