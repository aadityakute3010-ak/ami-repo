package com.ami.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ami.entity.Payload;
import com.ami.enums.PayloadStatus;
import com.ami.enums.SourceType;
import java.time.LocalDate;
import com.ami.repository.projection.PayloadDailyAnalyticsProjection;
import com.ami.repository.projection.PayloadHourlyAnalyticsProjection;

public interface PayloadRepository extends JpaRepository<Payload, Long>, JpaSpecificationExecutor<Payload> {

	// Dashboard Statistics
	long countByStatus(PayloadStatus status);

	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			WHERE p.device.meter.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt >= :since
			""")
	long countOnlineDevices(@Param("since") LocalDateTime since);

//	// Payload Listing with Filters - SUPER ADMIN
//	@Query("""
//			SELECT p
//			FROM Payload p
//			JOIN p.device d
//			JOIN d.meter m
//			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
//
//			AND (
//				:deviceId IS NULL
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :deviceId, '%'))
//			)
//
//			AND (
//				:consumer IS NULL
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :consumer, '%'))
//			)
//
//			AND (
//				:meterNumber IS NULL
//				OR LOWER(d.deviceId)
//					LIKE LOWER(CONCAT('%', :meterNumber, '%'))
//			)
//
//			AND (
//				:macAddress IS NULL
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))
//			)
//
//			AND (
//				:status IS NULL
//				OR p.status = :status
//			)
//
//			AND (
//				:sourceType IS NULL
//				OR m.sourceType = :sourceType
//			)
//
//			AND (
//				:from IS NULL
//				OR p.receivedAt >= :from
//			)
//
//			AND (
//				:to IS NULL
//				OR p.receivedAt <= :to
//			)
//
//			AND (
//				:minBattery IS NULL
//				OR p.batteryPercentage >= :minBattery
//			)
//
//			AND (
//				:maxBattery IS NULL
//				OR p.batteryPercentage <= :maxBattery
//			)
//
//			AND (
//				:minSignal IS NULL
//				OR p.signalQuality >= :minSignal
//			)
//
//			AND (
//				:maxSignal IS NULL
//				OR p.signalQuality <= :maxSignal
//			)
//
//			AND (
//				:search IS NULL
//
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(m.meterName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.failureReason) LIKE LOWER(CONCAT('%', :search, '%'))
//			)
//			""")
//	Page<Payload> findWithFiltersForSuperAdmin(@Param("deviceId") String deviceId, @Param("consumer") String consumer,
//			@Param("meterNumber") String meterNumber, @Param("macAddress") String macAddress,
//			@Param("status") PayloadStatus status, @Param("sourceType") SourceType sourceType,
//			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("minBattery") Integer minBattery,
//			@Param("maxBattery") Integer maxBattery, @Param("minSignal") Integer minSignal,
//			@Param("maxSignal") Integer maxSignal, @Param("search") String search, Pageable pageable);
//
//	// Payload Listing with Filters - ADMIN
//	@Query("""
//			SELECT p
//			FROM Payload p
//			JOIN p.device d
//			JOIN d.meter m
//			WHERE d.assignedAdmin.id = :adminId
//			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
//
//			AND (
//				:deviceId IS NULL
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :deviceId, '%'))
//			)
//
//			AND (
//				:consumer IS NULL
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :consumer, '%'))
//			)
//
//			AND (
//				:meterNumber IS NULL
//				OR LOWER(d.deviceId)
//					LIKE LOWER(CONCAT('%', :meterNumber, '%'))
//			)
//
//			AND (
//				:macAddress IS NULL
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))
//			)
//
//			AND (
//				:status IS NULL
//				OR p.status = :status
//			)
//
//			AND (
//				:sourceType IS NULL
//				OR m.sourceType = :sourceType
//			)
//
//			AND (
//				:from IS NULL
//				OR p.receivedAt >= :from
//			)
//
//			AND (
//				:to IS NULL
//				OR p.receivedAt <= :to
//			)
//
//			AND (
//				:minBattery IS NULL
//				OR p.batteryPercentage >= :minBattery
//			)
//
//			AND (
//				:maxBattery IS NULL
//				OR p.batteryPercentage <= :maxBattery
//			)
//
//			AND (
//				:minSignal IS NULL
//				OR p.signalQuality >= :minSignal
//			)
//
//			AND (
//				:maxSignal IS NULL
//				OR p.signalQuality <= :maxSignal
//			)
//
//			AND (
//				:search IS NULL
//
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(m.meterName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.failureReason) LIKE LOWER(CONCAT('%', :search, '%'))
//			)
//			""")
//	Page<Payload> findWithFiltersForAdmin(@Param("adminId") Long adminId, @Param("deviceId") String deviceId,
//			@Param("consumer") String consumer, @Param("meterNumber") String meterNumber,
//			@Param("macAddress") String macAddress, @Param("status") PayloadStatus status,
//			@Param("sourceType") SourceType sourceType, @Param("from") LocalDateTime from,
//			@Param("to") LocalDateTime to, @Param("minBattery") Integer minBattery,
//			@Param("maxBattery") Integer maxBattery, @Param("minSignal") Integer minSignal,
//			@Param("maxSignal") Integer maxSignal, @Param("search") String search, Pageable pageable);
//
//	// Payload Listing with Filters - USER
//	@Query("""
//			SELECT p
//			FROM Payload p
//			JOIN p.device d
//			JOIN d.meter m
//			WHERE d.assignedUser.id = :userId
//			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
//
//			AND (
//				:deviceId IS NULL
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :deviceId, '%'))
//			)
//
//			AND (
//				:consumer IS NULL
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :consumer, '%'))
//			)
//
//			AND (
//				:meterNumber IS NULL
//				OR LOWER(d.deviceId)
//					LIKE LOWER(CONCAT('%', :meterNumber, '%'))
//			)
//
//			AND (
//				:macAddress IS NULL
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))
//			)
//
//			AND (
//				:status IS NULL
//				OR p.status = :status
//			)
//
//			AND (
//				:sourceType IS NULL
//				OR m.sourceType = :sourceType
//			)
//
//			AND (
//				:from IS NULL
//				OR p.receivedAt >= :from
//			)
//
//			AND (
//				:to IS NULL
//				OR p.receivedAt <= :to
//			)
//
//			AND (
//				:minBattery IS NULL
//				OR p.batteryPercentage >= :minBattery
//			)
//
//			AND (
//				:maxBattery IS NULL
//				OR p.batteryPercentage <= :maxBattery
//			)
//
//			AND (
//				:minSignal IS NULL
//				OR p.signalQuality >= :minSignal
//			)
//
//			AND (
//				:maxSignal IS NULL
//				OR p.signalQuality <= :maxSignal
//			)
//
//			AND (
//				:search IS NULL
//
//				OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(m.meterName) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.consumerNumber) LIKE LOWER(CONCAT('%', :search, '%'))
//
//				OR LOWER(p.failureReason) LIKE LOWER(CONCAT('%', :search, '%'))
//			)
//			""")
//	Page<Payload> findWithFiltersForUser(@Param("userId") Long userId, @Param("deviceId") String deviceId,
//			@Param("consumer") String consumer, @Param("meterNumber") String meterNumber,
//			@Param("macAddress") String macAddress, @Param("status") PayloadStatus status,
//			@Param("sourceType") SourceType sourceType, @Param("from") LocalDateTime from,
//			@Param("to") LocalDateTime to, @Param("minBattery") Integer minBattery,
//			@Param("maxBattery") Integer maxBattery, @Param("minSignal") Integer minSignal,
//			@Param("maxSignal") Integer maxSignal, @Param("search") String search, Pageable pageable);

	// Consumption Trend
	@Query("""
			SELECT p
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.id = :deviceId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :from AND :to
			         AND p.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			ORDER BY p.receivedAt ASC
			""")
	List<Payload> findForConsumptionTrend(@Param("deviceId") Long deviceId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	// 24 Hour Readings
	@Query("""
			SELECT p
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.id = :deviceId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :from AND :to
			AND p.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			ORDER BY p.receivedAt ASC
			""")
	List<Payload> find24HourReadings(@Param("deviceId") Long deviceId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedAdmin.id = :adminId
			""")
	long countByAssignedAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedAdmin.id = :adminId
			AND p.status = :status
			""")
	long countByAssignedAdminAndStatus(@Param("adminId") Long adminId, @Param("status") PayloadStatus status);

	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedAdmin.id = :adminId
			AND p.receivedAt >= :since
			""")
	long countOnlineDevicesByAdmin(@Param("adminId") Long adminId, @Param("since") LocalDateTime since);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedUser.id = :userId
			""")
	long countByAssignedUser(@Param("userId") Long userId);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedUser.id = :userId
			AND p.status = :status
			""")
	long countByAssignedUserAndStatus(@Param("userId") Long userId, @Param("status") PayloadStatus status);

	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.assignedUser.id = :userId
			AND p.receivedAt >= :since
			""")
	long countOnlineDevicesByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countByReceivedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.status = :status
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countByStatusAndReceivedAtBetween(@Param("status") PayloadStatus status, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(DISTINCT d)
			FROM Device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.online = true
			AND d.lastSyncTime BETWEEN :start AND :end
			""")
	long countOnlineDevicesToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countTodayByAssignedAdmin(@Param("adminId") Long adminId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND p.status = :status
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countTodayByAssignedAdminAndStatus(@Param("adminId") Long adminId, @Param("status") PayloadStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(DISTINCT d)
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.online = true
			AND d.lastSyncTime BETWEEN :start AND :end
			""")
	long countOnlineDevicesTodayByAdmin(@Param("adminId") Long adminId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.assignedUser.id = :userId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countTodayByAssignedUser(@Param("userId") Long userId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			JOIN p.device d
			JOIN d.meter m
			WHERE d.assignedUser.id = :userId
			AND p.status = :status
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND p.receivedAt BETWEEN :start AND :end
			""")
	long countTodayByAssignedUserAndStatus(@Param("userId") Long userId, @Param("status") PayloadStatus status,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(DISTINCT d)
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedUser.id = :userId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND d.online = true
			AND d.lastSyncTime BETWEEN :start AND :end
			""")
	long countOnlineDevicesTodayByUser(@Param("userId") Long userId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			SELECT COUNT(d)
			FROM Device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			""")
	long countTotalDevices();

	@Query("""
			SELECT COUNT(d)
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			""")
	long countTotalDevicesByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COUNT(d)
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedUser.id = :userId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			""")
	long countTotalDevicesByUser(@Param("userId") Long userId);

	Optional<Payload> findTopByDevice_IdAndStatusInOrderByReceivedAtDesc(Long deviceId,
			Collection<PayloadStatus> statuses);

	@Query("""
			SELECT COALESCE(AVG(p.batteryPercentage), 0)
			FROM Payload p
			WHERE p.batteryPercentage IS NOT NULL
			""")
	Double findAverageBattery();

	@Query("""
			SELECT COALESCE(AVG(p.signalQuality), 0)
			FROM Payload p
			WHERE p.signalQuality IS NOT NULL
			""")
	Double findAverageSignal();

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			WHERE p.receivedAt BETWEEN :from AND :to
			AND p.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			""")
	Double findTodayConsumption(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("""
			SELECT COALESCE(AVG(p.batteryPercentage), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedAdmin.id = :adminId
			AND p.batteryPercentage IS NOT NULL
			""")
	Double findAverageBatteryByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COALESCE(AVG(p.signalQuality), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedAdmin.id = :adminId
			AND p.signalQuality IS NOT NULL
			""")
	Double findAverageSignalByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedAdmin.id = :adminId
			AND p.receivedAt BETWEEN :from AND :to
			AND p.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			""")
	Double findTodayConsumptionByAdmin(@Param("adminId") Long adminId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	@Query("""
			SELECT COALESCE(AVG(p.batteryPercentage), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedUser.id = :userId
			AND p.batteryPercentage IS NOT NULL
			""")
	Double findAverageBatteryByUser(@Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(AVG(p.signalQuality), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedUser.id = :userId
			AND p.signalQuality IS NOT NULL
			""")
	Double findAverageSignalByUser(@Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			JOIN p.device d
			WHERE d.assignedUser.id = :userId
			AND p.receivedAt BETWEEN :from AND :to
			AND p.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			""")
	Double findTodayConsumptionByUser(@Param("userId") Long userId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.meter.sourceType = :sourceType
			""")
	long countBySourceType(@Param("sourceType") SourceType sourceType);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			AND p.device.meter.sourceType = :sourceType
			""")
	long countByAssignedAdminAndSourceType(@Param("adminId") Long adminId, @Param("sourceType") SourceType sourceType);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			AND p.device.meter.sourceType = :sourceType
			""")
	long countByAssignedUserAndSourceType(@Param("userId") Long userId, @Param("sourceType") SourceType sourceType);

	@Query("""
			SELECT COALESCE(AVG(p.signalPower), 0)
			FROM Payload p
			WHERE p.signalPower IS NOT NULL
			""")
	Double findAverageSignalPower();

	@Query("""
			SELECT COALESCE(AVG(p.signalPower), 0)
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			AND p.signalPower IS NOT NULL
			""")
	Double findAverageSignalPowerByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COALESCE(AVG(p.signalPower), 0)
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			AND p.signalPower IS NOT NULL
			""")
	Double findAverageSignalPowerByUser(@Param("userId") Long userId);

	@Query("""
			SELECT MAX(p.receivedAt)
			FROM Payload p
			""")
	LocalDateTime findLastPayloadTime();

	@Query("""
			SELECT MAX(p.receivedAt)
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			""")
	LocalDateTime findLastPayloadTimeByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT MAX(p.receivedAt)
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			""")
	LocalDateTime findLastPayloadTimeByUser(@Param("userId") Long userId);

	@Query("""
			SELECT
				d.readingDate AS readingDate,
				COALESCE(SUM(d.openingReading), 0) AS openingReading,
				COALESCE(SUM(d.closingReading), 0) AS closingReading,
				COALESCE(SUM(d.totalReading), 0) AS totalReading,
				COALESCE(SUM(d.dailyConsumption), 0) AS consumption
			FROM DailyConsumption d
			WHERE d.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY d.readingDate
			ORDER BY d.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrend(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	@Query("""
			SELECT
				d.readingDate AS readingDate,
				COALESCE(SUM(d.openingReading), 0) AS openingReading,
				COALESCE(SUM(d.closingReading), 0) AS closingReading,
				COALESCE(SUM(d.totalReading), 0) AS totalReading,
				COALESCE(SUM(d.dailyConsumption), 0) AS consumption
			FROM DailyConsumption d
			WHERE d.device.assignedAdmin.id = :adminId
			AND d.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY d.readingDate
			ORDER BY d.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrendByAdmin(@Param("adminId") Long adminId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("""
			SELECT
				d.readingDate AS readingDate,
				COALESCE(SUM(d.openingReading), 0) AS openingReading,
				COALESCE(SUM(d.closingReading), 0) AS closingReading,
				COALESCE(SUM(d.totalReading), 0) AS totalReading,
				COALESCE(SUM(d.dailyConsumption), 0) AS consumption
			FROM DailyConsumption d
			WHERE d.device.assignedUser.id = :userId
			AND d.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY d.readingDate
			ORDER BY d.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrendByUser(@Param("userId") Long userId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("""
			SELECT
				HOUR(p.receivedAt) AS readingHour,
				MAX(p.receivedAt) AS lastPayloadTime,
				COALESCE(SUM(p.endReading), 0) AS totalReading,
				COALESCE(SUM(p.consumption), 0) AS consumption
			FROM Payload p
			WHERE p.receivedAt BETWEEN :from AND :to
			AND p.status IN :statuses
			GROUP BY HOUR(p.receivedAt)
			ORDER BY HOUR(p.receivedAt) ASC
			""")
	List<PayloadHourlyAnalyticsProjection> findHourlyAnalytics(@Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to, @Param("statuses") List<PayloadStatus> statuses);

	@Query("""
			SELECT
				HOUR(p.receivedAt) AS readingHour,
				MAX(p.receivedAt) AS lastPayloadTime,
				COALESCE(SUM(p.endReading), 0) AS totalReading,
				COALESCE(SUM(p.consumption), 0) AS consumption
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			AND p.receivedAt BETWEEN :from AND :to
			AND p.status IN :statuses
			GROUP BY HOUR(p.receivedAt)
			ORDER BY HOUR(p.receivedAt) ASC
			""")
	List<PayloadHourlyAnalyticsProjection> findHourlyAnalyticsByAdmin(@Param("adminId") Long adminId,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
			@Param("statuses") List<PayloadStatus> statuses);

	@Query("""
			SELECT
				HOUR(p.receivedAt) AS readingHour,
				MAX(p.receivedAt) AS lastPayloadTime,
				COALESCE(SUM(p.endReading), 0) AS totalReading,
				COALESCE(SUM(p.consumption), 0) AS consumption
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			AND p.receivedAt BETWEEN :from AND :to
			AND p.status IN :statuses
			GROUP BY HOUR(p.receivedAt)
			ORDER BY HOUR(p.receivedAt) ASC
			""")
	List<PayloadHourlyAnalyticsProjection> findHourlyAnalyticsByUser(@Param("userId") Long userId,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
			@Param("statuses") List<PayloadStatus> statuses);

	// =====================================================
	// Source Summary Consumption - SUPER ADMIN
	// =====================================================

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			WHERE p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findTotalConsumptionByStatuses(@Param("statuses") List<PayloadStatus> statuses);

	@Query("""
			SELECT COALESCE(AVG(p.consumption), 0)
			FROM Payload p
			WHERE p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findAverageConsumptionByStatuses(@Param("statuses") List<PayloadStatus> statuses);

	// =====================================================
	// Source Summary Consumption - ADMIN
	// =====================================================

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			AND p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findTotalConsumptionByAdminAndStatuses(@Param("adminId") Long adminId,

			@Param("statuses") List<PayloadStatus> statuses);

	@Query("""
			SELECT COALESCE(AVG(p.consumption), 0)
			FROM Payload p
			WHERE p.device.assignedAdmin.id = :adminId
			AND p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findAverageConsumptionByAdminAndStatuses(@Param("adminId") Long adminId,

			@Param("statuses") List<PayloadStatus> statuses);

	// =====================================================
	// Source Summary Consumption - USER
	// =====================================================

	@Query("""
			SELECT COALESCE(SUM(p.consumption), 0)
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			AND p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findTotalConsumptionByUserAndStatuses(@Param("userId") Long userId,

			@Param("statuses") List<PayloadStatus> statuses);

	@Query("""
			SELECT COALESCE(AVG(p.consumption), 0)
			FROM Payload p
			WHERE p.device.assignedUser.id = :userId
			AND p.status IN :statuses
			AND p.consumption IS NOT NULL
			""")
	Double findAverageConsumptionByUserAndStatuses(@Param("userId") Long userId,
			@Param("statuses") List<PayloadStatus> statuses);

	// =====================================================
	// Device Payload History
	// =====================================================

	@Query("""
			SELECT p
			FROM Payload p
			WHERE p.device.id = :deviceId
			ORDER BY p.receivedAt DESC
			""")
	Page<Payload> findPayloadHistoryByDeviceId(@Param("deviceId") Long deviceId, Pageable pageable);

	@Query("""
			SELECT payload
			FROM Payload payload
			WHERE payload.device.id = :deviceId
			AND payload.receivedAt >= :periodStart
			AND payload.receivedAt < :periodEndExclusive
			AND payload.startReading IS NOT NULL
			AND payload.endReading IS NOT NULL
			AND payload.endReading >= payload.startReading
			AND payload.status IN (
				com.ami.enums.PayloadStatus.SUCCESS,
				com.ami.enums.PayloadStatus.WARNING
			)
			ORDER BY payload.receivedAt ASC
			""")
	List<Payload> findValidBillingPayloads(@Param("deviceId") Long deviceId,
			@Param("periodStart") LocalDateTime periodStart,
			@Param("periodEndExclusive") LocalDateTime periodEndExclusive);

	Optional<Payload> findFirstByDeviceIdAndReceivedAtBeforeAndEndReadingIsNotNullOrderByReceivedAtDesc(Long deviceId,
			LocalDateTime beforeDateTime);

	boolean existsByDevice_IdAndStartReadingAndEndReadingAndStatusIn(Long deviceId, Double startReading,
			Double endReading, List<PayloadStatus> statuses);

}