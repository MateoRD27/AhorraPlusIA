package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

    // Monto del gasto
    private BigDecimal amount;

    // Fecha del gasto
    private LocalDate date;

    // Método de pago (efectivo, transferencia)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    // Descripción del gasto
    private String description;

    // Indica si es un gasto anómalo
    private Boolean anomalous = false;

    // Indica si hay sobrecupo
    private Boolean overlimit = false;

    // Fecha de creación del registro
    private LocalDateTime creationDate;

    // Fecha de modificación
    private LocalDateTime modificationDate;


    private UserDto userDto;
}
