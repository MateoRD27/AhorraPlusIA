package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecommendationDTO {
    private Long id;
    private String message;
    private EstadoRecomendacion estado;
    private LocalDateTime fechaCreacion;
    private Boolean fueUtil;
}