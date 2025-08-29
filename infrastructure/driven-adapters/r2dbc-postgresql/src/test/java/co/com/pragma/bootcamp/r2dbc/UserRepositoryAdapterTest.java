package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserEntityRepository userEntityRepository;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionalOperator transactionalOperator;

    private UserEntity sampleData;
    private User sampleDomain;


    @BeforeEach
    void setUp() {
        sampleData = new UserEntity();
        sampleData.setId("1");
        sampleData.setFirstName("Juan");
        sampleData.setLastName("Pérez");
        sampleData.setEmail("juan@example.com");
        sampleData.setBaseSalary(new BigDecimal("2000000.00"));

        sampleDomain = User.builder()
                .id("1")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                .baseSalary(new BigDecimal("2000000.00"))
                .build();
    }

    @Test
    void existsByEmailOrIdentificationDocument_shouldReturnTrue_whenUserExists() {
        when(userEntityRepository
                .findByEmailOrIdentificationDocument("test@example.com", "12345"))
                .thenReturn(Flux.just(sampleData));

        Mono<Boolean> result = repositoryAdapter
                .existsByEmailOrIdentificationDocument("test@example.com", "12345");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmailOrIdentificationDocument_shouldReturnFalse_whenUserDoesNotExist() {
        when(userEntityRepository
                .findByEmailOrIdentificationDocument("no-user@example.com", "999"))
                .thenReturn(Flux.empty());

        Mono<Boolean> result = repositoryAdapter
                .existsByEmailOrIdentificationDocument("no-user@example.com", "999");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByIdentificationDocument_shouldReturnUser_whenUserExists() {
        String document = "1030";
        when(userEntityRepository.findByIdentificationDocument(document))
                .thenReturn(Mono.just(sampleData));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(sampleDomain);

        Mono<User> result = repositoryAdapter.findByIdentificationDocument(document);

        StepVerifier.create(result)
                .expectNext(sampleDomain)
                .verifyComplete();
    }

    @Test
    void findByIdentificationDocument_shouldReturnEmptyMono_whenUserDoesNotExist() {
        String document = "9999";
        when(userEntityRepository.findByIdentificationDocument(document))
                .thenReturn(Mono.empty());

        Mono<User> result = repositoryAdapter.findByIdentificationDocument(document);

        StepVerifier.create(result)
                .expectComplete();
    }

    @Test
    void save_shouldReturnUser_whenSaveIsSuccessful() {
        when(mapper.map(any(User.class), any())).thenReturn(sampleData);
        when(mapper.map(any(UserEntity.class), any())).thenReturn(sampleDomain);
        when(transactionalOperator.execute(any())).thenReturn(Flux.just(sampleData));

        Mono<User> result = repositoryAdapter.save(sampleDomain);

        StepVerifier.create(result)
                .expectNext(sampleDomain)
                .verifyComplete();
    }

    @Test
    void save_shouldReturnError_whenTransactionFails() {
        when(mapper.map(any(User.class), any())).thenReturn(sampleData);
        when(transactionalOperator.execute(any())).thenReturn(Flux.error(new RuntimeException("Simulated database error")));

        Mono<User> result = repositoryAdapter.save(sampleDomain);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}