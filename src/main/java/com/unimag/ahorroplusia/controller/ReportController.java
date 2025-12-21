package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.dto.reports.FinancialReportResponse;
import com.unimag.ahorroplusia.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{userId}")
    public ResponseEntity<FinancialReportResponse> getReport(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "6m") String period) {
        return ResponseEntity.ok(reportService.getFinancialReport(userId, period));
    }
}