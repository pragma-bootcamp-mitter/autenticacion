package co.com.pragma.bootcamp.security.config;

import co.com.pragma.bootcamp.security.adapter.TokenAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenAdapter tokenAdapter;

    public JwtAuthenticationFilter(
            ReactiveAuthenticationManager authenticationManager,
            TokenAdapter tokenAdapter) {
        super(authenticationManager);
        this.tokenAdapter = tokenAdapter;
        setServerAuthenticationConverter(createAuthenticationConverter());
    }

    private ServerAuthenticationConverter createAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .map(authHeader -> authHeader.substring(BEARER_PREFIX.length()))
                .flatMap(this::convertTokenToAuthentication)
                .doOnError(e -> log.error("Error during authentication conversion: {}", e.getMessage()));
    }

    private Mono<Authentication> convertTokenToAuthentication(String authToken) {
        return tokenAdapter.validateToken(authToken)
                .map(claims -> {
                    String email = claims.getSubject();
                    String role = claims.get("role", String.class);
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + role));
                    return new UsernamePasswordAuthenticationToken(email, null, authorities);
                });
    }
}