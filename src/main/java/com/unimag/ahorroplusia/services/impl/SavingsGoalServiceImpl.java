package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.dto.SavingGoalDTO;
import com.unimag.ahorroplusia.entity.entities.SavingsGoal;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.Frequency;
import com.unimag.ahorroplusia.entity.enums.GoalStatus;
import com.unimag.ahorroplusia.exception.ResourceNotFoundException;
import com.unimag.ahorroplusia.mapper.SavingGoalMapper;
import com.unimag.ahorroplusia.repository.SavingsGoalRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {
    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    @Transactional // Asegura que toda la operación se guarde o se revierta si hay error
    public SavingGoalDTO createSavingsGoal(SavingGoalDTO dto, Long userId) {
        //  Buscamos al usuario dueño de la meta
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        //  Convertimos el DTO (datos que vienen del front) a una Entidad (datos para la BD)
        SavingsGoal goal = savingGoalMapper.savingGoalDTOToSavingGoal(dto);

        //  Asignamos valores iniciales automáticos
        goal.setUser(user);
        goal.setCreationDate(LocalDateTime.now());
        goal.setModificationDate(LocalDateTime.now());
        goal.setCurrentAmount(BigDecimal.ZERO); // Una meta nueva empieza con $0 ahorrados
        goal.setStatus(GoalStatus.ACTIVE);      // Estado inicial: Activa

        //  Validación de seguridad para evitar errores de BD:
        // Si el frontend no envió una frecuencia, asignamos MENSUAL por defecto
        if (goal.getFrequency() == null) {
            goal.setFrequency(Frequency.MONTHLY);
        }

        //  Guardamos en base de datos y devolvemos el DTO resultante
        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public SavingGoalDTO updateSavingsGoal(Integer idGoal, SavingGoalDTO dto, Long userId) {
        //  Buscamos la meta y verificamos que pertenezca al usuario (ver método getGoalEntity abajo)
        SavingsGoal goal = getGoalEntity(idGoal, userId);

        //  Actualizamos solo los campos permitidos (No dejamos cambiar el ID ni el usuario)
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setEndDate(dto.getEndDate());
        goal.setPriority(dto.getPriority());
        goal.setModificationDate(LocalDateTime.now());

        //  Verificamos si con los nuevos cambios (ej: bajaron el monto objetivo) la meta ya se cumplió
        checkCompletion(goal);

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteSavingsGoal(Integer idGoal, Long userId) {
        //  Buscamos la meta y verificamos permisos antes de borrar
        SavingsGoal goal = getGoalEntity(idGoal, userId);
        savingsGoalRepository.delete(goal);
    }

    @Override
    @Transactional(readOnly = true) // Optimización: Solo lectura, no bloquea la BD
    public List<SavingGoalDTO> getAllGoalsByUser(Long userId) {
        // Busca todas las metas del usuario y las convierte a una lista de DTOs
        return savingsGoalRepository.findByUserId(userId).stream()
                .map(savingGoalMapper::savingGoalToSavingGoalDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SavingGoalDTO getGoalById(Integer idGoal, Long userId) {
        // Busca una meta específica validando que sea del usuario
        return savingGoalMapper.savingGoalToSavingGoalDTO(getGoalEntity(idGoal, userId));
    }

    @Override
    @Transactional
    public SavingGoalDTO addFunds(Integer idGoal, BigDecimal amount, Long userId) {
        //  Validación básica: no se puede sumar 0 o negativo
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        //  Obtener la meta
        SavingsGoal goal = getGoalEntity(idGoal, userId);

        //  Sumar el dinero al monto actual
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        //  Verificar si ya llegamos a la meta para felicitar al usuario (cambiar estado)
        checkCompletion(goal);

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public SavingGoalDTO withdrawFunds(Integer idGoal, BigDecimal amount, Long userId) {
        //  Validación básica
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a 0");
        }

        SavingsGoal goal = getGoalEntity(idGoal, userId);

        //  Validación de fondos: No puedes retirar más de lo que tienes ahorrado
        if (goal.getCurrentAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la meta");
        }

        //  Restar el dinero
        goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount));

        //  Lógica de Estado: Si la meta estaba 'COMPLETADA' pero retiramos dinero
        // y bajamos del objetivo, debe volver a estar 'ACTIVA'
        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        return savingGoalMapper.savingGoalToSavingGoalDTO(savingsGoalRepository.save(goal));
    }

    // --- MÉTODOS AUXILIARES PRIVADOS (Helper Methods) ---

    // Método centralizado para buscar una meta y asegurar que pertenece al usuario que hace la petición.
    // Evita que el Usuario A edite las metas del Usuario B.
    private SavingsGoal getGoalEntity(Integer idGoal, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(idGoal)
                .orElseThrow(() -> new ResourceNotFoundException("Meta de ahorro no encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado para acceder a esta meta");
        }
        return goal;
    }

    // Lógica para determinar si la meta se ha logrado
    private void checkCompletion(SavingsGoal goal) {
        // Si lo ahorrado es mayor o igual al objetivo
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
    }
}
