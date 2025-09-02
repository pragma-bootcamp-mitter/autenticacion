package co.com.pragma.bootcamp.r2dbc.mapper;


import co.com.pragma.bootcamp.model.rol.Role;
import co.com.pragma.bootcamp.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleEntityMapper {

    @Mapping(source = "roleId", target = "id")
    Role toDomain(RoleEntity roleEntity);
}
