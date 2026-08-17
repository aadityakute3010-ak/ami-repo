package com.ami.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.AlertAssignment;
import com.ami.entity.AlertAssignment.AssignmentType;

@Repository
public interface AlertAssignmentRepository
        extends JpaRepository<AlertAssignment, Long> {

    // =========================================================
    // ADMIN ASSIGNMENTS
    // =========================================================

    List<AlertAssignment> findByAdminIdAndActiveTrue(
            Long adminId);

    Optional<AlertAssignment>
    findByAlertIdAndAdminIdAndActiveTrue(
            Long alertId,
            Long adminId);

    // =========================================================
    // ALERT ASSIGNMENTS
    // =========================================================

    List<AlertAssignment> findByAlertIdAndActiveTrue(
            Long alertId);

    List<AlertAssignment> findByAlertId(
            Long alertId);

    // =========================================================
    // DEVICE ASSIGNMENTS
    // =========================================================

    List<AlertAssignment> findByDeviceIdAndActiveTrue(
            String deviceId);

    Optional<AlertAssignment>
    findByAlertIdAndDeviceIdAndActiveTrue(
            Long alertId,
            String deviceId);

    // =========================================================
    // ACTIVE ASSIGNMENTS
    // =========================================================

    List<AlertAssignment> findByActiveTrue();

    long countByActiveTrue();

    // =========================================================
    // ASSIGNMENT TYPE
    // =========================================================

    long countByAssignmentTypeAndActiveTrue(
            AssignmentType assignmentType);
    
  
}