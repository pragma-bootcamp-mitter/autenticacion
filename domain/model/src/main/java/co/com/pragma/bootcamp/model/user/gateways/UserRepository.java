package co.com.pragma.bootcamp.model.user.gateways;

import co.com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> findByCorreoElectronico(String correoElectronico);
    Mono<User> findByDocumentoIdentidad(String documentoIdentidad);
    Flux<User> findAll();
}
