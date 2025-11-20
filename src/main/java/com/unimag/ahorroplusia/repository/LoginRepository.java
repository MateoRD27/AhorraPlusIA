package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {

    List<Login> findByUserId(Long userId);
}
