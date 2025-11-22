package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.NotificationType;
import com.unimag.ahorroplusia.entity.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    // Tipo de notificación
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // Título
    private String title;

    // Mensaje
    private String message;

    // Fecha de creación
    private LocalDateTime creationDate;

    // Fecha de lectura
    private LocalDateTime readDate;

    // Indica si fue leída
    private Boolean read = false;

    // Prioridad
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;


    private UserDto userDto;
}
