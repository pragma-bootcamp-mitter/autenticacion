package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.rol.Role;
import co.com.pragma.bootcamp.r2dbc.adapter.RoleRepositoryAdapter;
import co.com.pragma.bootcamp.r2dbc.entity.RoleEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.RoleEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    @InjectMocks
    private RoleRepositoryAdapter roleRepositoryAdapter;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @Mock
    private RoleEntityMapper roleEntityMapper;

    private RoleEntity roleEntity;
    private Role role;

    @BeforeEach
    void setUp() {
        roleEntity = RoleEntity.builder()
                .roleId(1)
                .name("ADMIN")
                .description("Administrator Role")
                .build();

        role = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator Role")
                .build();
    }

    @Test
    void findById_shouldReturnRole_whenRoleExists() {
        when(roleEntityRepository.findByRoleId(1)).thenReturn(Mono.just(roleEntity));
        when(roleEntityMapper.toDomain(any(RoleEntity.class))).thenReturn(role);

        Mono<Role> result = roleRepositoryAdapter.findById(1);

        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenRoleDoesNotExist() {
        when(roleEntityRepository.findByRoleId(1)).thenReturn(Mono.empty());

        Mono<Role> result = roleRepositoryAdapter.findById(1);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}