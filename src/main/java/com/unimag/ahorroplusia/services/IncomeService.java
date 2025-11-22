package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.IncomeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface IncomeService {

    IncomeDTO createIncome(IncomeDTO incomeDTO, Long userId);

    IncomeDTO updateIncome(Integer idIncome, IncomeDTO incomeDTO, Long userId);

    void deleteIncome(Integer idIncome, Long userId);

    List<IncomeDTO> getAllIncomesByUser(Long userId);

    BigDecimal getTotalIncome(Long userId);

    BigDecimal getIncomeBetweenDates(Long userId, LocalDate start, LocalDate end);

}
