package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Alert;
import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;

@Repository
public interface AlertRepository
        extends JpaRepository<Alert, Long> {

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
}