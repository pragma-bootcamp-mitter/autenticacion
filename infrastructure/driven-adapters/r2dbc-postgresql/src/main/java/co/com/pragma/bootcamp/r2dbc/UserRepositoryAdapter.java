package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.dto.UserData;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserData, String, UserDataRepository>
        implements UserRepository {

    private final UserDataRepository userDataRepository;
    private final ObjectMapper mapper;

    public UserRepositoryAdapter(UserDataRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.userDataRepository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<User> findByCorreoElectronico(String correoElectronico) {
        return userDataRepository.findByCorreoElectronico(correoElectronico)
                .map(d -> mapper.map(d, User.class));
    }
}
