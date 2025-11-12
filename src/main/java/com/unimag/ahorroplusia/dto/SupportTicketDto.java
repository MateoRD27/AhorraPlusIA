package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.Priority;
import com.unimag.ahorroplusia.entity.enums.RequestType;
import com.unimag.ahorroplusia.entity.enums.TicketStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class SupportTicketDto {
    // Tipo de solicitud
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    // Descripción del problema o solicitud
    private String description;

    // Estado del ticket
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.OPEN;

    // Prioridad
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    // Fecha de creación
    private LocalDateTime creationDate;

    // Fecha de resolución
    private LocalDateTime resolutionDate;

    // Respuesta del administrador
    private String adminResponse;

    // Identificador del administrador
    private Integer idAdmin;

    private UserDto userDto;
}
