package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserId(Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpenses(Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    BigDecimal getExpensesBetweenDates(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT e FROM Expense e WHERE e.anomalous = true AND e.user.id = :userId")
    List<Expense> getAnomalousExpenses(Long userId);
}
