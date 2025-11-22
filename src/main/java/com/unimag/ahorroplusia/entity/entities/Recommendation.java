package com.unimag.ahorroplusia.entity.entities;

import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    private String message;

    @Enumerated(EnumType.STRING)
    private EstadoRecomendacion status = EstadoRecomendacion.SUGERIDA; // sugerida, aceptada, rechazada, completada

    private LocalDateTime creationDate;;

    private LocalDateTime fechaEvaluacion;

    private Boolean wasUseful; // indicador de efectividad
}
