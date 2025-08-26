package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.SolicitudUsuario;
import co.com.pragma.bootcamp.api.dto.RespuestaUsuario;
import co.com.pragma.bootcamp.model.user.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapeadorUsuarioDto {
    Usuario aDominio(SolicitudUsuario solicitud);
    RespuestaUsuario aRespuesta(Usuario usuario);
}