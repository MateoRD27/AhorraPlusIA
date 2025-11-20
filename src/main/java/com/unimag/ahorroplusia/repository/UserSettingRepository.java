package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {

    UserSetting findByUserId(Long userId);
}
