package com.unimag.ahorroplusia.repository;

import com.unimag.ahorroplusia.entity.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByRead(boolean read);
}
