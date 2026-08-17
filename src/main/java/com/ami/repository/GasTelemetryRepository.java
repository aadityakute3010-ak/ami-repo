package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.telemetry.GasTelemetry;

@Repository
public interface GasTelemetryRepository
        extends JpaRepository<GasTelemetry, Long>,
        JpaSpecificationExecutor<GasTelemetry> {

    List<GasTelemetry> findByDeviceId(
            String deviceId);

    Optional<GasTelemetry> findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);

    List<GasTelemetry> findByDeviceOnline(
            Boolean deviceOnline);

    long countByDeviceOnline(
            Boolean deviceOnline);

    List<GasTelemetry> findByReadingTimeAfter(
            LocalDateTime readingTime);

    List<GasTelemetry> findByReadingTimeAfterOrderByReadingTimeAsc(
            LocalDateTime readingTime);

    List<GasTelemetry> findByReadingTimeBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<GasTelemetry> findByDeviceIdAndReadingTimeBetween(
            String deviceId,
            LocalDateTime from,
            LocalDateTime to);

    List<GasTelemetry> findTop10ByOrderByReadingTimeDesc();

    List<GasTelemetry> findTop20ByOrderByReadingTimeDesc();

    List<GasTelemetry> findTop50ByOrderByReadingTimeDesc();

    List<GasTelemetry> findByPressureLessThan(
            Double pressure);

    List<GasTelemetry> findByPressureGreaterThan(
            Double pressure);

    List<GasTelemetry> findByFlowRateGreaterThan(
            Double flowRate);

    List<GasTelemetry> findByBatteryLevelLessThan(
            Double batteryLevel);

    List<GasTelemetry> findBySignalStrengthLessThan(
            Integer signalStrength);

    List<GasTelemetry> findByStatus(
            String status);

    List<GasTelemetry> findByLeakDetected(
            Boolean leakDetected);

    long countByLeakDetected(
            Boolean leakDetected);

    List<GasTelemetry> findByGasConcentrationGreaterThan(
            Double gasConcentration);

    List<GasTelemetry> findByGasDensityGreaterThan(
            Double gasDensity);

    List<GasTelemetry> findByGasQuality(
            String gasQuality);

    List<GasTelemetry> findByDifferentialPressureGreaterThan(
            Double differentialPressure);

    List<GasTelemetry> findByEmergencyShutdown(
            Boolean emergencyShutdown);

    List<GasTelemetry> findByAlarmActive(
            Boolean alarmActive);
}