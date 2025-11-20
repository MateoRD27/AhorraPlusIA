package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findByUserId(Long userId);
}
