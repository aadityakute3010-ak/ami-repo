package com.ami.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.DailyConsumption;
import com.ami.entity.Device;

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

}