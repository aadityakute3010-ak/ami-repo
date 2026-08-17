package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.Notification;
import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    List<Notification> findByStatus(
            NotificationStatus status);

    List<Notification> findByType(
            NotificationType type);

    List<Notification> findByRecipient(
            String recipient);

    long countByStatus(
            NotificationStatus status);

    long countByType(
            NotificationType type);
}