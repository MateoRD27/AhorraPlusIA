package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.UserSettingDto;
import com.unimag.ahorroplusia.entity.entities.UserSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSettingMapper {
    UserSetting userSettingDTOToUserSetting(UserSetting userSetting);
    UserSettingDto userSettingToUserSettingDTO(UserSettingDto userSettingDto);
}
