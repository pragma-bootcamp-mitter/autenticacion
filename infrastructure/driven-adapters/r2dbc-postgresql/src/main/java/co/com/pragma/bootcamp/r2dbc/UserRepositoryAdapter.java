package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.r2dbc.entity.UserData;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.bootcamp.r2dbc.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import co.com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserData, String, UserDataRepository> implements UserRepository {

    private final UserMapper userMapper;
    private final UserDataRepository userDataRepository;

    public UserRepositoryAdapter(UserDataRepository repository, UserMapper userMapper, UserDataRepository userDataRepository) {
        super(repository, null, userMapper::toDomain);
        this.userMapper = userMapper;
        this.userDataRepository = userDataRepository;
    }

    @Override
    protected UserData toData(User entity) {
        return userMapper.toData(entity);
    }

    @Override
    protected User toEntity(UserData data) {
        return userMapper.toDomain(data);
    }

    public Mono<User> findByCorreoElectronico(String correoElectronico) {
        return userDataRepository.findByCorreoElectronico(correoElectronico)
                .map(userMapper::toDomain);
    }
}
