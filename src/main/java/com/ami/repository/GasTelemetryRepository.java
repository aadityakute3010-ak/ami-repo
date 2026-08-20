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
import com.ami.entity.telemetry.GasTelemetry;

@Repository
public interface GasTelemetryRepository extends JpaRepository<GasTelemetry, Long> {

	Optional<GasTelemetry> findTopByDeviceOrderByReadingTimeDesc(Device device);

	List<GasTelemetry> findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(Device device, LocalDateTime start,
			LocalDateTime end);

	Optional<GasTelemetry> findByPayload(Payload payload);

	@Query("""
			SELECT gt
			FROM GasTelemetry gt
			JOIN FETCH gt.payload p
			WHERE p.id IN :payloadIds
			""")
	List<GasTelemetry> findByPayloadIds(@Param("payloadIds") List<Long> payloadIds);

}
