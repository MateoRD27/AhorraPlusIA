package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.NotificationFrequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class UserSettingDto {
    // Frecuencia de notificaciones
    @Enumerated(EnumType.STRING)
    private NotificationFrequency notificationFrequency = NotificationFrequency.MONTHLY;

    // Canales de notificación
    private String notificationChannels;

    // Alertas de presupuesto activas
    private Boolean budgetAlertsActive = true;

    // Umbral de alerta de gasto
    private BigDecimal spendingAlertThreshold;

    // Moneda
    private String currency = "COP";

    private UserDto userDto;

}
