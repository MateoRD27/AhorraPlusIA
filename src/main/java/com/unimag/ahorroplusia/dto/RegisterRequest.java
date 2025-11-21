package com.unimag.ahorroplusia.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Double fixedSalary;
    private Double currentAvailableMoney;
}