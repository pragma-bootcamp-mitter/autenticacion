package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.CORREO_YA_REGISTRADO;
import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.SALARIO_NEGATIVO;
import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.SALARIO_SUPERA_MAXIMO;
import static co.com.pragma.bootcamp.usecase.helper.ErroresUsuario.USUARIO_NO_ENCONTRADO;

@RequiredArgsConstructor
public class UsuarioCasoDeUso {

    private final RepositorioUsuario repositorioUsuarios;
    private static final BigDecimal SALARIO_MAXIMO = BigDecimal.valueOf(15_000_000);
    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        if (usuario.getSalarioBase().compareTo(SALARIO_MINIMO) < 0) {
            return Mono.error(new BusinessException(SALARIO_NEGATIVO.getMensaje()));
        }
        if (usuario.getSalarioBase().compareTo(SALARIO_MAXIMO) > 0) {
            return Mono.error(new BusinessException(
                    SALARIO_SUPERA_MAXIMO.getMensaje() + SALARIO_MAXIMO.toPlainString()));
        }
        return repositorioUsuarios.existePorCorreoElectronico(usuario.getCorreoElectronico())
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new BusinessException(CORREO_YA_REGISTRADO.getMensaje()));
                    }
                    return repositorioUsuarios.save(usuario);
                });
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