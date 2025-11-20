package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.ChangeHistory;
import com.unimag.ahorroplusia.entity.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Integer> {

    List<ChangeHistory> findByUserId(Long userId);

    @Query("SELECT c FROM ChangeHistory c WHERE c.entityType = :entityType AND c.idEntity = :idEntity")
    List<ChangeHistory> findEntityHistory(EntityType entityType, Integer idEntity);
}
