package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.user.User;
import co.com.pragma.bootcamp.r2dbc.entity.UserData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserData toData(User user);
    User toDomain(UserData data);
}
