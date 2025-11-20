package com.unimag.ahorroplusia.entity.entities;

import com.unimag.ahorroplusia.entity.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="expenses")
// gastos
public class Expense {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer idExpense;


    // Monto del gasto
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // Fecha del gasto
    @Column(nullable = false)
    private LocalDate date;

    // Método de pago (efectivo, transferencia)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    // Descripción del gasto
    private String description;

    // Indica si es un gasto anómalo
    @Column(nullable = false)
    private Boolean anomalous = false;

    // Indica si hay sobrecupo
    @Column(nullable = false)
    private Boolean overlimit = false;

    // Fecha de creación del registro
    @Column(nullable = false)
    private LocalDateTime creationDate;

    // Fecha de modificación
    private LocalDateTime modificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
