// IncomeRepository.java
package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.entity.entities.User;
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


    // Encontrar los últimos N ingresos de un usuario
    List<Income> findTop10ByUserOrderByDateDesc(User user);

    // Sumar ingresos de un usuario entre fechas
    @Query("SELECT COALESCE(SUM(i.amount), 0.0) FROM Income i WHERE i.user = :user AND i.date BETWEEN :startDate AND :endDate")
    Double sumByUserAndDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}