package com.unimag.ahorroplusia.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Double fixedSalary;
    private Double currentAvailableMoney;
}