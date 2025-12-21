package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.reports.FinancialReportResponse;
import com.unimag.ahorroplusia.dto.reports.MonthlyStatsDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public FinancialReportResponse getFinancialReport(Long userId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(period);

        List<Income> incomes = incomeRepository.findByUserId(userId).stream()
                .filter(i -> !i.getDate().isBefore(startDate))
                .collect(Collectors.toList());

        List<Expense> expenses = expenseRepository.findByUserId(userId).stream()
                .filter(e -> !e.getDate().isBefore(startDate))
                .collect(Collectors.toList());

        // 1. Estadísticas Mensuales
        List<MonthlyStatsDTO> monthlyStats = processMonthlyStats(incomes, expenses, startDate, endDate);

        // 2. Totales
        Double totalIncome = incomes.stream()
                .map(Income::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        Double totalExpense = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        return FinancialReportResponse.builder()
                .monthlyStats(monthlyStats)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(totalIncome - totalExpense)
                .build();
    }

    private LocalDate calculateStartDate(String period) {
        return switch (period) {
            case "1m" -> LocalDate.now().minusMonths(1);
            case "3m" -> LocalDate.now().minusMonths(3);
            case "1y" -> LocalDate.now().withDayOfYear(1);
            default -> LocalDate.now().minusMonths(6);
        };
    }

    private List<MonthlyStatsDTO> processMonthlyStats(List<Income> incomes, List<Expense> expenses, LocalDate start, LocalDate end) {
        Map<String, MonthlyStatsDTO> statsMap = new LinkedHashMap<>();
        Locale localeEs = Locale.forLanguageTag("es-ES");

        LocalDate current = start.withDayOfMonth(1);
        while (!current.isAfter(end)) {
            String monthName = current.getMonth().getDisplayName(TextStyle.SHORT, localeEs);
            statsMap.put(monthName, new MonthlyStatsDTO(monthName, 0.0, 0.0));
            current = current.plusMonths(1);
        }

        incomes.forEach(i -> {
            String m = i.getDate().getMonth().getDisplayName(TextStyle.SHORT, localeEs);
            if (statsMap.containsKey(m)) {
                statsMap.get(m).setIncome(statsMap.get(m).getIncome() + i.getAmount().doubleValue());
            }
        });

        expenses.forEach(e -> {
            String m = e.getDate().getMonth().getDisplayName(TextStyle.SHORT, localeEs);
            if (statsMap.containsKey(m)) {
                statsMap.get(m).setExpense(statsMap.get(m).getExpense() + e.getAmount().doubleValue());
            }
        });

        return new ArrayList<>(statsMap.values());
    }
}