package com.unimag.ahorroplusia.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Double currentBalance;
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double savingsPercentage;
    private Double incomeChange;
    private Double expenseChange;
}
