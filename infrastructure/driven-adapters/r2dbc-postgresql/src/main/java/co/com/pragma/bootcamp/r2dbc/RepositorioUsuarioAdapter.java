package co.com.pragma.bootcamp.r2dbc;

import co.com.pragma.bootcamp.model.user.Usuario;
import co.com.pragma.bootcamp.model.user.gateways.RepositorioUsuario;
import co.com.pragma.bootcamp.r2dbc.entidad.UsuarioEntidad;
import co.com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RepositorioUsuarioAdapter
        extends ReactiveAdapterOperations<Usuario, UsuarioEntidad, String, RepositorioDatosUsuario>
        implements RepositorioUsuario {

    private final RepositorioDatosUsuario repositorioDatosUsuario;
    private final ObjectMapper mapper;

    public RepositorioUsuarioAdapter(RepositorioDatosUsuario repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
        this.repositorioDatosUsuario = repository;
        this.mapper = mapper;
    }

    @Override
    protected UsuarioEntidad toData(Usuario entity) {
        return mapper.map(entity, UsuarioEntidad.class);
    }

    @Override
    protected Usuario toEntity(UsuarioEntidad data) {
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
        return repositorioDatosUsuario.findByCorreoElectronico(correoElectronico)
                .hasElement();
    }

    @Override
    public Mono<Usuario> buscarPorDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .map(this::toEntity);
    }
}
