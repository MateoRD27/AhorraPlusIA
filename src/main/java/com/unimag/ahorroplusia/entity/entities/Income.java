package com.unimag.ahorroplusia.entity.entities;


import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.spi.AccessType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// ingresos
public class Income {
    // Identificador del ingreso
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIncome;

    // Monto del ingreso
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // Fecha del ingreso
    @Column(nullable = false)
    private LocalDate date;

    // Fuente del ingreso (beca, familia, trabajo)
    @Column(length = 100)
    private String source;

    // Descripción del ingreso
    private String description;

    // Fecha de creación del registro
    @Column(nullable = false)
    private LocalDateTime creationDate;

    // Fecha de última modificación
    private LocalDateTime modificationDate;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
