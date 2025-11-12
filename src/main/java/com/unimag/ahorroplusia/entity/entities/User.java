package com.unimag.ahorroplusia.entity.entities;


import com.unimag.ahorroplusia.entity.enums.AccountStatus;
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
@Table(name = "users")
// usuarios de la aplicacion
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(nullable = false,  unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @Column(name ="fixed_salary")
    private Double fixedSalary;

    @Column(name = "current_available_money")
    private Double currentAvailableMoney;

    @Column(name = "registration_date", nullable = false)
    LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    AccountStatus accountStatus;

    @Column(name = "verified_account", nullable = false)
    Boolean verifiedAccount;

    @Column(name = "last_access_date")
    LocalDateTime lastAccessDate;

    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @Column(name = "modification_date")
    LocalDateTime modificationDate;
// lista de inicios de sesion del usuario
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY )
    private List<Login> logins = new ArrayList<>();
// lista de metas de ahorro del usuario
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY )
    private List<SavingsGoal> savingsGoals = new ArrayList<>();
    // lista de ingresos del usuario
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> incomes = new ArrayList<>();
     // lista de gastos del usuario
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserSetting userSetting;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SupportTicket> supportTickets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChangeHistory> changeHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();
}
