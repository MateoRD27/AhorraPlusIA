package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.ExpenseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseDTO createExpense(ExpenseDTO dto, Long userId);

    ExpenseDTO updateExpense(Integer idExpense, ExpenseDTO dto, Long userId);

    void deleteExpense(Integer idExpense, Long userId);

    List<ExpenseDTO> getAllExpensesByUser(Long userId);

    BigDecimal getTotalExpenses(Long userId);

    BigDecimal getExpensesBetweenDates(Long userId, LocalDate start, LocalDate end);

    List<ExpenseDTO> getAnomalousExpenses(Long userId);
}
