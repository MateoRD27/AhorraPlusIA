// IncomeRepository.java
package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

    List<Income> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId")
    BigDecimal getTotalIncome(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :start AND :end")
    BigDecimal getIncomeBetweenDates(@Param("userId") Long userId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);
}