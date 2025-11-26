package com.unimag.ahorroplusia.dto;

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
public class IncomeDTO {
    // CORRECCIÓN: Agregar este campo
    private Integer idIncome;

    private BigDecimal amount;
    private LocalDate date;
    private String source;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private UserDto userDto;
}