// RecommendationAIController.java
package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.dto.RecommendationDTO;
import com.unimag.ahorroplusia.entity.entities.Recommendation;
import com.unimag.ahorroplusia.repository.RecommendationRepository;
import com.unimag.ahorroplusia.services.RecommendationAIService;
import com.unimag.ahorroplusia.services.RecommendationService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationAIController {

    private final RecommendationAIService recommendationAIService;
    private final RecommendationService recommendationService;
    private final RecommendationRepository recommendationRepository;

    @PostMapping("/recommend")
    public String getRecommendation(@RequestBody Map<String, String> request) throws Exception {
        String prompt = request.get("prompt");
        return recommendationAIService.generateRecommendation(prompt);
    }

    @PostMapping("/generate/{userId}")
    public RecommendationDTO generateRecommendation(@PathVariable Long userId) throws Exception {
        return recommendationService.generateAndSaveRecommendation(userId);
    }

    // Obtener las últimas recomendaciones del usuario
    @GetMapping("/user/{userId}")
    public List<RecommendationDTO> getUserRecommendations(@PathVariable Long userId) {
        List<Recommendation> recommendations = recommendationRepository.findByUserIdOrderByCreationDateDesc(userId);

        return recommendations.stream()
                .map(rec -> RecommendationDTO.builder()
                        .id(rec.getId())
                        .message(rec.getMessage())
                        .estado(rec.getStatus())
                        .fechaCreacion(rec.getCreationDate())
                        .fueUtil(rec.getWasUseful())
                        .build())
                .collect(Collectors.toList());
    }

    // Actualizar estado de recomendación (aceptada/rechazada)
    @PatchMapping("/{recommendationId}/status")
    public RecommendationDTO updateStatus(
            @PathVariable Long recommendationId,
            @RequestBody Map<String, String> body) {

        Recommendation rec = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recomendación no encontrada"));

        String status = body.get("status");
        if (status != null) {
            rec.setStatus(com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion.valueOf(status));
        }

        recommendationRepository.save(rec);

        return RecommendationDTO.builder()
                .id(rec.getId())
                .message(rec.getMessage())
                .estado(rec.getStatus())
                .fechaCreacion(rec.getCreationDate())
                .fueUtil(rec.getWasUseful())
                .build();
    }
}