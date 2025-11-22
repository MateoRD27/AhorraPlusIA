package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.dto.IncomeDTO;
import com.unimag.ahorroplusia.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/{userId}")
    public ResponseEntity<IncomeDTO> createIncome(@RequestBody IncomeDTO dto,
                                                  @PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.createIncome(dto, userId));
    }

    @PutMapping("/{idIncome}/{userId}")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable Integer idIncome,
                                                  @PathVariable Long userId,
                                                  @RequestBody IncomeDTO dto) {
        return ResponseEntity.ok(incomeService.updateIncome(idIncome, dto, userId));
    }

    @DeleteMapping("/{idIncome}/{userId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Integer idIncome,
                                             @PathVariable Long userId) {
        incomeService.deleteIncome(idIncome, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<IncomeDTO>> getAllByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.getAllIncomesByUser(userId));
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<BigDecimal> getTotalIncome(@PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.getTotalIncome(userId));
    }

    @GetMapping("/range/{userId}")
    public ResponseEntity<BigDecimal> getIncomeBetweenDates(
            @PathVariable Long userId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(incomeService.getIncomeBetweenDates(userId, start, end));
    }
}
