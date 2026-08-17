package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.ami.entity.telemetry.WaterTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;



@Repository
public interface WaterTelemetryRepository
        extends JpaRepository<WaterTelemetry, Long>,
        JpaSpecificationExecutor<WaterTelemetry> {

    List<WaterTelemetry> findByDeviceId(
            String deviceId);

    Optional<WaterTelemetry> findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);

    List<WaterTelemetry> findByDeviceOnline(
            Boolean deviceOnline);

    long countByDeviceOnline(
            Boolean deviceOnline);

    List<WaterTelemetry> findByReadingTimeAfter(
            LocalDateTime readingTime);

    List<WaterTelemetry> findByReadingTimeAfterOrderByReadingTimeAsc(
            LocalDateTime readingTime);

    List<WaterTelemetry> findByLeakDetected(
            Boolean leakDetected);

    List<WaterTelemetry> findByTamperDetected(
            Boolean tamperDetected);

    List<WaterTelemetry> findByValveStatus(
            String valveStatus);

    List<WaterTelemetry> findByStatus(
            String status);

    List<WaterTelemetry> findByReadingTimeBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<WaterTelemetry> findByDeviceIdAndReadingTimeBetween(
            String deviceId,
            LocalDateTime from,
            LocalDateTime to);

    long countByLeakDetected(
            Boolean leakDetected);

    long countByTamperDetected(
            Boolean tamperDetected);

    List<WaterTelemetry> findTop10ByOrderByReadingTimeDesc();

    List<WaterTelemetry> findTop20ByOrderByReadingTimeDesc();

    List<WaterTelemetry> findTop50ByOrderByReadingTimeDesc();

    List<WaterTelemetry> findByPressureLessThan(
            Double pressure);

    List<WaterTelemetry> findByPressureGreaterThan(
            Double pressure);

    List<WaterTelemetry> findByFlowRateGreaterThan(
            Double flowRate);

    List<WaterTelemetry> findByBatteryLevelLessThan(
            Double batteryLevel);

    List<WaterTelemetry> findBySignalStrengthLessThan(
            Integer signalStrength);
    
    List<WaterTelemetry> findByDeviceIdOrderByReadingTimeAsc(
            String deviceId);
    
    List<WaterTelemetry> findByLeakDetectedTrue();
}