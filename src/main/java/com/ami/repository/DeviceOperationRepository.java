package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.DeviceOperation;
import com.ami.enums.SourceType;

@Repository
public interface DeviceOperationRepository
extends JpaRepository<DeviceOperation, Long>,
        JpaSpecificationExecutor<DeviceOperation> {

    List<DeviceOperation> findByDeviceId(
            String deviceId);

    List<DeviceOperation> findBySourceType(
            SourceType sourceType);

    List<DeviceOperation> findByOperationType(
            String operationType);

    List<DeviceOperation> findByStatus(
            String status);

    List<DeviceOperation> findByResolved(
            Boolean resolved);

    List<DeviceOperation> findByAssignedTo(
            String assignedTo);

    List<DeviceOperation> findBySourceTypeAndStatus(
            SourceType sourceType,
            String status);

    List<DeviceOperation> findBySourceTypeAndResolved(
            SourceType sourceType,
            Boolean resolved);

    long countByResolved(
            Boolean resolved);

    long countBySourceType(
            SourceType sourceType);

    long countByStatus(
            String status);

    long countByAssignedTo(
            String assignedTo);

    // =====================================================
    // Water Module
    // =====================================================

    List<DeviceOperation> findByRequestedBy(
            String requestedBy);

    List<DeviceOperation> findByRequestedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<DeviceOperation> findByCompletedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<DeviceOperation> findByDeviceIdOrderByRequestedAtDesc(
            String deviceId);

    List<DeviceOperation> findByDeviceIdOrderByCompletedAtDesc(
            String deviceId);

    List<DeviceOperation> findTop10ByOrderByRequestedAtDesc();

    List<DeviceOperation> findTop20ByOrderByRequestedAtDesc();

    List<DeviceOperation> findTop50ByOrderByRequestedAtDesc();

    List<DeviceOperation> findByOperationTypeAndStatus(
            String operationType,
            String status);

    List<DeviceOperation> findByDeviceIdAndStatus(
            String deviceId,
            String status);

    List<DeviceOperation> findByDeviceIdAndOperationType(
            String deviceId,
            String operationType);

    long countByRequestedBy(
            String requestedBy);

    long countByOperationType(
            String operationType);

    long countByDeviceId(
            String deviceId);

    long countByDeviceIdAndStatus(
            String deviceId,
            String status);

    List<DeviceOperation> findByAcknowledgedBy(
            String acknowledgedBy);

    List<DeviceOperation> findByAcknowledgedAtBetween(
            LocalDateTime from,
            LocalDateTime to);
    
    long countBySourceTypeAndStatus(
            SourceType sourceType,
            String status);

    long countBySourceTypeAndResolved(
            SourceType sourceType,
            Boolean resolved);

    long countBySourceTypeAndAssignedTo(
            SourceType sourceType,
            String assignedTo);
}