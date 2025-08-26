package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entidad.UsuarioEntidad;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RepositorioDatosUsuario extends
        ReactiveCrudRepository<UsuarioEntidad, String>, ReactiveQueryByExampleExecutor<UsuarioEntidad> {
    Mono<UsuarioEntidad> findByCorreoElectronico(String correoElectronico);
    Mono<UsuarioEntidad> findByDocumentoIdentidad(String documentoIdentidad);
}
