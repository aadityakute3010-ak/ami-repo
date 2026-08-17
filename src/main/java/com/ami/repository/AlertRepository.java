package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.Alert;
import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

@Repository
public interface AlertRepository
extends JpaRepository<Alert, Long>,
        JpaSpecificationExecutor<Alert> {

    
    List<Alert> findByEnabled(
            Boolean enabled);

    List<Alert> findBySource(
            AlertSource source);

    List<Alert> findBySeverity(
            AlertSeverity severity);

    List<Alert> findByCategory(
            AlertCategory category);

    List<Alert> findByNameContainingIgnoreCase(
            String keyword);

    Optional<Alert> findByNameIgnoreCase(
            String name);
    
    List<Alert> findByArchived(
            Boolean archived);

    List<Alert> findByArchivedFalse();

    List<Alert> findByArchivedTrue();

    long countByArchived(
            Boolean archived);

    List<Alert> findTop10ByArchivedFalseOrderByCreatedAtDesc();

    // ====================================================
    // Water Module
    // ====================================================

    List<Alert> findByDeviceId(
            String deviceId);

    List<Alert> findByStatus(
            AlertStatus status);

    List<Alert> findByDeviceIdAndStatus(
            String deviceId,
            AlertStatus status);

    List<Alert> findByDeviceIdAndSeverity(
            String deviceId,
            AlertSeverity severity);

    List<Alert> findByMessageContainingIgnoreCase(
            String keyword);

    List<Alert> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<Alert> findTop10ByOrderByCreatedAtDesc();

    List<Alert> findTop20ByOrderByCreatedAtDesc();

    List<Alert> findTop50ByOrderByCreatedAtDesc();

    long countByStatus(
            AlertStatus status);

    long countBySeverity(
            AlertSeverity severity);

    long countByDeviceId(
            String deviceId);

    long countBySource(
            AlertSource source);

    long countByCategory(
            AlertCategory category);

    long countByEnabled(
            Boolean enabled);
    
    Optional<Alert> findByDeviceIdAndNameAndStatus(

            String deviceId,

            String name,

            AlertStatus status);
    
    
}