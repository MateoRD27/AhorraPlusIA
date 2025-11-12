package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.ReportFormat;
import com.unimag.ahorroplusia.entity.enums.ReportType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class ReportDTO {
    // Tipo de reporte
    @Enumerated(EnumType.STRING)
    private ReportType reportType; // Identificador del reporte

    // Periodo de inicio
    private LocalDate startPeriod;

    // Periodo de fin
    private LocalDate endPeriod;

    // Formato del reporte
    @Enumerated(EnumType.STRING)
    private ReportFormat format;

    // Fecha de generación
    private LocalDateTime generationDate;

    // Filtros aplicados
    private String appliedFilters;

    private UserDto userDto;
}
