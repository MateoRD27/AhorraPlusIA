package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Recommendation;
import com.unimag.ahorroplusia.entity.enums.EstadoRecomendacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByStatus(EstadoRecomendacion status);
}
