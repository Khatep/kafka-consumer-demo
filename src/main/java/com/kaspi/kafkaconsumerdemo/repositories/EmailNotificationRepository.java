package com.kaspi.kafkaconsumerdemo.repositories;

import com.kaspi.kafkaconsumerdemo.domain.entities.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification, UUID> {
}
