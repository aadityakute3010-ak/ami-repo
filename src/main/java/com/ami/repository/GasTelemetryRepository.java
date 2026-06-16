package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Device;
import com.ami.entity.telemetry.GasTelemetry;

@Repository
public interface GasTelemetryRepository extends JpaRepository<GasTelemetry, Long> {

	Optional<GasTelemetry> findTopByDeviceOrderByReadingTimeDesc(Device device);

	List<GasTelemetry> findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(Device device, LocalDateTime start,
			LocalDateTime end);

}
