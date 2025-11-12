package com.unimag.ahorroplusia.dto;

import com.unimag.ahorroplusia.entity.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class LoginDTO {
    //fecha de iniciqo de sesion
    private LocalDateTime loginDate;

    //fecha fin inicio de sesion
    private LocalDateTime logoutDate;

    //ip dispositivo
    private String ipAddress;

    //intento exitoso
    private Boolean successful;

    //motivo de fallo
    private String failureReason;

    //relacion con usuario
    private UserDto userDto;

}
