package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.dashboard.*;
import java.util.List;

public interface DashboardService {

    DashboardStatsDto getDashboardStats(String email);

    List<MonthlyDataDto> getMonthlyData(String email);

    List<PieDataDto> getPieData(String email);

    List<RecentTransactionDto> getRecentTransactions(String email, int limit);
}