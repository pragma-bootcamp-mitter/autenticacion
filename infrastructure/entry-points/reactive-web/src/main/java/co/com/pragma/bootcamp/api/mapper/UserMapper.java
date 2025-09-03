package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.user.UserRequest;
import co.com.pragma.bootcamp.api.dto.user.UserResponse;
import co.com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //@Mapping(source = "roleId", target = "role.id")
    User toDomain(UserRequest request);

    //@Mapping(source = "role.id", target = "roleId")
    UserResponse toResponse(User user);
}