package com.unimag.ahorroplusia.entity.entities;

import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacion")
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
