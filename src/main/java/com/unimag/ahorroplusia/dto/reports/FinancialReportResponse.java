package com.unimag.ahorroplusia.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportResponse {
    private List<MonthlyStatsDTO> monthlyStats;
    private Double totalIncome;
    private Double totalExpense;
    private Double netSavings;
}