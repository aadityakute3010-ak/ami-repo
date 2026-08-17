package com.ami.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.ArchivedDevice;

@Repository
public interface ArchivedDeviceRepository
        extends JpaRepository<ArchivedDevice, Long> {
	
	void deleteByArchivedAtBefore(
	        LocalDateTime dateTime);

}