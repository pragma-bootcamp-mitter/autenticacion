package co.com.pragma.bootcamp.usecase.user;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> registrarUsuario(User user) {
        return validarUsuario(user)
                .then(userRepository.findByCorreoElectronico(user.getCorreoElectronico())
                        .flatMap(existing -> Mono.<User>error(new IllegalArgumentException("El correo ya está registrado")))
                        .switchIfEmpty(userRepository.save(user))
                );
    }

    private Mono<Void> validarUsuario(User user) {
        if (isNullOrEmpty(user.getNombres()) ||
                isNullOrEmpty(user.getApellidos()) ||
                isNullOrEmpty(user.getCorreoElectronico()) ||
                user.getSalarioBase() == null) {
            return Mono.error(new IllegalArgumentException("Campos obligatorios faltantes"));
        }

        if (!isValidEmail(user.getCorreoElectronico())) {
            return Mono.error(new IllegalArgumentException("Correo inválido"));
        }

        BigDecimal salario = user.getSalarioBase();
        if (salario.compareTo(BigDecimal.ZERO) < 0 || salario.compareTo(new BigDecimal("15000000")) > 0) {
            return Mono.error(new IllegalArgumentException("Salario fuera de rango"));
        }

        return Mono.empty();
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
}