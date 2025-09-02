package co.com.pragma.bootcamp.security.config;

import co.com.pragma.bootcamp.security.adapter.TokenAdapter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final TokenAdapter tokenAdapter;
    private static final String ROLE = "ROLE_";

    public JwtAuthenticationFilter(
            ReactiveAuthenticationManager authenticationManager,
            TokenAdapter tokenAdapter) {
        super(authenticationManager);
        this.tokenAdapter = tokenAdapter;
        setServerAuthenticationConverter(exchange ->
                Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                        .filter(authHeader -> authHeader.startsWith("Bearer "))
                        .flatMap(authHeader -> {
                            String authToken = authHeader.substring(7);
                            return tokenAdapter.validateToken(authToken)
                                    .map(claims -> {
                                        String email = claims.getSubject();
                                        String role = claims.get("role", String.class);
                                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE + role));
                                        return new UsernamePasswordAuthenticationToken(email, null, authorities);
                                    });
                        })
        );
    }
}