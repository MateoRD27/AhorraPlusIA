package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.ExpenseDTO;
import com.unimag.ahorroplusia.entity.entities.Expense;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    ExpenseDTO expenseToExpenseDTO(Expense expense);
    Expense expenseDTOToExpense(ExpenseDTO expenseDTO);
}
