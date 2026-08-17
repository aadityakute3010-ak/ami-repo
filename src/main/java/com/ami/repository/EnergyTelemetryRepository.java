package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.telemetry.EnergyTelemetry;

@Repository
public interface EnergyTelemetryRepository
        extends JpaRepository<EnergyTelemetry, Long>,
        JpaSpecificationExecutor<EnergyTelemetry> {

    List<EnergyTelemetry> findByDeviceId(
            String deviceId);

    Optional<EnergyTelemetry> findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);

    List<EnergyTelemetry> findByDeviceOnline(
            Boolean deviceOnline);

    long countByDeviceOnline(
            Boolean deviceOnline);

    List<EnergyTelemetry> findByReadingTimeAfter(
            LocalDateTime readingTime);

    List<EnergyTelemetry> findByReadingTimeAfterOrderByReadingTimeAsc(
            LocalDateTime readingTime);

    List<EnergyTelemetry> findByReadingTimeBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<EnergyTelemetry> findByDeviceIdAndReadingTimeBetween(
            String deviceId,
            LocalDateTime from,
            LocalDateTime to);

    List<EnergyTelemetry> findTop10ByOrderByReadingTimeDesc();

    List<EnergyTelemetry> findTop20ByOrderByReadingTimeDesc();

    List<EnergyTelemetry> findTop50ByOrderByReadingTimeDesc();

    List<EnergyTelemetry> findByVoltageGreaterThan(
            Double voltage);

    List<EnergyTelemetry> findByCurrentGreaterThan(
            Double current);

    List<EnergyTelemetry> findByConsumptionGreaterThan(
            Double consumption);

    List<EnergyTelemetry> findByBatteryLevelLessThan(
            Double batteryLevel);

    List<EnergyTelemetry> findBySignalStrengthLessThan(
            Integer signalStrength);

    List<EnergyTelemetry> findByTamperDetected(
            Boolean tamperDetected);

    long countByTamperDetected(
            Boolean tamperDetected);

    List<EnergyTelemetry> findByStatus(
            String status);

    List<EnergyTelemetry> findByEmergencyShutdown(
            Boolean emergencyShutdown);

    List<EnergyTelemetry> findByAlarmActive(
            Boolean alarmActive);
}