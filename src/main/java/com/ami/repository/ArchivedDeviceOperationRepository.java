package com.ami.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.ArchivedDeviceOperation;

@Repository
public interface ArchivedDeviceOperationRepository
        extends JpaRepository<ArchivedDeviceOperation, Long> {
	
	void deleteByArchivedAtBefore(
	        LocalDateTime dateTime);

}