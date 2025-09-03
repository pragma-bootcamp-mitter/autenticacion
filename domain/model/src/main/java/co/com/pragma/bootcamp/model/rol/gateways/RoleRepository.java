package co.com.pragma.bootcamp.model.rol.gateways;

import co.com.pragma.bootcamp.model.rol.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(Integer id);
}
