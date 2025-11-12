package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class IncomeDTO {

    // Monto del ingreso
    private BigDecimal amount;

    // Fecha del ingreso
    private LocalDate date;

    // Fuente del ingreso (beca, familia, trabajo)
    private String source;

    // Descripción del ingreso
    private String description;

    // Fecha de creación del registro
    private LocalDateTime creationDate;

    // Fecha de última modificación
    private LocalDateTime modificationDate;

    private UserDto userDto;
}
