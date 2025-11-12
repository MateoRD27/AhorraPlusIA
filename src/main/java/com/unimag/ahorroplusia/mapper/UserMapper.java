package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.UserDTOall;
import com.unimag.ahorroplusia.dto.UserDto;
import com.unimag.ahorroplusia.entity.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
    UserDTOall userToUserDTOall(User user);
    User userDtoToUser(UserDto userDto);
    User userDTOallToUser(UserDTOall userDTOall);
}
