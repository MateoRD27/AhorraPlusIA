package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long id;
    private String message;
    private EstadoRecomendacion estado;
    private LocalDateTime fechaCreacion;
    private Boolean fueUtil;
}