// ExpenseRepository.java
package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpenses(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    BigDecimal getExpensesBetweenDates(@Param("userId") Long userId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.anomalous = true")
    List<Expense> getAnomalousExpenses(@Param("userId") Long userId);


    // Encontrar los últimos N gastos de un usuario
    List<Expense> findTop10ByUserOrderByDateDesc(User user);

    // Sumar gastos de un usuario entre fechas
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.user = :user AND e.date BETWEEN :startDate AND :endDate")
    Double sumByUserAndDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}