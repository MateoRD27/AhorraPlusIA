package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ExpenseDTO {
    // CORRECCIÓN: Agregar este campo
    private Integer idExpense;

    private BigDecimal amount;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private String description;
    private Boolean anomalous = false;
    private Boolean overlimit = false;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private UserDto userDto;
}