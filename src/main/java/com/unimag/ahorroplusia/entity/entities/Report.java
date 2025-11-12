package com.unimag.ahorroplusia.entity.entities;
import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import com.unimag.ahorroplusia.entity.enums.ReportFormat;
import com.unimag.ahorroplusia.entity.enums.ReportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.spi.AccessType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reports")
public class Report {
    // Identificador del reporte
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReport;

    // Tipo de reporte
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    // Periodo de inicio
    @Column(nullable = false)
    private LocalDate startPeriod;

    // Periodo de fin
    @Column(nullable = false)
    private LocalDate endPeriod;

    // Formato del reporte
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportFormat format;

    // Fecha de generación
    @Column(nullable = false)
    private LocalDateTime generationDate;

    // Filtros aplicados
    private String appliedFilters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
