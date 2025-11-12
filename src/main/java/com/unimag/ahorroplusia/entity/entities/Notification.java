package com.unimag.ahorroplusia.entity.entities;

import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import com.unimag.ahorroplusia.entity.enums.NotificationType;
import com.unimag.ahorroplusia.entity.enums.Priority;
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
@Table(name = "notofications")
// notificaciones de la aplicacion
public class Notification {


    // Identificador de la notificación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotification;

    // Tipo de notificación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Título
    @Column(nullable = false, length = 200)
    private String title;

    // Mensaje
    @Column(nullable = false)
    private String message;

    // Fecha de creación
    @Column(nullable = false)
    private LocalDateTime creationDate;

    // Fecha de lectura
    private LocalDateTime readDate;

    // Indica si fue leída
    @Column(nullable = false)
    private Boolean read = false;

    // Prioridad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
