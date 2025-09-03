package co.com.pragma.bootcamp.model.login.gateways;

public interface PasswordGateway {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
