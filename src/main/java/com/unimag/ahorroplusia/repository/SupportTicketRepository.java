package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {

    List<SupportTicket> findByUserId(Long userId);
}
