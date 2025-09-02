package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    //@Mapping(target = "roleId", source = "role.id")
    UserEntity toEntity(User user);

    //@Mapping(target = "role.id", source = "roleId")
    User toDomain(UserEntity entity);
}