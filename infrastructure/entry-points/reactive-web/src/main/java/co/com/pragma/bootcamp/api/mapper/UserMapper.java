package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserRequest solicitud);
    UserResponse toResponse(User user);
}