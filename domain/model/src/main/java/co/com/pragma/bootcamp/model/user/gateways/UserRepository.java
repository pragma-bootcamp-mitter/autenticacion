package co.com.pragma.bootcamp.model.user.gateways;

import co.com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> findByCorreoElectronico(String correoElectronico);
}
