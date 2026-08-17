package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.AlertEvent;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;

@Repository
public interface AlertEventRepository
        extends JpaRepository<AlertEvent, Long> {

    // =========================================================
    // DEVICE EVENTS
    // =========================================================

    List<AlertEvent> findByDeviceIdOrderByTriggeredAtDesc(
            Long deviceId);

    // =========================================================
    // ALERT EVENTS
    // =========================================================

    List<AlertEvent> findByAlertIdOrderByTriggeredAtDesc(
            Long alertId);

    // =========================================================
    // STATUS
    // =========================================================

    List<AlertEvent> findByStatusOrderByTriggeredAtDesc(
            AlertStatus status);

    // =========================================================
    // SEVERITY
    // =========================================================

    List<AlertEvent> findBySeverityOrderByTriggeredAtDesc(
            AlertSeverity severity);

    // =========================================================
    // ACTIVE EVENTS
    // =========================================================

    List<AlertEvent> findByStatusOrderByTriggeredAtDesc(
            AlertStatus status,
            org.springframework.data.domain.Pageable pageable);

    // =========================================================
    // DATE RANGE
    // =========================================================

    List<AlertEvent> findByTriggeredAtBetweenOrderByTriggeredAtDesc(
            LocalDateTime start,
            LocalDateTime end);

    // =========================================================
    // RECENT EVENTS
    // =========================================================

    List<AlertEvent> findTop10ByOrderByTriggeredAtDesc();

    // =========================================================
    // COUNTS
    // =========================================================

    long countByStatus(AlertStatus status);
    
    long countBySeverity(AlertSeverity severity);

    long countByTriggeredAtBetween(
            LocalDateTime start,
            LocalDateTime end);

    long countByStatusAndTriggeredAtBetween(
            AlertStatus status,
            LocalDateTime start,
            LocalDateTime end);
}