package com.unimag.ahorroplusia.entity.entities;


import com.unimag.ahorroplusia.entity.enums.Priority;
import com.unimag.ahorroplusia.entity.enums.RequestType;
import com.unimag.ahorroplusia.entity.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "support_tickets")
// tickets de soporte creados por los usuarios
public class SupportTicket {

    // Identificador del ticket
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTicket;

    // Tipo de solicitud
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    // Descripción del problema o solicitud
    @Column(nullable = false)
    private String description;

    // Estado del ticket
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    // Prioridad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    // Fecha de creación
    @Column(nullable = false)
    private LocalDateTime creationDate;

    // Fecha de resolución
    private LocalDateTime resolutionDate;

    // Respuesta del administrador
    private String adminResponse;

    // Identificador del administrador
    private Integer idAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
