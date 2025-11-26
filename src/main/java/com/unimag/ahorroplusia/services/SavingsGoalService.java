package com.unimag.ahorroplusia.services;


import com.unimag.ahorroplusia.dto.SavingGoalDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsGoalService {
    // CRUD Básico
    SavingGoalDTO createSavingsGoal(SavingGoalDTO dto, Long userId);
    SavingGoalDTO updateSavingsGoal(Integer idGoal, SavingGoalDTO dto, Long userId);
    void deleteSavingsGoal(Integer idGoal, Long userId);

    // Consultas
    List<SavingGoalDTO> getAllGoalsByUser(Long userId);
    SavingGoalDTO getGoalById(Integer idGoal, Long userId);

    // Operaciones Financieras
    SavingGoalDTO addFunds(Integer idGoal, BigDecimal amount, Long userId); //verificamos si se alcanzó la meta para cambiar el estado a COMPLETED
    SavingGoalDTO withdrawFunds(Integer idGoal, BigDecimal amount, Long userId);//verificamos que no saquen más de lo que tienen ahorrado.
}
