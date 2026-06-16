package com.ami.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ami.entity.Device;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

	boolean existsByMacAddress(String macAddress);

	boolean existsBySerialNumber(String serialNumber);

	Page<Device> findAll(Pageable pageable);

	Page<Device> findByAssignedAdminId(Long adminId, Pageable pageable);

	Page<Device> findByAssignedUserId(Long userId, Pageable pageable);

	Optional<Device> findById(Long id);

	@Query("""
			SELECT d
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedAdmin.id = :adminId
			AND d.assignedUser IS NULL
			AND m.sourceType IN :sources
			""")
	List<Device> findAvailableDevicesForUser(Long adminId, Set<SourceType> sources);

	@Query("""
			SELECT d
			FROM Device d
			JOIN d.meter m
			WHERE d.assignedUser IS NULL
			AND m.sourceType IN :sources
			""")
	List<Device> findAvailableDevicesForSuperAdmin(Set<SourceType> sources);

	@Query("""
			SELECT d
			FROM Device d
			WHERE d.active = true
			AND (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND (
			     :search IS NULL
			     OR LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :search, '%'))
			     OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
			     OR LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :search, '%'))
			     OR LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			     OR LOWER(d.meter.meterName) LIKE LOWER(CONCAT('%', :search, '%'))
			     OR LOWER(d.customerName) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:status IS NULL OR d.meter.status = :status)
			AND (:sourceType IS NULL OR d.meter.sourceType = :sourceType)
			AND (:technologyType IS NULL OR d.meter.technologyType = :technologyType)""")
	Page<Device> findDevicesWithFilters(Long adminId, Long userId, String search, DeviceStatus status,
			SourceType sourceType, TechnologyType technologyType, Pageable pageable);

	List<Device> findByAssignedAdminId(Long adminId);

	List<Device> findByAssignedUserId(Long userId);

	boolean existsByDeviceId(String deviceId);

	Page<Device> findByActiveFalse(Pageable pageable);

	Page<Device> findByAssignedAdminIdAndActiveFalse(Long adminId, Pageable pageable);

	Optional<Device> findByDeviceId(String deviceId);

}