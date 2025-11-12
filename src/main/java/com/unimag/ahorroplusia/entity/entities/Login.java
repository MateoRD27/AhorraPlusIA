package com.unimag.ahorroplusia.entity.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "logins")
//registro de inicios de sesion
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //fecha de iniciqo de sesion
    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;

    //fecha fin inicio de sesion
    @Column(name = "logout_date")
    private LocalDateTime logoutDate;

    //ip dispositivo
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    //dispositivo info
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    //intento exitoso
    @Column(name = "successful", nullable = false)
    private Boolean successful;

    //motivo de fallo
    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    //relacion con usuario
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
