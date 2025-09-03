package co.com.pragma.bootcamp.r2dbc.adapter;

import co.com.pragma.bootcamp.model.rol.Role;
import co.com.pragma.bootcamp.model.rol.gateways.RoleRepository;
import co.com.pragma.bootcamp.r2dbc.RoleEntityRepository;
import co.com.pragma.bootcamp.r2dbc.mapper.RoleEntityMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter
        implements RoleRepository {

    private final RoleEntityRepository roleEntityRepository;
    private final RoleEntityMapper roleEntityMapper;


    public RoleRepositoryAdapter(RoleEntityRepository roleEntityRepository, RoleEntityMapper roleEntityMapper) {
        this.roleEntityRepository = roleEntityRepository;
        this.roleEntityMapper = roleEntityMapper;
    }

    @Override
    public Mono<Role> findById(Integer id) {
        return roleEntityRepository.findByRoleId(id)
                .map(roleEntityMapper::toDomain);
    }
}
