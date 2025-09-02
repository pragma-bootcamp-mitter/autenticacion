package co.com.pragma.bootcamp.api.mapper;

import co.com.pragma.bootcamp.api.dto.login.LoginResponse;
import co.com.pragma.bootcamp.model.login.LogIn;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogInMapper {

   LoginResponse toResponse(LogIn logIn);
}
