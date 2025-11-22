package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.dto.ExpenseDTO;
import com.unimag.ahorroplusia.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/{userId}")
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO dto,
                                                    @PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.createExpense(dto, userId));
    }

    @PutMapping("/{idExpense}/{userId}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Integer idExpense,
                                                    @PathVariable Long userId,
                                                    @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(idExpense, dto, userId));
    }

    @DeleteMapping("/{idExpense}/{userId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer idExpense,
                                              @PathVariable Long userId) {
        expenseService.deleteExpense(idExpense, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getAllByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getAllExpensesByUser(userId));
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<BigDecimal> getTotalExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getTotalExpenses(userId));
    }

    @GetMapping("/range/{userId}")
    public ResponseEntity<BigDecimal> getExpensesBetweenDates(
            @PathVariable Long userId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(expenseService.getExpensesBetweenDates(userId, start, end));
    }

    @GetMapping("/anomalous/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getAnomalousExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getAnomalousExpenses(userId));
    }
}
