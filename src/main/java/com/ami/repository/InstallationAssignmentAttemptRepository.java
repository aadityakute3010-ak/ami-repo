package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationAssignmentAttempt;

@Repository
public interface InstallationAssignmentAttemptRepository
        extends JpaRepository<InstallationAssignmentAttempt, Long> {

    // Installation

    List<InstallationAssignmentAttempt>
    findByInstallationIdOrderByAttemptedAtDesc(
            Long installationId);

    Optional<InstallationAssignmentAttempt>
    findTopByInstallationIdOrderByAttemptedAtDesc(
            Long installationId);

    Long countByInstallationId(
            Long installationId);

    // Engineer

    List<InstallationAssignmentAttempt>
    findByEngineerIdOrderByAttemptedAtDesc(
            Long engineerId);

    Long countByEngineerId(
            Long engineerId);

    // Success / Failure

    List<InstallationAssignmentAttempt>
    findBySuccessful(
            Boolean successful);

    Long countBySuccessful(
            Boolean successful);

    // Date

    List<InstallationAssignmentAttempt>
    findByAttemptedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    // Status

    List<InstallationAssignmentAttempt>
    findByInstallationStatus(
            com.ami.enums.InstallationStatus status);

    Long countByInstallationStatus(
            com.ami.enums.InstallationStatus status);

}