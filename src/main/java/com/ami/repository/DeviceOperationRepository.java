package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.DeviceOperation;
import com.ami.enums.SourceType;

@Repository
public interface DeviceOperationRepository
        extends JpaRepository<DeviceOperation, Long> {

    List<DeviceOperation> findByDeviceId(
            String deviceId);

    List<DeviceOperation> findBySourceType(
            SourceType sourceType);

    List<DeviceOperation> findByOperationType(
            String operationType);
}