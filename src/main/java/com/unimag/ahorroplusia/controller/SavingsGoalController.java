package com.unimag.ahorroplusia.controller;
import com.unimag.ahorroplusia.dto.SavingGoalDTO;
import com.unimag.ahorroplusia.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;

    // Crear Meta
    // URL: POST /api/v1/savings-goals/{userId}
    // Body: JSON con datos de la meta
    @PostMapping("/{userId}")
    public ResponseEntity<SavingGoalDTO> createGoal(@RequestBody SavingGoalDTO dto,
                                                    @PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.createSavingsGoal(dto, userId));
    }

    // Actualizar Meta
    // URL: PUT /api/v1/savings-goals/{idGoal}/{userId}
    @PutMapping("/{idGoal}/{userId}")
    public ResponseEntity<SavingGoalDTO> updateGoal(@PathVariable Integer idGoal,
                                                    @PathVariable Long userId,
                                                    @RequestBody SavingGoalDTO dto) {
        return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(idGoal, dto, userId));
    }

    // Eliminar Meta
    // URL: DELETE /api/v1/savings-goals/{idGoal}/{userId}
    @DeleteMapping("/{idGoal}/{userId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Integer idGoal,
                                           @PathVariable Long userId) {
        savingsGoalService.deleteSavingsGoal(idGoal, userId);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (éxito sin cuerpo)
    }

    // Obtener todas las metas del usuario
    // URL: GET /api/v1/savings-goals/all/{userId}
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<SavingGoalDTO>> getAllByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getAllGoalsByUser(userId));
    }

    // Obtener una meta específica
    // URL: GET /api/v1/savings-goals/{idGoal}/{userId}
    @GetMapping("/{idGoal}/{userId}")
    public ResponseEntity<SavingGoalDTO> getGoalById(@PathVariable Integer idGoal,
                                                     @PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getGoalById(idGoal, userId));
    }

    // Abonar dinero a la meta
    // URL: POST /api/v1/savings-goals/{idGoal}/{userId}/add
    // Body: { "amount": 50000 }
    @PostMapping("/{idGoal}/{userId}/add")
    public ResponseEntity<SavingGoalDTO> addFunds(@PathVariable Integer idGoal,
                                                  @PathVariable Long userId,
                                                  @RequestBody Map<String, BigDecimal> payload) {
        // Extraemos el monto del mapa JSON recibido
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(savingsGoalService.addFunds(idGoal, amount, userId));
    }

    // Retirar dinero de la meta
    // URL: POST /api/v1/savings-goals/{idGoal}/{userId}/withdraw
    // Body: { "amount": 20000 }
    @PostMapping("/{idGoal}/{userId}/withdraw")
    public ResponseEntity<SavingGoalDTO> withdrawFunds(@PathVariable Integer idGoal,
                                                       @PathVariable Long userId,
                                                       @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(savingsGoalService.withdrawFunds(idGoal, amount, userId));
    }
}
