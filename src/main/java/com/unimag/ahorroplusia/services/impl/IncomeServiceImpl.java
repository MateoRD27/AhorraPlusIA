package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.IncomeDTO;
import com.unimag.ahorroplusia.entity.entities.Income;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.IncomeMapper;
import com.unimag.ahorroplusia.repository.IncomeRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.IncomeService;
import com.unimag.ahorroplusia.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final IncomeMapper incomeMapper;
    private final RecommendationService recommendationService;

    @Override
    @Transactional
    public IncomeDTO createIncome(IncomeDTO incomeDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Income income = incomeMapper.incomeDTOToIncome(incomeDTO);
        income.setUser(user);
        income.setCreationDate(LocalDateTime.now());

        // SUMAR AL SALDO DISPONIBLE
        BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());
        user.setCurrentAvailableMoney(currentBalance.add(incomeDTO.getAmount()).doubleValue());
        userRepository.save(user);

        IncomeDTO savedIncome = incomeMapper.incomeToIncomeDTO(incomeRepository.save(income));

        try {
            String detail = String.format("Monto: $%s, Fuente: %s", incomeDTO.getAmount(), incomeDTO.getSource());
            recommendationService.generateSpecificRecommendation(userId, "INCOME", detail);
        } catch (Exception e) {
            System.err.println("No se pudo generar recomendación de ingreso: " + e.getMessage());
        }

        return savedIncome;
    }

    @Override
    @Transactional
    public IncomeDTO updateIncome(Integer idIncome, IncomeDTO incomeDTO, Long userId) {
        Income income = incomeRepository.findById(idIncome)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado con ID: " + idIncome));

        if(!income.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado para modificar este ingreso");

        User user = income.getUser();

        // AJUSTAR SALDO: Restar valor antiguo, sumar valor nuevo
        BigDecimal oldAmount = income.getAmount();
        BigDecimal newAmount = incomeDTO.getAmount();
        BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());

        user.setCurrentAvailableMoney(currentBalance.subtract(oldAmount).add(newAmount).doubleValue());
        userRepository.save(user);

        // Actualizar datos
        income.setAmount(newAmount);
        income.setDate(incomeDTO.getDate());
        income.setSource(incomeDTO.getSource());
        income.setDescription(incomeDTO.getDescription());
        income.setModificationDate(LocalDateTime.now());

        return incomeMapper.incomeToIncomeDTO(incomeRepository.save(income));
    }

    @Override
    @Transactional
    public void deleteIncome(Integer idIncome, Long userId) {
        Income income = incomeRepository.findById(idIncome)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado con ID: " + idIncome));

        if(!income.getUser().getId().equals(userId))
            throw new RuntimeException("No autorizado para eliminar este ingreso");

        // RESTAR DEL SALDO (Se elimina el ingreso, el dinero desaparece de la cuenta)
        User user = income.getUser();
        BigDecimal amountToRemove = income.getAmount();
        BigDecimal currentBalance = BigDecimal.valueOf(user.getCurrentAvailableMoney() == null ? 0.0 : user.getCurrentAvailableMoney());

        user.setCurrentAvailableMoney(currentBalance.subtract(amountToRemove).doubleValue());
        userRepository.save(user);

        incomeRepository.delete(income);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeDTO> getAllIncomesByUser(Long userId) {
        return incomeRepository.findByUserId(userId)
                .stream().map(incomeMapper::incomeToIncomeDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(Long userId) {
        return incomeRepository.getTotalIncome(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getIncomeBetweenDates(Long userId, java.time.LocalDate start, java.time.LocalDate end) {
        return incomeRepository.getIncomeBetweenDates(userId, start, end);
    }
}