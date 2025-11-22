package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.ExpenseDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.ExpenseMapper;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseDTO createExpense(ExpenseDTO dto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal totalIncome = incomeRepository.getTotalIncome(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalExpenses(userId);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        boolean overLimit = dto.getAmount().compareTo(balance) > 0;

        Expense expense = expenseMapper.expenseDTOToExpense(dto);
        expense.setUser(user);
        expense.setCreationDate(LocalDateTime.now());
        expense.setOverlimit(overLimit);

        return expenseMapper.expenseToExpenseDTO(expenseRepository.save(expense));
    }

    @Override
    public ExpenseDTO updateExpense(Integer idExpense, ExpenseDTO dto, Long userId) {
        Expense expense = expenseRepository.findById(idExpense)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));

        if (!expense.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado");

        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setMethod(dto.getMethod());
        expense.setDescription(dto.getDescription());
        expense.setModificationDate(LocalDateTime.now());

        return expenseMapper.expenseToExpenseDTO(expenseRepository.save(expense));
    }

    @Override
    public void deleteExpense(Integer idExpense, Long userId) {
        Expense expense = expenseRepository.findById(idExpense)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));

        if (!expense.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado");

        expenseRepository.delete(expense);
    }

    @Override
    public List<ExpenseDTO> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId)
                .stream().map(expenseMapper::expenseToExpenseDTO).toList();
    }

    @Override
    public BigDecimal getTotalExpenses(Long userId) {
        return expenseRepository.getTotalExpenses(userId);
    }

    @Override
    public BigDecimal getExpensesBetweenDates(Long userId, java.time.LocalDate start, java.time.LocalDate end) {
        return expenseRepository.getExpensesBetweenDates(userId, start, end);
    }

    @Override
    public List<ExpenseDTO> getAnomalousExpenses(Long userId) {
        return expenseRepository.getAnomalousExpenses(userId)
                .stream().map(expenseMapper::expenseToExpenseDTO).toList();
    }
}
