package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.enums.AccountStatus;
import com.unimag.ahorroplusia.entity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.accountStatus = :status")
    List<User> findByAccountStatus(AccountStatus status);

    @Query("SELECT u FROM User u WHERE u.verifiedAccount = false")
    List<User> findUnverifiedUsers();

    @Query("SELECT u FROM User u WHERE u.lastAccessDate < :date")
    List<User> findInactiveUsers(LocalDateTime date);
}