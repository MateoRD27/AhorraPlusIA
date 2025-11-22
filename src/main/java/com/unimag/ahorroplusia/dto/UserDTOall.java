package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
