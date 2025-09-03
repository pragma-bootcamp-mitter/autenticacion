package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleEntityRepository
        extends ReactiveCrudRepository<RoleEntity, Integer>,
        ReactiveQueryByExampleExecutor<RoleEntity> {

    Mono<RoleEntity> findByRoleId(Integer id);

}


