package com.unimag.ahorroplusia.entity.entities;
import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import com.unimag.ahorroplusia.entity.enums.ActionType;
import com.unimag.ahorroplusia.entity.enums.EntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.spi.AccessType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "change_history")
public class ChangeHistory {
    // Identificador del historial
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistory;

    // Tipo de entidad (ingreso, gasto, meta)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    // Identificador de la entidad
    @Column(nullable = false)
    private Integer idEntity;

    // Acción realizada
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;

    // Datos anteriores
    private String oldData;

    // Datos nuevos
    private String newData;

    // Fecha del cambio
    @Column(nullable = false)
    private LocalDateTime changeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
