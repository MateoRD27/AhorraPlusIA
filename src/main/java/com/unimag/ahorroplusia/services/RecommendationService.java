package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.RecommendationDTO;

public interface RecommendationService {

    // Método para recomendación general (ej: desde el botón del dashboard)
    RecommendationDTO generateAndSaveRecommendation(Long userId) throws Exception;

    // Método para recomendaciones específicas disparadas por eventos
    // type: "INCOME", "EXPENSE", "GOAL"
    // details: Descripción del evento (ej: "Gasto de 50.000 en Comida")
    RecommendationDTO generateSpecificRecommendation(Long userId, String type, String details) throws Exception;
}