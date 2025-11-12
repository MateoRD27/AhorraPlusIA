package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.LoginDTO;
import com.unimag.ahorroplusia.entity.entities.Login;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    LoginDTO loginDtoToLoginDTO(Login login);
    Login loginDTOToLoginDto(LoginDTO loginDTO);
}
