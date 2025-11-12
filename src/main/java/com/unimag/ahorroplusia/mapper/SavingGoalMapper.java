package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.SavingGoalDTO;
import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SavingGoalMapper {
    SavingGoalDTO savingGoalToSavingGoalDTO(SavingsGoal savingGoal);
    SavingsGoal savingGoalDTOToSavingGoal(SavingGoalDTO savingGoalDTO);

}
