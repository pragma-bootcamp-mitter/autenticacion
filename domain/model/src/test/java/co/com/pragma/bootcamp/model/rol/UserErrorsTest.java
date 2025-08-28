package co.com.pragma.bootcamp.model.rol;

import co.com.pragma.bootcamp.model.exceptions.BusinessErrorCode;
import co.com.pragma.bootcamp.model.exceptions.UserErrors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

class UserErrorsTest {

    @Test
    void userErrors_shouldHaveNonNullMessages() {
        for (UserErrors error : UserErrors.values()) {
            assertNotNull(error.getMessage(), "Message for " + error.name() + " should not be null");
        }
    }

    @Test
    void testDocumentOrEmailAlreadyRegistered() {
        // Given
        UserErrors error = UserErrors.DOCUMENT_OR_EMAIL_ALREADY_REGISTERED;

        // When
        BusinessErrorCode actualErrorCode = error.getErrorCode();
        String actualMessage = error.getMessage();

        // Then
        assertEquals(BusinessErrorCode.BR_409_CONFLICT, actualErrorCode, "El código de error no coincide.");
        assertEquals("The provided identification document or email address is already in use", actualMessage,
                "El mensaje de error no coincide.");
    }

}
