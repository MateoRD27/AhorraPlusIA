package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.reports.FinancialReportResponse;

public interface ReportService {
    FinancialReportResponse getFinancialReport(Long userId, String period);
}
