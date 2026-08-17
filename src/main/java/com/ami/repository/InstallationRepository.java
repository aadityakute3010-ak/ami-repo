package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ami.entity.Installation;
import com.ami.enums.AssignmentStatus;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;

public interface InstallationRepository
        extends JpaRepository<Installation, Long>,
        JpaSpecificationExecutor<Installation> {

    Long countByStatus(
            InstallationStatus status);

    Long countByAssignedEngineerIsNotNull();

    List<Installation> findByStatus(
            InstallationStatus status);

    List<Installation> findBySource(
            InstallationSource source);

    List<Installation> findByCity(
            String city);

    List<Installation> findByCustomerId(
            String customerId);

    List<Installation> findByDeviceId(
            String deviceId);

    List<Installation> findByAssignedEngineerId(
            Long engineerId);

    Long countByAssignedEngineerId(
            Long engineerId);

    Long countByAssignedEngineerIdAndStatus(
            Long engineerId,
            InstallationStatus status);

    List<Installation> findByScheduledDateBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<Installation> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    Long countByScheduledDateBetween(
            LocalDateTime from,
            LocalDateTime to);

    Long countBySource(
            InstallationSource source);

    Long countBySourceAndStatus(
            InstallationSource source,
            InstallationStatus status);

    Long countByStatusNotAndScheduledDateBefore(
            InstallationStatus status,
            LocalDateTime date);

    boolean existsByDeviceIdAndStatusIn(
            String deviceId,
            List<InstallationStatus> statuses);

    List<Installation> findByAssignedEngineerIdAndStatus(
            Long engineerId,
            InstallationStatus status);

    List<Installation> findBySourceAndScheduledDateBetween(
            InstallationSource source,
            LocalDateTime from,
            LocalDateTime to);

    List<Installation> findTop10ByOrderByCreatedAtDesc();

    List<Installation> findTop10ByStatusOrderByCreatedAtDesc(
            InstallationStatus status);
    
   

    Long countByCompletedAtIsNotNull();

    Long countByStartedAtIsNotNull();
    
    /*
     * Assignment Status
     */
    List<Installation> findByAssignmentStatus(
            AssignmentStatus assignmentStatus);

    /*
     * Retry Count
     */
    List<Installation> findByAssignmentRetryCountLessThan(
            Integer retryCount);

    /*
     * Last Assignment Attempt
     */
    List<Installation> findByLastAssignmentAttemptBefore(
            LocalDateTime date);

    /*
     * Completion Percentage
     */
    List<Installation> findByCompletionPercentageGreaterThanEqual(
            Double percentage);

    /*
     * Assignment Dashboard
     */
    Long countByAssignmentStatus(
            AssignmentStatus assignmentStatus);

    Long countByAssignmentRetryCountGreaterThan(
            Integer retryCount);

    /*
     * Completed Percentage
     */
    Long countByCompletionPercentage(
            Double percentage);
}