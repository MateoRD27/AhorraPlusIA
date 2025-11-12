package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserDTOall {
    private String name;

    private String email;

    private String password;

    private Double fixedSalary;

    private Double currentAvailableMoney;

    LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus;

    Boolean verifiedAccount;

    LocalDateTime lastAccessDate;

    LocalDateTime creationDate;

    LocalDateTime modificationDate;
}
