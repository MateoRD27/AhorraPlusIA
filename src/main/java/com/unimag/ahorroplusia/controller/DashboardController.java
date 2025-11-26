package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.dto.dashboard.*;
import com.unimag.ahorroplusia.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        DashboardStatsDto stats = dashboardService.getDashboardStats(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/monthly-data")
    public ResponseEntity<ApiResponse<List<MonthlyDataDto>>> getMonthlyData(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MonthlyDataDto> data = dashboardService.getMonthlyData(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/pie-data")
    public ResponseEntity<ApiResponse<List<PieDataDto>>> getPieData(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PieDataDto> data = dashboardService.getPieData(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<ApiResponse<List<RecentTransactionDto>>> getRecentTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "4") int limit) {
        List<RecentTransactionDto> transactions = dashboardService.getRecentTransactions(
                userDetails.getUsername(), limit);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}