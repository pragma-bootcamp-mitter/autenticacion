package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.exceptions.BusinessException;
import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.usecase.helper.UserError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private static final BigDecimal SALARIO_MAXIMO = BigDecimal.valueOf(15_000_000);
    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        if (usuario.getSalarioBase().compareTo(SALARIO_MINIMO) < 0) {
            return Mono.error(new BusinessException("El salario base no puede ser negativo"));
        }
        if (usuario.getSalarioBase().compareTo(SALARIO_MAXIMO) > 0) {
            return Mono.error(new BusinessException(
                    "El salario base no puede superar " + SALARIO_MAXIMO.toPlainString()));
        }
        return userRepository.existePorCorreoElectronico(usuario.getCorreoElectronico())
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new BusinessException(UserError.EMAIL_ALREADY_EXISTS.getMessage()));
                    }
                    return userRepository.save(usuario);
                });
    }

    public Flux<Usuario> listarUsuarios() {
        return userRepository.findAll();
    }

    public Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad) {
        return userRepository.buscarPorDocumentoIdentidad(documentoIdentidad)
                .switchIfEmpty(Mono.error(
                        new BusinessException(UserError.USER_NOT_FOUND.getMessage())));
    }
}