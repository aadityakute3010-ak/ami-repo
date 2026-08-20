package com.ami.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.DeviceTariffAssignment;

public interface DeviceTariffAssignmentRepository extends JpaRepository<DeviceTariffAssignment, Long> {

	Optional<DeviceTariffAssignment> findByDeviceId(Long deviceId);

	Optional<DeviceTariffAssignment> findByDeviceIdAndActiveTrue(Long deviceId);

	boolean existsByDeviceId(Long deviceId);
}