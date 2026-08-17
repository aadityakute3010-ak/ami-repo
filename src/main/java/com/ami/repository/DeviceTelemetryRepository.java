package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.DeviceTelemetry;
import com.ami.enums.SourceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface DeviceTelemetryRepository
extends JpaRepository<DeviceTelemetry, Long>,
        JpaSpecificationExecutor<DeviceTelemetry> {

    
    List<DeviceTelemetry> findByDeviceId(
            String deviceId);

    List<DeviceTelemetry> findBySourceType(
            SourceType sourceType);

    Optional<DeviceTelemetry> findTopByDeviceIdOrderByReadingTimeDesc(
            String deviceId);

    List<DeviceTelemetry> findByDeviceOnline(
            Boolean deviceOnline);

    long countByDeviceOnline(
            Boolean deviceOnline);

    long countBySourceType(
            SourceType sourceType);

    List<DeviceTelemetry> findBySourceTypeAndDeviceOnline(
            SourceType sourceType,
            Boolean deviceOnline);

    List<DeviceTelemetry> findBySourceTypeOrderByReadingTimeAsc(
            SourceType sourceType);

    List<DeviceTelemetry> findByDeviceIdOrderByReadingTimeAsc(
            String deviceId);
    
    List<DeviceTelemetry> findByReadingTimeAfter(
            LocalDateTime readingTime);

    List<DeviceTelemetry> findBySourceTypeAndReadingTimeAfter(
            SourceType sourceType,
            LocalDateTime readingTime);
    
    List<DeviceTelemetry> findByReadingTimeAfterOrderByReadingTimeAsc(
            LocalDateTime readingTime);

    List<DeviceTelemetry> findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
            SourceType sourceType,
            LocalDateTime readingTime);
    
    // gas module 
    
    List<DeviceTelemetry> findByGasConcentrationGreaterThan(
            Double gasConcentration);

    List<DeviceTelemetry> findByDifferentialPressureGreaterThan(
            Double differentialPressure);

    List<DeviceTelemetry> findByTotalFlowGreaterThan(
            Double totalFlow);

    List<DeviceTelemetry> findByLeakSeverity(
            String leakSeverity);

    List<DeviceTelemetry> findByEmergencyShutdown(
            Boolean emergencyShutdown);

    List<DeviceTelemetry> findByAlarmActive(
            Boolean alarmActive);

    List<DeviceTelemetry> findByGasQuality(
            String gasQuality);

    List<DeviceTelemetry> findByGasDensityGreaterThan(
            Double gasDensity);

    List<DeviceTelemetry> findBySourceTypeAndReadingTimeBetween(
            SourceType sourceType,
            LocalDateTime from,
            LocalDateTime to);

    Optional<DeviceTelemetry> findTopBySourceTypeOrderByReadingTimeDesc(
            SourceType sourceType);
    
  

    // ====================================================
    // Water Module
    // ====================================================

    List<DeviceTelemetry> findByLeakDetected(
            Boolean leakDetected);

    List<DeviceTelemetry> findByTamperDetected(
            Boolean tamperDetected);

    List<DeviceTelemetry> findByValveStatus(
            String valveStatus);

    List<DeviceTelemetry> findByStatus(
            String status);

    List<DeviceTelemetry> findByReadingTimeBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<DeviceTelemetry> findByDeviceIdAndReadingTimeBetween(
            String deviceId,
            LocalDateTime from,
            LocalDateTime to);

    long countByLeakDetected(
            Boolean leakDetected);

    long countByTamperDetected(
            Boolean tamperDetected);

    List<DeviceTelemetry> findTop10ByOrderByReadingTimeDesc();

    List<DeviceTelemetry> findTop20ByOrderByReadingTimeDesc();

    List<DeviceTelemetry> findTop50ByOrderByReadingTimeDesc();

    List<DeviceTelemetry> findByPressureLessThan(
            Double pressure);

    List<DeviceTelemetry> findByPressureGreaterThan(
            Double pressure);

    List<DeviceTelemetry> findByFlowRateGreaterThan(
            Double flowRate);

    List<DeviceTelemetry> findByBatteryLevelLessThan(
            Double batteryLevel);

    List<DeviceTelemetry> findBySignalStrengthLessThan(
            Integer signalStrength);
    
 // ====================================================
 // Dashboard Aggregation
 // ====================================================

 @Query("""
 SELECT COUNT(DISTINCT d.deviceId)
 FROM DeviceTelemetry d
 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
 """)
 long countDistinctDevices(
         @Param("sourceType") SourceType sourceType);

 @Query("""
 SELECT COUNT(DISTINCT d.deviceId)
 FROM DeviceTelemetry d
 WHERE d.deviceOnline = true
 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
 """)
 long countDistinctOnlineDevices(
         @Param("sourceType") SourceType sourceType);

 @Query("""
 SELECT COUNT(DISTINCT d.deviceId)
 FROM DeviceTelemetry d
 WHERE d.leakDetected = true
 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
 """)
 long countDistinctLeakDetectedDevices(
         @Param("sourceType") SourceType sourceType);

 @Query("""
 SELECT COUNT(DISTINCT d.deviceId)
 FROM DeviceTelemetry d
 WHERE d.pumpStatus = com.ami.enums.PumpStatus.RUNNING
 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
 """)
 long countDistinctPumpRunningDevices(
         @Param("sourceType") SourceType sourceType);

 @Query("""
 SELECT COUNT(DISTINCT d.deviceId)
 FROM DeviceTelemetry d
 WHERE d.pumpStatus = com.ami.enums.PumpStatus.STOPPED
 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
 """)
 long countDistinctPumpStoppedDevices(
         @Param("sourceType") SourceType sourceType);
 
 @Query("""
		 SELECT COALESCE(SUM(d.consumption), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getTotalConsumption(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(AVG(d.pressure), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getAveragePressure(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(AVG(d.temperature), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getAverageTemperature(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(AVG(d.flowRate), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getAverageFlowRate(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(AVG(d.batteryLevel), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getAverageBatteryLevel(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(AVG(d.signalStrength), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getAverageSignalStrength(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(MAX(d.consumption), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getMaximumConsumption(
		         @Param("sourceType") SourceType sourceType);

		 @Query("""
		 SELECT COALESCE(MIN(d.consumption), 0)
		 FROM DeviceTelemetry d
		 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
		 """)
		 Double getMinimumConsumption(
		         @Param("sourceType") SourceType sourceType);
		 
		 @Query("""
				 SELECT COUNT(DISTINCT d.deviceId)
				 FROM DeviceTelemetry d
				 WHERE d.tamperDetected = true
				 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
				 """)
				 long countDistinctTamperDevices(
				         @Param("sourceType") SourceType sourceType);

				 @Query("""
				 SELECT COUNT(DISTINCT d.deviceId)
				 FROM DeviceTelemetry d
				 WHERE UPPER(d.valveStatus) = 'OPEN'
				 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
				 """)
				 long countDistinctValveOpenDevices(
				         @Param("sourceType") SourceType sourceType);

				 @Query("""
				 SELECT COUNT(DISTINCT d.deviceId)
				 FROM DeviceTelemetry d
				 WHERE UPPER(d.valveStatus) = 'CLOSED'
				 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
				 """)
				 long countDistinctValveClosedDevices(
				         @Param("sourceType") SourceType sourceType);

				 @Query("""
				 SELECT COUNT(DISTINCT d.deviceId)
				 FROM DeviceTelemetry d
				 WHERE d.batteryLevel < 20
				 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
				 """)
				 long countDistinctLowBatteryDevices(
				         @Param("sourceType") SourceType sourceType);

				 @Query("""
				 SELECT COUNT(DISTINCT d.deviceId)
				 FROM DeviceTelemetry d
				 WHERE d.signalStrength < 30
				 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
				 """)
				 long countDistinctPoorSignalDevices(
				         @Param("sourceType") SourceType sourceType);
				 
				 @Query("""
						 SELECT COALESCE(AVG(d.consumption), 0)
						 FROM DeviceTelemetry d
						 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 Double getAverageConsumption(
						         @Param("sourceType") SourceType sourceType);

						 @Query("""
						 SELECT COUNT(d)
						 FROM DeviceTelemetry d
						 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 long countTelemetryReadings(
						         @Param("sourceType") SourceType sourceType);

						 @Query("""
						 SELECT COUNT(d)
						 FROM DeviceTelemetry d
						 WHERE d.deviceOnline = true
						 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 long countOnlineReadings(
						         @Param("sourceType") SourceType sourceType);

						 @Query("""
						 SELECT COUNT(d)
						 FROM DeviceTelemetry d
						 WHERE d.leakDetected = true
						 AND (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 long countLeakDetectedReadings(
						         @Param("sourceType") SourceType sourceType);

						 @Query("""
						 SELECT COALESCE(AVG(d.pipelineHealthScore), 0)
						 FROM DeviceTelemetry d
						 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 Double getAveragePipelineHealth(
						         @Param("sourceType") SourceType sourceType);

						 @Query("""
						 SELECT COALESCE(AVG(d.sensorHealthScore), 0)
						 FROM DeviceTelemetry d
						 WHERE (:sourceType IS NULL OR d.sourceType = :sourceType)
						 """)
						 Double getAverageSensorHealth(
						         @Param("sourceType") SourceType sourceType);
						 
						 @Query("""
								 SELECT COUNT(DISTINCT d.deviceId)
								 FROM DeviceTelemetry d
								 WHERE d.sourceType = :sourceType
								 """)
								 long countDistinctDevicesBySourceType(
								         @Param("sourceType") SourceType sourceType);
 
}
