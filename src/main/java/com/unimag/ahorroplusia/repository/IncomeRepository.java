package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

    List<Income> findByUserId(Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    BigDecimal getTotalIncome(Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :start AND :end")
    BigDecimal getIncomeBetweenDates(Long userId, LocalDate start, LocalDate end);
}
