package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.ExpenseDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.ExpenseMapper;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.ExpenseService;
import com.unimag.ahorroplusia.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;
    private final RecommendationService recommendationService;

    @Override
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
        boolean overLimit = dto.getAmount().compareTo(currentBalance) > 0;

        // RESTAR DEL SALDO DISPONIBLE
        user.setCurrentAvailableMoney(currentBalance.subtract(dto.getAmount()).doubleValue());
        userRepository.save(user);

        Expense expense = expenseMapper.expenseDTOToExpense(dto);
        expense.setUser(user);
        expense.setCreationDate(LocalDateTime.now());
        expense.setOverlimit(overLimit);

        ExpenseDTO savedExpense = expenseMapper.expenseToExpenseDTO(expenseRepository.save(expense));
        try {
            String detail = String.format("Monto: $%s en %s (%s)", dto.getAmount(), dto.getMethod(), dto.getDescription());
            recommendationService.generateSpecificRecommendation(userId, "EXPENSE", detail);
        } catch (Exception e) {
            System.err.println("No se pudo generar recomendación de gasto: " + e.getMessage());
        }
        return savedExpense;
    }

    @Override
    @Transactional
    public ExpenseDTO updateExpense(Integer idExpense, ExpenseDTO dto, Long userId) {
        Expense expense = expenseRepository.findById(idExpense)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con ID: " + idExpense));

        if (!expense.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado para modificar este gasto");

        User user = expense.getUser();

        BigDecimal newAmount = dto.getAmount();
        // AJUSTAR SALDO: Devolver monto antiguo (se cancela el gasto viejo), Restar monto nuevo si tiene menos de un dia de creacion
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        if(expense.getCreationDate().isAfter( now)) {
            // El gasto fue creado hace menos de un dia, se ajusta el saldo
            BigDecimal oldAmount = expense.getAmount();
            BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());

            user.setCurrentAvailableMoney(currentBalance.add(oldAmount).subtract(newAmount).doubleValue());
            userRepository.save(user);
        }
        // Actualizar datos
        expense.setAmount(newAmount);
        expense.setDate(dto.getDate());
        expense.setMethod(dto.getMethod());
        expense.setDescription(dto.getDescription());
        expense.setModificationDate(LocalDateTime.now());

        ExpenseDTO updatedExpense = expenseMapper.expenseToExpenseDTO(expenseRepository.save(expense));
        try { recommendationService.generateAndSaveRecommendation(userId); } catch (Exception e) {}
        return updatedExpense;
    }

    @Override
    @Transactional
    public void deleteExpense(Integer idExpense, Long userId) {
        Expense expense = expenseRepository.findById(idExpense)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con ID: " + idExpense));

        if (!expense.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado para eliminar este gasto");

        // SUMAR AL SALDO (Se elimina el gasto, el dinero regresa a la cuenta)
        User user = expense.getUser();

        LocalDateTime now = LocalDateTime.now().minusDays(1);
        if(expense.getCreationDate().isAfter(now)) {
            // El gasto fue creado hace menos de un dia, se ajusta el saldo
            BigDecimal refundAmount = expense.getAmount();
            BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());

            user.setCurrentAvailableMoney(currentBalance.add(refundAmount).doubleValue());
            userRepository.save(user);
        }

        expenseRepository.delete(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId)
                .stream().map(expenseMapper::expenseToExpenseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses(Long userId) {
        return expenseRepository.getTotalExpenses(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getExpensesBetweenDates(Long userId, java.time.LocalDate start, java.time.LocalDate end) {
        return expenseRepository.getExpensesBetweenDates(userId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAnomalousExpenses(Long userId) {
        return expenseRepository.getAnomalousExpenses(userId)
                .stream().map(expenseMapper::expenseToExpenseDTO).toList();
    }
}