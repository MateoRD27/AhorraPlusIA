package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.IncomeDTO;
import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.IncomeMapper;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final IncomeMapper incomeMapper;

    @Override
    public IncomeDTO createIncome(IncomeDTO incomeDTO, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Income income = incomeMapper.incomeDTOToIncome(incomeDTO);
        income.setUser(user);
        income.setCreationDate(LocalDateTime.now());

        return incomeMapper.incomeToIncomeDTO(incomeRepository.save(income));
    }

    @Override
    public IncomeDTO updateIncome(Integer idIncome, IncomeDTO incomeDTO, Long userId) {

        Income income = incomeRepository.findById(idIncome)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado"));

        if(!income.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado");

        income.setAmount(incomeDTO.getAmount());
        income.setDate(incomeDTO.getDate());
        income.setSource(incomeDTO.getSource());
        income.setDescription(incomeDTO.getDescription());
        income.setModificationDate(LocalDateTime.now());

        return incomeMapper.incomeToIncomeDTO(incomeRepository.save(income));
    }

    @Override
    public void deleteIncome(Integer idIncome, Long userId) {

        Income income = incomeRepository.findById(idIncome)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado"));

        if(!income.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado");

        incomeRepository.delete(income);
    }

    @Override
    public List<IncomeDTO> getAllIncomesByUser(Long userId) {
        return incomeRepository.findByUserId(userId)
                .stream().map(incomeMapper::incomeToIncomeDTO).toList();
    }

    @Override
    public BigDecimal getTotalIncome(Long userId) {
        return incomeRepository.getTotalIncome(userId);
    }

    @Override
    public BigDecimal getIncomeBetweenDates(Long userId, java.time.LocalDate start, java.time.LocalDate end) {
        return incomeRepository.getIncomeBetweenDates(userId, start, end);
    }
}
