package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserEntityRepository repository;

    @Mock
    ObjectMapper mapper;

    private UserEntity sampleData() {
        return UserEntity.builder()
                .id("1")
                .firstName("Juan")
                .lastName("Pérez")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@example.com")
                .baseSalary(new BigDecimal("2000000.00"))
                .build();
    }

    private User sampleDomain() {
        return User.builder()
                .id("1")
                .firstName("Juan")
                .lastName("Pérez")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@example.com")
                .baseSalary(new BigDecimal("2000000.00"))
                .build();
    }

    @Test
    void shouldFindUserById() {
        UserEntity data = sampleData();
        User domain = sampleDomain();

        when(repository.findById("1")).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getFirstName().equals("Juan") &&
                                u.getLastName().equals("Pérez") &&
                                u.getEmail().equals("juan@example.com") &&
                                u.getBaseSalary().equals(new BigDecimal("2000000.00"))
                )
                .verifyComplete();
    }

    @Test
    void shouldListAllUsers() {
        UserEntity data = sampleData();
        User domain = sampleDomain();

        when(repository.findAll()).thenReturn(Flux.just(data));
        lenient().when(mapper.map(data, User.class)).thenReturn(domain);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getFirstName().equals("Juan") &&
                                u.getLastName().equals("Pérez")
                )
                .verifyComplete();
    }

    @Test
    void shouldSaveUser() {
        UserEntity data = sampleData();
        User domain = sampleDomain();

        lenient().when(mapper.map(domain, UserEntity.class)).thenReturn(data);
        when(repository.save(data)).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getEmail().equals("juan@example.com")
                )
                .verifyComplete();
    }


    @Test
    void shouldVerifyExistenceByEmail() {
        UserEntity data = sampleData();

        when(repository.findByEmail("juan@example.com")).thenReturn(Mono.just(data));

        Mono<Boolean> result = repositoryAdapter.existsByEmail("juan@example.com");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldFindUserByIdentificationDocument() {
        UserEntity data = sampleData();
        User domain = sampleDomain();

        when(repository.findByIdentificationDocument("123456")).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findByIdentificationDocument("123456");

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getFirstName().equals("Juan") &&
                                u.getEmail().equals("juan@example.com")
                )
                .verifyComplete();
    }
}