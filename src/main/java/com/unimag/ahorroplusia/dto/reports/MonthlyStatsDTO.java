package com.unimag.ahorroplusia.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsDTO {
    private String month; // Ej: "Ene", "Feb"
    private Double income;
    private Double expense;
}