package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ami.entity.Device;
import com.ami.entity.Payload;
import com.ami.entity.telemetry.WaterTelemetry;

@Repository
public interface WaterTelemetryRepository extends JpaRepository<WaterTelemetry, Long> {

	Optional<WaterTelemetry> findTopByDeviceOrderByReadingTimeDesc(Device device);

	List<WaterTelemetry> findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(Device device, LocalDateTime start,
			LocalDateTime end);

	Optional<WaterTelemetry> findByPayload(Payload payload);

	@Query("""
			SELECT wt
			FROM WaterTelemetry wt
			JOIN FETCH wt.payload p
			WHERE p.id IN :payloadIds
			""")
	List<WaterTelemetry> findByPayloadIds(@Param("payloadIds") List<Long> payloadIds);
}
