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
import com.ami.entity.telemetry.EnergyTelemetry;

@Repository
public interface EnergyTelemetryRepository extends JpaRepository<EnergyTelemetry, Long> {

	Optional<EnergyTelemetry> findTopByDeviceOrderByReadingTimeDesc(Device device);

	List<EnergyTelemetry> findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(Device device, LocalDateTime start,
			LocalDateTime end);

	Optional<EnergyTelemetry> findByPayload(Payload payload);

	@Query("""
			SELECT et
			FROM EnergyTelemetry et
			JOIN FETCH et.payload p
			WHERE p.id IN :payloadIds
			""")
	List<EnergyTelemetry> findByPayloadIds(@Param("payloadIds") List<Long> payloadIds);

}
