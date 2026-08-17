package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.telemetry.SolarTelemetry;

@Repository
public interface SolarTelemetryRepository
        extends JpaRepository<SolarTelemetry, Long>,
        JpaSpecificationExecutor<SolarTelemetry> {

    List<SolarTelemetry> findByDeviceId(
            String deviceId);

    Optional<SolarTelemetry> findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);

    List<SolarTelemetry> findByDeviceOnline(
            Boolean deviceOnline);

    long countByDeviceOnline(
            Boolean deviceOnline);

    List<SolarTelemetry> findByReadingTimeAfter(
            LocalDateTime readingTime);

    List<SolarTelemetry> findByReadingTimeAfterOrderByReadingTimeAsc(
            LocalDateTime readingTime);

    List<SolarTelemetry> findByReadingTimeBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<SolarTelemetry> findByDeviceIdAndReadingTimeBetween(
            String deviceId,
            LocalDateTime from,
            LocalDateTime to);

    List<SolarTelemetry> findTop10ByOrderByReadingTimeDesc();

    List<SolarTelemetry> findTop20ByOrderByReadingTimeDesc();

    List<SolarTelemetry> findTop50ByOrderByReadingTimeDesc();

    List<SolarTelemetry> findByVoltageGreaterThan(
            Double voltage);

    List<SolarTelemetry> findByCurrentGreaterThan(
            Double current);

    List<SolarTelemetry> findByTemperatureGreaterThan(
            Double temperature);

    List<SolarTelemetry> findByConsumptionGreaterThan(
            Double consumption);

    List<SolarTelemetry> findByBatteryLevelLessThan(
            Double batteryLevel);

    List<SolarTelemetry> findBySignalStrengthLessThan(
            Integer signalStrength);

    List<SolarTelemetry> findByStatus(
            String status);

    List<SolarTelemetry> findByEmergencyShutdown(
            Boolean emergencyShutdown);

    List<SolarTelemetry> findByAlarmActive(
            Boolean alarmActive);
}