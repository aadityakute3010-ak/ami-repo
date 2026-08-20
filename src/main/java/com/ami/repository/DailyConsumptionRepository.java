package com.ami.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.DailyConsumption;
import com.ami.entity.Device;
import com.ami.repository.projection.PayloadDailyAnalyticsProjection;

public interface DailyConsumptionRepository extends JpaRepository<DailyConsumption, Long> {

	Optional<DailyConsumption> findByDeviceAndReadingDate(Device device, LocalDate readingDate);

	@Query("""
			SELECT dc
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE d.id = :deviceId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			ORDER BY dc.readingDate ASC
			""")
	List<DailyConsumption> findTrendByDeviceAndDateRange(@Param("deviceId") Long deviceId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	// =====================================================
	// Analytics Trend - SUPER ADMIN
	// =====================================================

	@Query("""
			SELECT
				dc.readingDate AS readingDate,
				COALESCE(SUM(dc.openingReading), 0) AS openingReading,
				COALESCE(SUM(dc.closingReading), 0) AS closingReading,
				COALESCE(SUM(dc.totalReading), 0) AS totalReading,
				COALESCE(SUM(dc.dailyConsumption), 0) AS consumption
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY dc.readingDate
			ORDER BY dc.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrend(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	// =====================================================
	// Analytics Trend - ADMIN
	// =====================================================

	@Query("""
			SELECT
				dc.readingDate AS readingDate,
				COALESCE(SUM(dc.openingReading), 0) AS openingReading,
				COALESCE(SUM(dc.closingReading), 0) AS closingReading,
				COALESCE(SUM(dc.totalReading), 0) AS totalReading,
				COALESCE(SUM(dc.dailyConsumption), 0) AS consumption
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY dc.readingDate
			ORDER BY dc.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrendByAdmin(@Param("adminId") Long adminId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	// =====================================================
	// Analytics Trend - USER
	// =====================================================

	@Query("""
			SELECT
				dc.readingDate AS readingDate,
				COALESCE(SUM(dc.openingReading), 0) AS openingReading,
				COALESCE(SUM(dc.closingReading), 0) AS closingReading,
				COALESCE(SUM(dc.totalReading), 0) AS totalReading,
				COALESCE(SUM(dc.dailyConsumption), 0) AS consumption
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE d.assignedUser.id = :userId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			GROUP BY dc.readingDate
			ORDER BY dc.readingDate ASC
			""")
	List<PayloadDailyAnalyticsProjection> findAnalyticsTrendByUser(@Param("userId") Long userId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("""
			SELECT dc
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE d.id = :deviceId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			ORDER BY dc.readingDate ASC
			""")
	List<DailyConsumption> findByDeviceAndDateRange(@Param("deviceId") Long deviceId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("""
			SELECT dc
			FROM DailyConsumption dc
			JOIN dc.device d
			JOIN d.meter m
			WHERE d.id = :deviceId
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND dc.readingDate BETWEEN :fromDate AND :toDate
			ORDER BY dc.readingDate ASC
			""")
	List<DailyConsumption> findBillingReadingsByDeviceAndDateRange(@Param("deviceId") Long deviceId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

}