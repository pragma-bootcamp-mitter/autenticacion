package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.UserRepository;
import co.com.pragma.bootcamp.r2dbc.entity.UserData;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<Usuario, UserData, String, UserDataRepository>
        implements UserRepository {

    private final UserDataRepository userDataRepository;
    private final ObjectMapper mapper;

    public UserRepositoryAdapter(UserDataRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
        this.userDataRepository = repository;
        this.mapper = mapper;
    }

    @Override
    protected UserData toData(Usuario entity) {
        return mapper.map(entity, UserData.class);
    }

    @Override
    protected Usuario toEntity(UserData data) {
        return Usuario.builder()
                .id(data.getId())
                .documentoIdentidad(data.getDocumentoIdentidad())
                .nombres(data.getNombres())
                .apellidos(data.getApellidos())
                .fechaNacimiento(data.getFechaNacimiento())
                .direccion(data.getDireccion())
                .telefono(data.getTelefono())
                .correoElectronico(data.getCorreoElectronico())
                .salarioBase(data.getSalarioBase())
                .build();
    }

    public Mono<Boolean> existePorCorreoElectronico(String correoElectronico) {
        return userDataRepository.findByCorreoElectronico(correoElectronico)
                .hasElement();
    }

    @Override
    public Mono<Usuario> buscarPorDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .map(this::toEntity);
    }
}
