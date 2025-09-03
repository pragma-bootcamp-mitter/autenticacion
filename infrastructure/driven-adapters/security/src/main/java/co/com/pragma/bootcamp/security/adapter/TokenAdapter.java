package co.com.pragma.bootcamp.security.adapter;

import co.com.pragma.bootcamp.model.login.LogIn;
import co.com.pragma.bootcamp.model.login.gateways.TokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class TokenAdapter implements TokenGateway {

    private final long expirationTime;
    private final SecretKey secretKey;

    public TokenAdapter
            (@Value("${jwt.expiration-time}") long expirationTime,
             @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<LogIn> generateToken(String email, String role, String documentId) {
        return Mono.fromCallable(() -> {
            String token = Jwts.builder()
                    .subject(documentId)
                    .claim("role", role)
                    .claim("email", email)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(secretKey)
                    .compact();

            return LogIn.builder()
                    .token(token)
                    .email(email)
                    .build();
        });
    }

    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload())
                .onErrorResume(e -> Mono.empty());
    }
}