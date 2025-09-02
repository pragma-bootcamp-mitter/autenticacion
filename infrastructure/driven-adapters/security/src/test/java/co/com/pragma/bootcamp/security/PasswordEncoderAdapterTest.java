package co.com.pragma.bootcamp.security;

import co.com.pragma.bootcamp.security.adapter.PasswordEncoderAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {

    @InjectMocks
    private PasswordEncoderAdapter passwordEncoderAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void encode_shouldCallEncoderEncode() {
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        passwordEncoderAdapter.encode(rawPassword);

        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void matches_shouldCallEncoderMatches() {
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        passwordEncoderAdapter.matches(rawPassword, encodedPassword);

        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void matches_shouldReturnTrue_whenPasswordsMatch() {
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = passwordEncoderAdapter.matches(rawPassword, encodedPassword);

        assertTrue(result);
    }

    @Test
    void matches_shouldReturnFalse_whenPasswordsDoNotMatch() {
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = passwordEncoderAdapter.matches(rawPassword, encodedPassword);

        assertFalse(result);
    }
}