package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.adapter.UserRepositoryAdapter;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import co.com.pragma.bootcamp.r2dbc.mapper.UserEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import org.springframework.transaction.reactive.TransactionCallback;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User sampleDomain;
    private UserEntity sampleData;

    @BeforeEach
    void setUp() {
        sampleData = new UserEntity();
        sampleData.setId("1");
        sampleData.setFirstName("Juan");
        sampleData.setLastName("Pérez");
        sampleData.setEmail("juan@example.com");
        sampleData.setRoleId(1);
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

        Mono<Boolean> result = userRepositoryAdapter
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

        Mono<Boolean> result = userRepositoryAdapter
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

        Mono<User> result = userRepositoryAdapter.findByIdentificationDocument(document);

        StepVerifier.create(result)
                .expectNext(sampleDomain)
                .verifyComplete();
    }

    @Test
    void findByIdentificationDocument_shouldReturnEmptyMono_whenUserDoesNotExist() {
        String document = "9999";
        when(userEntityRepository.findByIdentificationDocument(document))
                .thenReturn(Mono.empty());

        Mono<User> result = userRepositoryAdapter.findByIdentificationDocument(document);

        StepVerifier.create(result)
                .expectComplete();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(userEntityMapper.toEntity(sampleDomain)).thenReturn(sampleData);
        when(userEntityRepository.save(sampleData)).thenReturn(Mono.just(sampleData));
        when(userEntityMapper.toDomain(sampleData)).thenReturn(sampleDomain);

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<UserEntity> callback = invocation.getArgument(0);
                    return Flux.defer(() -> callback.doInTransaction(mock(ReactiveTransaction.class)));
                });

        Mono<User> result = userRepositoryAdapter.save(sampleDomain);

        StepVerifier.create(result)
                .expectNext(sampleDomain)
                .verifyComplete();

        verify(userEntityMapper).toEntity(sampleDomain);
        verify(userEntityRepository).save(sampleData);
        verify(userEntityMapper).toDomain(sampleData);
    }

    @Test
    void shouldHandleSaveError() {
        when(userEntityMapper.toEntity(sampleDomain)).thenReturn(sampleData);
        when(transactionalOperator.execute(any()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        Mono<User> result = userRepositoryAdapter.save(sampleDomain);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("DB error"))
                .verify();

        verify(userEntityMapper).toEntity(sampleDomain);
        verify(userEntityRepository, never()).save(any());
        verify(userEntityMapper, never()).toDomain(any());
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        String email = "juan@example.com";
        when(userEntityRepository.findByEmail(email)).thenReturn(Mono.just(sampleData));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(sampleDomain);

        Mono<User> result = userRepositoryAdapter.findByEmail(email);

        StepVerifier.create(result)
                .expectNext(sampleDomain)
                .verifyComplete();
        verify(userEntityRepository, times(1)).findByEmail(email);
        verify(mapper, times(1)).map(sampleData, User.class);
    }

    @Test
    void findByEmail_shouldReturnEmptyMono_whenUserDoesNotExist() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        Mono<User> result = userRepositoryAdapter.findByEmail("no-existe@mail.com");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userEntityRepository).findByEmail("no-existe@mail.com");
        verify(userEntityMapper, never()).toDomain(any());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId("1");
        userEntity1.setEmail("user1@example.com");

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId("2");
        userEntity2.setEmail("user2@example.com");

        User userDomain1 = User.builder().id("1").email("user1@example.com").build();
        User userDomain2 = User.builder().id("2").email("user2@example.com").build();

        when(userEntityRepository.findAll()).thenReturn(Flux.just(userEntity1, userEntity2));
        when(userEntityMapper.toDomain(userEntity1)).thenReturn(userDomain1);
        when(userEntityMapper.toDomain(userEntity2)).thenReturn(userDomain2);

        Flux<User> result = userRepositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(userDomain1)
                .expectNext(userDomain2)
                .verifyComplete();

        verify(userEntityRepository, times(1)).findAll();
        verify(userEntityMapper, times(2)).toDomain(any(UserEntity.class));
    }
}