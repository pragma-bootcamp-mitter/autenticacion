package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserData;
import co.com.pragma.bootcamp.r2dbc.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserDataRepository repository;

    @Mock
    UserMapper userMapper; // cambiamos ObjectMapper -> UserMapper

    private UserData sampleData() {
        return UserData.builder()
                .id("1")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 5, 10))
                .direccion("Calle 123")
                .telefono("3001234567")
                .correoElectronico("juan@example.com")
                .salarioBase(new BigDecimal("2000000.00"))
                .build();
    }

    private User sampleDomain() {
        return User.builder()
                .id("1")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 5, 10))
                .direccion("Calle 123")
                .telefono("3001234567")
                .correoElectronico("juan@example.com")
                .salarioBase(new BigDecimal("2000000.00"))
                .build();
    }


    @Test
    void mustFindValueById() {
        UserData data = sampleData();
        User domain = sampleDomain();

        when(repository.findById("1")).thenReturn(Mono.just(data));
        when(userMapper.toDomain(data)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getCorreoElectronico().equals("juan@example.com"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserData data = sampleData();
        User domain = sampleDomain();

        when(repository.findAll()).thenReturn(Flux.just(data));
        when(userMapper.toDomain(data)).thenReturn(domain);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getNombres().equals("Juan"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        UserData data = sampleData();
        User domain = sampleDomain();

        when(userMapper.toData(domain)).thenReturn(data);
        when(repository.save(data)).thenReturn(Mono.just(data));
        when(userMapper.toDomain(data)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void mustFindByCorreoElectronico() {
        UserData data = sampleData();
        User domain = sampleDomain();

        when(repository.findByCorreoElectronico("juan@example.com")).thenReturn(Mono.just(data));
        when(userMapper.toDomain(data)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findByCorreoElectronico("juan@example.com");

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getCorreoElectronico().equals("juan@example.com"))
                .verifyComplete();
    }

}
