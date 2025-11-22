package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.Frequency;
import com.unimag.ahorroplusia.entity.enums.GoalStatus;
import com.unimag.ahorroplusia.entity.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalDTO {
    private String name;

    private BigDecimal targetAmount; // cantidad objetivo

    private BigDecimal currentAmount = BigDecimal.ZERO; // cantidad actual

    private LocalDate startDate; // fecha de inicio

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    private BigDecimal suggestedQuota;

    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;

    private Boolean deletable = true;

    private Integer daysToDelete;    // dias para eliminar

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    private UserDto userDto;
}
