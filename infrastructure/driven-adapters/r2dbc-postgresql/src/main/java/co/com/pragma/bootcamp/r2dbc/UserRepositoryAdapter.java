package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, String, UserEntityRepository>
        implements UserRepository {

    private final UserEntityRepository userEntityRepository;
    private final ObjectMapper mapper;

    public UserRepositoryAdapter(UserEntityRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.userEntityRepository = repository;
        this.mapper = mapper;
    }

    @Override
    protected UserEntity toData(User entity) {
        return mapper.map(entity, UserEntity.class);
    }

    @Override
    public Mono<Boolean> existsByEmailOrIdentificationDocument(String email, String identificationDocument) {
        return userEntityRepository.findByEmailOrIdentificationDocument(email, identificationDocument)
                .hasElements();
    }
}
