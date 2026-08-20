package com.ami.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.Device;
import com.ami.entity.DeviceLocation;

public interface DeviceLocationRepository extends JpaRepository<DeviceLocation, Long> {

	Optional<DeviceLocation> findByDevice(Device device);

	void deleteByDevice(Device device);
	void deleteByDeviceId(Long deviceId);

	@Query("""
			SELECT dl
			FROM DeviceLocation dl
			JOIN dl.device d
			JOIN d.meter m
			WHERE dl.latitude IS NOT NULL
			AND dl.longitude IS NOT NULL
			AND m.status <> com.ami.enums.DeviceStatus.INACTIVE
			AND (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			""")
	List<DeviceLocation> findDeviceMapMarkers(@Param("adminId") Long adminId, @Param("userId") Long userId);

}