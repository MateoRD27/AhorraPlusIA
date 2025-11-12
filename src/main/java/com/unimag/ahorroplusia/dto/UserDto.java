package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String name;
    private String email;
}
