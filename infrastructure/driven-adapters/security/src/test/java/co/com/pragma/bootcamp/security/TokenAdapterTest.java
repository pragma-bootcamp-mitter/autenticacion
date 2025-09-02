package co.com.pragma.bootcamp.security;

import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.security.adapter.TokenAdapter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TokenAdapterTest {

    private TokenAdapter tokenAdapter;
    private static final String MOCK_SECRET = "thisisaverylongandsecuresecretkeyforjwttokenadapter";
    private static final long MOCK_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1);
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ROLE = "USER";
    private SecretKey mockSecretKey;

    @BeforeEach
    void setUp() {
        tokenAdapter = new TokenAdapter(MOCK_EXPIRATION_TIME, MOCK_SECRET);
        mockSecretKey = Keys.hmacShaKeyFor(MOCK_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void generateToken_shouldReturnValidTokenAndLoginObject() {
        Mono<LogIn> result = tokenAdapter.generateToken(TEST_EMAIL, TEST_ROLE);

        StepVerifier.create(result)
                .expectNextMatches(login -> {
                    assertNotNull(login.getToken());
                    return login.getEmail().equals(TEST_EMAIL);
                })
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnClaims_whenTokenIsValid() {
        String validToken = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MOCK_EXPIRATION_TIME))
                .signWith(mockSecretKey)
                .compact();

        Mono<Claims> result = tokenAdapter.validateToken(validToken);

        StepVerifier.create(result)
                .expectNextMatches(claims ->
                        claims.getSubject().equals(TEST_EMAIL) &&
                                claims.get("role", String.class).equals(TEST_ROLE)
                )
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnEmptyMono_whenTokenIsExpired() {
        String expiredToken = Jwts.builder()
                .subject(TEST_EMAIL)
                .claim("role", TEST_ROLE)
                .issuedAt(new Date(System.currentTimeMillis() - 100000))
                .expiration(new Date(System.currentTimeMillis() - 50000))
                .signWith(mockSecretKey)
                .compact();

        Mono<Claims> result = tokenAdapter.validateToken(expiredToken);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnEmptyMono_whenTokenIsInvalid() {
        String invalidToken = "invalid.token.string";

        Mono<Claims> result = tokenAdapter.validateToken(invalidToken);

        StepVerifier.create(result)
                .verifyComplete();
    }
}