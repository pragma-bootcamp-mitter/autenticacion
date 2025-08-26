package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.model.user.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    Usuario toDomain(UserRequest request);
    UserResponse toResponse(Usuario usuario);
}