package co.com.pragma.bootcamp.usecase.login;

import co.com.pragma.bootcamp.model.login.gateways.PasswordGateway;
import co.com.pragma.bootcamp.model.login.gateways.TokenGateway;
import co.com.pragma.bootcamp.model.rol.gateways.RoleRepository;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class LogInUseCaseTest {

    @InjectMocks
    private LogInUseCase logInUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordGateway passwordGateway;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenGateway tokenGateway;


}
