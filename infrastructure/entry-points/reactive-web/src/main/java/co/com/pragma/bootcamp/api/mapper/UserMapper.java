package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.UserRequest;
import co.com.pragma.bootcamp.api.dto.UserResponse;
import co.com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roleId", target = "role.id")
    User toDomain(UserRequest request);

    @Mapping(source = "role.id", target = "roleId")
    UserResponse toResponse(User user);
}