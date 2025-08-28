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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserEntityRepository userEntityRepository;

    @Mock
    ObjectMapper mapper;

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
        // Given
        when(userEntityRepository
                .findByEmailOrIdentificationDocument("test@example.com", "12345"))
                .thenReturn(Flux.just(sampleData));

        // When
        Mono<Boolean> result = repositoryAdapter
                .existsByEmailOrIdentificationDocument("test@example.com", "12345");

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmailOrIdentificationDocument_shouldReturnFalse_whenUserDoesNotExist() {
        // Given
        when(userEntityRepository
                .findByEmailOrIdentificationDocument("no-user@example.com", "999"))
                .thenReturn(Flux.empty());

        // When
        Mono<Boolean> result = repositoryAdapter
                .existsByEmailOrIdentificationDocument("no-user@example.com", "999");

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void toData_shouldMapUserToUserEntity() {
        // Given
        User user = User.builder()
                .id("1")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                .identificationDocument("1030")
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setId("1");
        userEntity.setFirstName("Juan");
        userEntity.setLastName("Pérez");
        userEntity.setEmail("juan@example.com");
        userEntity.setIdentificationDocument("1030");

        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);

        // When
        UserEntity result = repositoryAdapter.toData(user);

        // Then
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getFirstName(), result.getFirstName());
        assertEquals(userEntity.getLastName(), result.getLastName());
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getIdentificationDocument(), result.getIdentificationDocument());
    }
}