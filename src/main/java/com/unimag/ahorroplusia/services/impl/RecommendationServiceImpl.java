package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.RecommendationDTO;
import com.unimag.ahorroplusia.entity.entities.Recommendation;
import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.RecommendationRepository;
import com.unimag.ahorroplusia.repository.SavingsGoalRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.RecommendationAIService;
import com.unimag.ahorroplusia.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationAIService aiService;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    @Override
    public RecommendationDTO generateAndSaveRecommendation(Long userId) throws Exception {
        return generateSpecificRecommendation(userId, "GENERAL", "Revisión periódica de finanzas");
    }

    @Override
    public RecommendationDTO generateSpecificRecommendation(Long userId, String type, String details) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 1. Recopilar contexto financiero global
        BigDecimal totalIncome = incomeRepository.getTotalIncome(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalExpenses(userId);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        List<SavingsGoal> goals = savingsGoalRepository.findByUserId(userId);
        String goalsSummary = goals.isEmpty() ? "Sin metas activas" :
                goals.stream().map(g -> g.getName() + " ($" + g.getCurrentAmount() + "/$" + g.getTargetAmount() + ")").collect(Collectors.joining(", "));

        // 2. Construir Prompt Específico según el evento
        String specificInstruction = "";
        String role = "Eres un asesor financiero personal proactivo.";

        switch (type) {
            case "INCOME":
                specificInstruction = String.format(
                        "El usuario acaba de recibir un INGRESO: '%s'.\n" +
                                "Tu tarea: Recomienda cómo distribuir inteligentemente este dinero específico. " +
                                "¿Debería ir a una meta de ahorro? ¿Cubrir un déficit? Dame porcentajes sugeridos.", details);
                break;
            case "EXPENSE":
                specificInstruction = String.format(
                        "El usuario acaba de registrar un GASTO: '%s'.\n" +
                                "Tu tarea: Analiza el impacto de este gasto. ¿Fue prudente dado su balance de $%s? " +
                                "Si el balance es bajo, dale una advertencia amable pero firme. Si está bien, dale un tip de optimización.", details, balance);
                break;
            case "GOAL":
                specificInstruction = String.format(
                        "El usuario acaba de crear o modificar una META DE AHORRO: '%s'.\n" +
                                "Tu tarea: Crea una micro-estrategia para alcanzar ESTA meta específica. " +
                                "Calcula cuánto debería ahorrar diariamente/semanalmente basándote en su balance actual para lograrlo rápido.", details);
                break;
            default:
                specificInstruction = "Realiza un análisis general de la salud financiera del usuario y sugiere una mejora inmediata.";
        }

        String fullPrompt = """
            %s
            
            CONTEXTO FINANCIERO ACTUAL:
            - Balance Disponible: $%s
            - Ingresos Totales Históricos: $%s
            - Gastos Totales Históricos: $%s
            - Metas Actuales: %s
            
            EVENTO DETONANTE:
            %s
            
            Formato de respuesta: Corto, directo, usa emojis, habla en segunda persona ("tú").
            """.formatted(role, balance, totalIncome, totalExpenses, goalsSummary, specificInstruction);

        // 3. Llamar a la IA
        String aiResponse = aiService.generateRecommendation(fullPrompt);

        // 4. Guardar recomendación
        Recommendation rec = new Recommendation();
        rec.setUser(user);
        rec.setMessage(aiResponse);
        rec.setStatus(EstadoRecomendacion.SUGERIDA);
        rec.setCreationDate(LocalDateTime.now());

        recommendationRepository.save(rec);

        return RecommendationDTO.builder()
                .id(rec.getId())
                .message(aiResponse)
                .estado(rec.getStatus())
                .fechaCreacion(rec.getCreationDate())
                .build();
    }
}