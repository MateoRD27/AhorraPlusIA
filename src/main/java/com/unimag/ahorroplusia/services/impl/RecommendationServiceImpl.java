package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.RecommendationDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.entity.entities.Recommendation;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.repository.ExpenseRepository;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.RecommendationRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.RecommendationAIService;
import com.unimag.ahorroplusia.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationAIService aiService;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Override
    public RecommendationDTO generateAndSaveRecommendation(Long userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BigDecimal totalIncome = incomeRepository.getTotalIncome(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalExpenses(userId);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        String prompt = """
        Eres un asesor financiero especializado en estudiantes universitarios.
        Analiza los datos y genera 3 recomendaciones accionables, numéricas y realistas.

        Total ingresos: %s
        Total gastos: %s
        Balance actual: %s

        Respuesta en formato:
        1. ...
        2. ...
        3. ...
        """.formatted(totalIncome, totalExpenses, balance);

        String result = aiService.generateRecommendation(prompt);

        Recommendation rec = new Recommendation();
        rec.setUser(user);
        rec.setMessage(result);
        rec.setStatus(EstadoRecomendacion.SUGERIDA);
        rec.setCreationDate(LocalDateTime.now());

        recommendationRepository.save(rec);

        return RecommendationDTO.builder()
                .id(rec.getId())
                .message(result)
                .estado(rec.getStatus())
                .fechaCreacion(rec.getCreationDate())
                .fueUtil(null)
                .build();
    }
}
