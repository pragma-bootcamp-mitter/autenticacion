package co.com.pragma.bootcamp.model.user.gateways;

import co.com.pragma.bootcamp.model.user.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RepositorioUsuario {
    Mono<Usuario> save(Usuario usuario);
    Mono<Boolean> existePorCorreoElectronico(String correoElectronico);
    Mono<Usuario> buscarPorDocumentoIdentidad(String documentoIdentidad);
    Flux<Usuario> findAll();
}
