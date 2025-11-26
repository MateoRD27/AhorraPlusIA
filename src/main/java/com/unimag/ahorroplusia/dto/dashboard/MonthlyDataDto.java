package com.unimag.ahorroplusia.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDataDto {
    private String month;
    private Double ingresos;
    private Double gastos;
}