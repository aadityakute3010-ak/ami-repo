package com.ami.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.ArchivedDeviceTelemetry;

@Repository
public interface ArchivedDeviceTelemetryRepository
        extends JpaRepository<ArchivedDeviceTelemetry, Long> {
	
	void deleteByArchivedAtBefore(
	        LocalDateTime dateTime);

}