package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.SavingGoalDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.Frequency;
import com.unimag.ahorroplusia.entity.enums.GoalStatus;
import com.unimag.ahorroplusia.entity.enums.PaymentMethod;
import com.unimag.ahorroplusia.entity.enums.Priority;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.SavingGoalMapper;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.SavingsGoalRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.RecommendationService;
import com.unimag.ahorroplusia.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingGoalMapper savingGoalMapper;
    private final RecommendationService recommendationService;

    @Override
    @Transactional
    public SavingGoalDTO createSavingsGoal(SavingGoalDTO dto, Long userId) {
        //  Buscar al usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        //  Mapear y configurar la meta
        SavingsGoal goal = savingGoalMapper.savingGoalDTOToSavingGoal(dto);
        goal.setUser(user);
        goal.setCreationDate(LocalDateTime.now());
        goal.setModificationDate(LocalDateTime.now());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setStatus(GoalStatus.ACTIVE);

        // Configurar frecuencia por defecto si es nula
        if (goal.getFrequency() == null) {
            goal.setFrequency(Frequency.MONTHLY);
        }
        //  Guardar la meta en la base de datos primero
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        //  Generar recomendación automática (Nueva funcionalidad)
        // Se envuelve en try-catch para que si la IA falla, la meta se cree de todos modos.
        try {
            String detail = String.format("Meta: %s, Objetivo: $%s, Fecha límite: %s",
                    savedGoal.getName(), savedGoal.getTargetAmount(), savedGoal.getEndDate());

            // Llamamos al nuevo método específico con tipo "GOAL"
            recommendationService.generateSpecificRecommendation(userId, "GOAL", detail);
        } catch (Exception e) {
            // Solo logueamos el error, no interrumpimos la creación de la meta
            System.err.println("Advertencia: No se pudo generar la recomendación para la nueva meta: " + e.getMessage());
        }
        //  Retornar el DTO
        return savingGoalMapper.savingGoalToSavingGoalDTO(savedGoal);
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

        // LOGICA DE ELIMINACIÓN:
        // Si la meta NO está completada y tiene dinero, devolvemos el dinero (Ingreso).
        // Si la meta ESTÁ completada, se asume que el dinero se usó para el objetivo (Gasto consolidado).
        if (goal.getStatus() != GoalStatus.COMPLETED && goal.getCurrentAmount().compareTo(BigDecimal.ZERO) > 0) {
            User user = goal.getUser();
            BigDecimal refundAmount = goal.getCurrentAmount();

            // 1. Registrar Ingreso (Devolución)
            Income refund = Income.builder()
                    .amount(refundAmount)
                    .date(LocalDate.now())
                    .source("Reembolso Meta: " + goal.getName())
                    .description("Reembolso por eliminación de meta no completada")
                    .creationDate(LocalDateTime.now())
                    .user(user)
                    .build();
            incomeRepository.save(refund);

            // 2. Devolver saldo al usuario
            BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
            user.setCurrentAvailableMoney(currentBalance.add(refundAmount).doubleValue());
            userRepository.save(user);
        }

        savingsGoalRepository.delete(goal);
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
    // agregar fondos a la meta
    public SavingGoalDTO addFunds(Integer idGoal, BigDecimal amount, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        SavingsGoal goal = getGoalEntity(idGoal, userId);
        User user = goal.getUser();

        // Validar saldo disponible
        BigDecimal userBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
        if (userBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente. Disponible: $" + userBalance);
        }

        /*  Registrar GASTO (El ahorro sale del bolsillo)
        Expense savingsExpense = Expense.builder()
                .amount(amount)
                .date(LocalDate.now())
                .method(PaymentMethod.TRANSFERENCIA)
                .description("Ahorro para meta: " + goal.getName())
                .anomalous(false)
                .overlimit(false)
                .creationDate(LocalDateTime.now())
                .user(user)
                .build();
        expenseRepository.save(savingsExpense);
        */


        // Restar del saldo del usuario
        user.setCurrentAvailableMoney(userBalance.subtract(amount).doubleValue());
        userRepository.save(user);

        // Sumar a la meta
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        checkCompletion(goal);

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    // retirar fondos de la meta
    public SavingGoalDTO withdrawFunds(Integer idGoal, BigDecimal amount, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a 0");
        }

        SavingsGoal goal = getGoalEntity(idGoal, userId);

        // REGLA DE PRIORIDAD ALTA: Solo retirar si está completada
        if (goal.getPriority() == Priority.HIGH && goal.getStatus() != GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Las metas de prioridad ALTA no permiten retiros parciales hasta ser completadas.");
        }

        // Validar fondos
        if (goal.getCurrentAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la meta.");
        }

        User user = goal.getUser();

        /* Registrar INGRESO (El dinero vuelve al bolsillo)
        Income withdrawalIncome = Income.builder()
                .amount(amount)
                .date(LocalDate.now())
                .source("Retiro de Meta")
                .description("Retiro de fondos de meta: " + goal.getName())
                .creationDate(LocalDateTime.now())
                .user(user)
                .build();
        incomeRepository.save(withdrawalIncome);
        */

        //  Sumar al saldo del usuario
        BigDecimal userBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
        user.setCurrentAvailableMoney(userBalance.add(amount).doubleValue());
        userRepository.save(user);

        //  Restar de la meta
        goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount));

        // Si baja del objetivo, volver a estado activo
        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    // Métodos auxiliares
    private SavingsGoal getGoalEntity(Integer idGoal, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(idGoal)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }
        return goal;
    }

    private void checkCompletion(SavingsGoal goal) {
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
    }
}