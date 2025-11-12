package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.IncomeDTO;
import com.unimag.ahorroplusia.entity.entities.Income;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    IncomeDTO incomeToIncomeDTO(Income income);
    Income incomeDTOToIncome(IncomeDTO incomeDTO);
}
