package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.CORREO_YA_REGISTRADO;
import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.DOCUMENTO_YA_REGISTRADO;
import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.USUARIO_NO_ENCONTRADO;

@RequiredArgsConstructor
public class UsuarioCasoDeUso {

    private final RepositorioUsuario repositorioUsuarios;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        return repositorioUsuarios.buscarPorDocumentoIdentidad(usuario.getDocumentoIdentidad())
                .flatMap(existente -> Mono.<Usuario>error(
                        new BusinessException(DOCUMENTO_YA_REGISTRADO.getMensaje())
                ))
                .switchIfEmpty(
                        repositorioUsuarios.existePorCorreoElectronico(usuario.getCorreoElectronico())
                                .flatMap(existeCorreo -> {
                                    if (Boolean.TRUE.equals(existeCorreo)) {
                                        return Mono.error(new BusinessException(CORREO_YA_REGISTRADO.getMensaje()));
                                    }
                                    return repositorioUsuarios.save(usuario);
                                })
                );
    }

    public Flux<Usuario> listarUsuarios() {
        return repositorioUsuarios.findAll();
    }

    public Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad) {
        return repositorioUsuarios.buscarPorDocumentoIdentidad(documentoIdentidad)
                .switchIfEmpty(Mono.error(
                        new BusinessException(USUARIO_NO_ENCONTRADO.getMensaje())));
    }
}