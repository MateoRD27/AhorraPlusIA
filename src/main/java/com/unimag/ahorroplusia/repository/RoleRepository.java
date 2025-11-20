package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Role;
import com.unimag.ahorroplusia.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole name);

    boolean existsByName(ERole name);
}
