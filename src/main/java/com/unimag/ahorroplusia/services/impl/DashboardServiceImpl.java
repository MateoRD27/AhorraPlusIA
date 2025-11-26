package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.dashboard.*;
import com.unimag.ahorroplusia.entity.entities.*;
import com.unimag.ahorroplusia.repository.*;
import com.unimag.ahorroplusia.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardStatsDto getDashboardStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfMonth.minusDays(1);

        Double monthlyIncome = incomeRepository.sumByUserAndDateBetween(user, startOfMonth, now);
        Double monthlyExpenses = expenseRepository.sumByUserAndDateBetween(user, startOfMonth, now);

        Double lastMonthIncome = incomeRepository.sumByUserAndDateBetween(user, startOfLastMonth, endOfLastMonth);
        Double lastMonthExpenses = expenseRepository.sumByUserAndDateBetween(user, startOfLastMonth, endOfLastMonth);

        Double incomeChange = calculatePercentageChange(lastMonthIncome, monthlyIncome);
        Double expenseChange = calculatePercentageChange(lastMonthExpenses, monthlyExpenses);

        double safeMonthlyIncome = monthlyIncome != null ? monthlyIncome : 0.0;
        double safeMonthlyExpenses = monthlyExpenses != null ? monthlyExpenses : 0.0;

        Double savingsPercentage = safeMonthlyIncome > 0
                ? ((safeMonthlyIncome - safeMonthlyExpenses) / safeMonthlyIncome) * 100
                : 0.0;

        Double currentBalance = user.getCurrentAvailableMoney();
        if (currentBalance == null) {
            Double totalIncome = incomeRepository.sumByUserAndDateBetween(
                    user, LocalDate.of(2000, 1, 1), now);
            Double totalExpenses = expenseRepository.sumByUserAndDateBetween(
                    user, LocalDate.of(2000, 1, 1), now);

            double safeTotalIncome = totalIncome != null ? totalIncome : 0.0;
            double safeTotalExpenses = totalExpenses != null ? totalExpenses : 0.0;

            currentBalance = safeTotalIncome - safeTotalExpenses;
        }

        return DashboardStatsDto.builder()
                .currentBalance(currentBalance)
                .monthlyIncome(safeMonthlyIncome)
                .monthlyExpenses(safeMonthlyExpenses)
                .savingsPercentage(savingsPercentage)
                .incomeChange(incomeChange)
                .expenseChange(expenseChange)
                .build();
    }

    private Double calculatePercentageChange(Double oldValue, Double newValue) {
        double safeOldValue = oldValue != null ? oldValue : 0.0;
        double safeNewValue = newValue != null ? newValue : 0.0;

        if (safeOldValue == 0.0) {
            return safeNewValue > 0.0 ? 100.0 : 0.0;
        }

        return ((safeNewValue - safeOldValue) / safeOldValue) * 100;
    }

    public List<MonthlyDataDto> getMonthlyData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate now = LocalDate.now();
        List<MonthlyDataDto> monthlyData = new ArrayList<>();

        for (int i = 4; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            LocalDate startOfMonth = monthDate.withDayOfMonth(1);
            LocalDate endOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth());

            Double ingresos = incomeRepository.sumByUserAndDateBetween(user, startOfMonth, endOfMonth);
            Double gastos = expenseRepository.sumByUserAndDateBetween(user, startOfMonth, endOfMonth);

            String monthName = monthDate.getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));

            monthlyData.add(MonthlyDataDto.builder()
                    .month(monthName)
                    .ingresos(ingresos != null ? ingresos : 0.0)
                    .gastos(gastos != null ? gastos : 0.0)
                    .build());
        }

        return monthlyData;
    }

    public List<PieDataDto> getPieData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        Double monthlyIncome = incomeRepository.sumByUserAndDateBetween(user, startOfMonth, now);
        Double monthlyExpenses = expenseRepository.sumByUserAndDateBetween(user, startOfMonth, now);

        return Arrays.asList(
                PieDataDto.builder().name("Ingresos").value(monthlyIncome != null ? monthlyIncome : 0.0).build(),
                PieDataDto.builder().name("Gastos").value(monthlyExpenses != null ? monthlyExpenses : 0.0).build()
        );
    }

    public List<RecentTransactionDto> getRecentTransactions(String email, int limit) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RecentTransactionDto> transactions = new ArrayList<>();

        // Obtener ingresos recientes
        List<Income> recentIncomes = incomeRepository.findTop10ByUserOrderByDateDesc(user);
        recentIncomes.forEach(income -> transactions.add(
                RecentTransactionDto.builder()
                        .id(income.getIdIncome())
                        .type("ingreso")
                        .description(income.getDescription() != null ? income.getDescription() : income.getSource())
                        .amount(income.getAmount().doubleValue())
                        .date(income.getDate().toString())
                        .build()
        ));

        // Obtener gastos recientes
        List<Expense> recentExpenses = expenseRepository.findTop10ByUserOrderByDateDesc(user);
        recentExpenses.forEach(expense -> transactions.add(
                RecentTransactionDto.builder()
                        .id(expense.getIdExpense())
                        .type("gasto")
                        .description(expense.getDescription())
                        .amount(expense.getAmount().doubleValue())
                        .date(expense.getDate().toString())
                        .build()
        ));

        // Ordenar por fecha y limitar
        return transactions.stream()
                .sorted(Comparator.comparing(RecentTransactionDto::getDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

}