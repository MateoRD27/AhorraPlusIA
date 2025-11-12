package com.unimag.ahorroplusia.entity.entities;


import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import com.unimag.ahorroplusia.entity.enums.NotificationFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.spi.AccessType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "settings")
// configuraciones personalizadas de los usuarios
public class UserSetting {

    // Identificador de la configuración
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSetting;

    // Frecuencia de notificaciones
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationFrequency notificationFrequency = NotificationFrequency.MONTHLY;

    // Canales de notificación
    @Column(length = 100)
    private String notificationChannels;

    // Alertas de presupuesto activas
    @Column(nullable = false)
    private Boolean budgetAlertsActive = true;

    // Umbral de alerta de gasto
    @Column(precision = 15, scale = 2)
    private BigDecimal spendingAlertThreshold;

    // Idioma
    @Column(nullable = false, length = 10)
    private String language = "es";

    // Moneda
    @Column(nullable = false, length = 10)
    private String currency = "COP";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
