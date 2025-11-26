package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.SavingGoalDTO;
import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.Frequency;
import com.unimag.ahorroplusia.entity.enums.GoalStatus;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.SavingGoalMapper;
import com.unimag.ahorroplusia.repository.SavingsGoalRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    @Transactional
    public SavingGoalDTO createSavingsGoal(SavingGoalDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        SavingsGoal goal = savingGoalMapper.savingGoalDTOToSavingGoal(dto);
        goal.setUser(user);
        goal.setCreationDate(LocalDateTime.now());
        goal.setModificationDate(LocalDateTime.now());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setStatus(GoalStatus.ACTIVE);

        // Valor por defecto si no viene en el DTO
        if (goal.getFrequency() == null) {
            goal.setFrequency(Frequency.MONTHLY);
        }

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingGoalDTO> getAllGoalsByUser(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
                .map(savingGoalMapper::savingGoalToSavingGoalDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SavingGoalDTO getGoalById(Integer idGoal, Long userId) {
        return savingGoalMapper.savingGoalToSavingGoalDTO(getGoalEntity(idGoal, userId));
    }

    @Override
    @Transactional
    public SavingGoalDTO updateSavingsGoal(Integer idGoal, SavingGoalDTO dto, Long userId) {
        SavingsGoal goal = getGoalEntity(idGoal, userId);

        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setEndDate(dto.getEndDate());
        goal.setPriority(dto.getPriority());
        goal.setModificationDate(LocalDateTime.now());

        checkCompletion(goal);

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteSavingsGoal(Integer idGoal, Long userId) {
        SavingsGoal goal = getGoalEntity(idGoal, userId);

        // IMPORTANTE: Antes de borrar, devolvemos el dinero al usuario si la meta tenía fondos
        if (goal.getCurrentAmount().compareTo(BigDecimal.ZERO) > 0) {
            User user = goal.getUser();
            BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
            BigDecimal newBalance = currentBalance.add(goal.getCurrentAmount());

            user.setCurrentAvailableMoney(newBalance.doubleValue());
            userRepository.save(user);
        }

        savingsGoalRepository.delete(goal);
    }

    @Override
    @Transactional
    public SavingGoalDTO addFunds(Integer idGoal, BigDecimal amount, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        SavingsGoal goal = getGoalEntity(idGoal, userId);
        User user = goal.getUser();

        // 1. Verificar si el usuario tiene saldo suficiente
        BigDecimal userBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());

        if (userBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente en tu cuenta disponible. Tienes: " + userBalance);
        }

        // 2. Restar del bolsillo del usuario
        user.setCurrentAvailableMoney(userBalance.subtract(amount).doubleValue());
        userRepository.save(user);

        // 3. Sumar a la meta
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        checkCompletion(goal);

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public SavingGoalDTO withdrawFunds(Integer idGoal, BigDecimal amount, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        SavingsGoal goal = getGoalEntity(idGoal, userId);

        // 1. Verificar si la meta tiene fondos suficientes
        if (goal.getCurrentAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("La meta no tiene fondos suficientes para este retiro.");
        }

        // 2. Restar de la meta
        goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount));

        // 3. Devolver dinero al bolsillo del usuario
        User user = goal.getUser();
        BigDecimal userBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
        user.setCurrentAvailableMoney(userBalance.add(amount).doubleValue());
        userRepository.save(user);

        // Si se retiró dinero y la meta estaba completa, volver a ponerla activa
        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    // --- Métodos Auxiliares ---

    private SavingsGoal getGoalEntity(Integer idGoal, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(idGoal)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de ahorro no encontrada con ID: " + idGoal));

        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tiene permisos para acceder a esta meta.");
        }
        return goal;
    }

    private void checkCompletion(SavingsGoal goal) {
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else {
            // Si por alguna razón editamos el objetivo y ahora es mayor, volvemos a activa
            if (goal.getStatus() == GoalStatus.COMPLETED) {
                goal.setStatus(GoalStatus.ACTIVE);
            }
        }
    }
}