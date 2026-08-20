package com.ami.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;

import jakarta.persistence.LockModeType;

public interface PrepaidBalanceRepository extends JpaRepository<PrepaidBalance, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM PrepaidBalance p WHERE p.device = :device")
	Optional<PrepaidBalance> findByDeviceForUpdate(@Param("device") Device device);

	boolean existsByDevice(Device device); 
	
	Optional<PrepaidBalance> findByDevice(Device device);
}