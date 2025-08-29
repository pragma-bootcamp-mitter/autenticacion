package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, String, UserEntityRepository>
        implements UserRepository {

    private final UserEntityRepository userEntityRepository;
    private final TransactionalOperator transactionalOperator;

    public UserRepositoryAdapter(UserEntityRepository repository,
                                 ObjectMapper mapper,
                                 TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.userEntityRepository = repository;
        this.transactionalOperator = transactionalOperator;
        this.mapper = mapper;
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity userEntityToSave = mapper.map(user, UserEntity.class);
        Mono<UserEntity> saveOperation = Mono.just(userEntityToSave)
                .flatMap(userEntityRepository::save);
        return transactionalOperator.execute(transactionStatus -> saveOperation)
                .single()
                .map(this::toEntity);
    }

    @Override
    public Mono<Boolean> existsByEmailOrIdentificationDocument(String email, String identificationDocument) {
        return userEntityRepository.findByEmailOrIdentificationDocument(email, identificationDocument)
                .hasElements();
    }

    @Override
    public Mono<User> findByIdentificationDocument(String identificationDocument) {
        return userEntityRepository.findByIdentificationDocument(identificationDocument)
                .map(this::toEntity);
    }
}
