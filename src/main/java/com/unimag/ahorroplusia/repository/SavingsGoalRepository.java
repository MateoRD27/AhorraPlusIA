package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import com.unimag.ahorroplusia.entity.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Integer> {

    List<SavingsGoal> findByUserId(Long userId);

    List<SavingsGoal> findByStatus(GoalStatus status);
}
