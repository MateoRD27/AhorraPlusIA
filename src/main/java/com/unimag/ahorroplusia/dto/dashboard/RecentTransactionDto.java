package com.unimag.ahorroplusia.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentTransactionDto {
    private Integer id;
    private String type;
    private String description;
    private Double amount;
    private String date;
}
