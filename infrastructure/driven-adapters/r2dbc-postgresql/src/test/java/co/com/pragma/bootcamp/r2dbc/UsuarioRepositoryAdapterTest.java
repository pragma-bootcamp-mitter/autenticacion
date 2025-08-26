package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.r2dbc.entity.UserData;
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
class UsuarioRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserDataRepository repository;

    @Mock
    ObjectMapper mapper;

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

    private Usuario sampleDomain() {
        return Usuario.builder()
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
    void debeEncontrarUsuarioPorId() {
        UserData data = sampleData();
        Usuario domain = sampleDomain();

        when(repository.findById("1")).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, Usuario.class)).thenReturn(domain);

        Mono<Usuario> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getNombres().equals("Juan") &&
                                u.getApellidos().equals("Pérez") &&
                                u.getCorreoElectronico().equals("juan@example.com") &&
                                u.getSalarioBase().equals(new BigDecimal("2000000.00"))
                )
                .verifyComplete();
    }

    @Test
    void debeListarTodosLosUsuarios() {
        UserData data = sampleData();
        Usuario domain = sampleDomain();

        when(repository.findAll()).thenReturn(Flux.just(data));
        lenient().when(mapper.map(data, Usuario.class)).thenReturn(domain);

        Flux<Usuario> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getNombres().equals("Juan") &&
                                u.getApellidos().equals("Pérez")
                )
                .verifyComplete();
    }

    @Test
    void debeGuardarUsuario() {
        UserData data = sampleData();
        Usuario domain = sampleDomain();

        lenient().when(mapper.map(domain, UserData.class)).thenReturn(data);
        when(repository.save(data)).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, Usuario.class)).thenReturn(domain);

        Mono<Usuario> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getCorreoElectronico().equals("juan@example.com")
                )
                .verifyComplete();
    }


    @Test
    void debeVerificarExistenciaPorCorreoElectronico() {
        UserData data = sampleData();

        when(repository.findByCorreoElectronico("juan@example.com")).thenReturn(Mono.just(data));

        Mono<Boolean> result = repositoryAdapter.existePorCorreoElectronico("juan@example.com");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void debeBuscarUsuarioPorDocumentoIdentidad() {
        UserData data = sampleData();
        Usuario domain = sampleDomain();

        when(repository.findByDocumentoIdentidad("123456")).thenReturn(Mono.just(data));
        lenient().when(mapper.map(data, Usuario.class)).thenReturn(domain);

        Mono<Usuario> result = repositoryAdapter.buscarPorDocumentoIdentidad("123456");

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getId().equals("1") &&
                                u.getNombres().equals("Juan") &&
                                u.getCorreoElectronico().equals("juan@example.com")
                )
                .verifyComplete();
    }
}