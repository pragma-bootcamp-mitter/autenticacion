package co.com.pragma.bootcamp.model.login.gateways;

import co.com.pragma.bootcamp.model.login.LogIn;
import reactor.core.publisher.Mono;

public interface TokenGateway {
    Mono<LogIn> generateToken(String email, String role, String documentId);
}