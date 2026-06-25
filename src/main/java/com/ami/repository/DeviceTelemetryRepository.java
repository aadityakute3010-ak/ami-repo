package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.DeviceTelemetry;
import com.ami.enums.SourceType;

@Repository
public interface DeviceTelemetryRepository
        extends JpaRepository<DeviceTelemetry, Long> {

    List<DeviceTelemetry> findByDeviceId(
            String deviceId);

    List<DeviceTelemetry> findBySourceType(
            SourceType sourceType);

    DeviceTelemetry
    findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);
}