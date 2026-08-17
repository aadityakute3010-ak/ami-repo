package com.ami.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.Device;
import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ami.enums.TechnologyType;
@Repository
public interface DeviceRepository
extends JpaRepository<Device, Long>,
        JpaSpecificationExecutor<Device> {

    

    boolean existsByMacAddress(String macAddress);

    boolean existsBySerialNumber(String serialNumber);
    
    boolean existsByDeviceId(String deviceId);
    
    boolean existsByDeviceIdAndIdNot(String deviceId, Long id);

    boolean existsByMacAddressAndIdNot(String macAddress, Long id);

    boolean existsBySerialNumberAndIdNot(String serialNumber, Long id);

    List<Device> findByAssignedAdminId(Long adminId);

    List<Device> findByAssignedUserId(Long userId);

   

    List<Device> findBySourceType(SourceType sourceType);

    List<Device> findByStatus(DeviceStatus status);

    List<Device> findByOnline(Boolean online);

    List<Device> findByLocation(String location);

    List<Device> findByZone(String zone);

    List<Device> findByFirmwareVersion(String firmwareVersion);

    List<Device> findByMeterNumber(String meterNumber);

    List<Device> findByDeviceId(String deviceId);

    long countBySourceType(SourceType sourceType);

    long countByOnline(Boolean online);

    long countByStatus(DeviceStatus status);

    long countByLocation(String location);

    long countByZone(String zone);
    
    long countByHealthStatus(DeviceHealthStatus healthStatus);
    
    long countByAssignedAdmin_IdAndHealthStatus(
            Long adminId,
            DeviceHealthStatus healthStatus);

    long countByAssignedUser_IdAndHealthStatus(
            Long userId,
            DeviceHealthStatus healthStatus);
    
    @Query("""
    		SELECT d
    		FROM Device d
    		WHERE
    		    (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
    		AND (:userId IS NULL OR d.assignedUser.id = :userId)
    		AND (
    		    :search IS NULL
    		    OR LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :search, '%'))
    		    OR LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
    		    OR LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :search, '%'))
    		)
    		AND (:status IS NULL OR d.status = :status)
    		AND (:sourceType IS NULL OR d.sourceType = :sourceType)
    		AND (:technologyType IS NULL OR d.technologyType = :technologyType)
    		AND (:zone IS NULL OR d.zone = :zone)
    		AND (:location IS NULL OR d.location = :location)
    		""")
    		Page<Device> findDevicesWithFilters(
    		        @Param("adminId") Long adminId,
    		        @Param("userId") Long userId,
    		        @Param("search") String search,
    		        @Param("status") DeviceStatus status,
    		        @Param("sourceType") SourceType sourceType,
    		        @Param("technologyType") TechnologyType technologyType,
    		        @Param("zone") String zone,
    		        @Param("location") String location,
    		        Pageable pageable);
    
    @Query("""
    		SELECT d
    		FROM Device d
    		WHERE d.assignedUser IS NULL
    		AND d.sourceType IN :sources
    		""")
    		List<Device> findAvailableDevicesForSuperAdmin(
    		        @Param("sources") Set<SourceType> sources);
    
    @Query("""
    		SELECT d
    		FROM Device d
    		WHERE d.assignedUser IS NULL
    		AND d.assignedAdmin.id = :adminId
    		AND d.sourceType IN :sources
    		""")
    		List<Device> findAvailableDevicesForUser(
    		        @Param("adminId") Long adminId,
    		        @Param("sources") Set<SourceType> sources);

    @Query("""
    		SELECT COUNT(d)
    		FROM Device d
    		WHERE d.assignedAdmin.id = :adminId
    		AND d.healthStatus = :healthStatus
    		""")
    		long countByAssignedAdminIdAndHealthStatus(
    		        @Param("adminId") Long adminId,
    		        @Param("healthStatus") DeviceHealthStatus healthStatus);

    		@Query("""
    		SELECT COUNT(d)
    		FROM Device d
    		WHERE d.assignedUser.id = :userId
    		AND d.healthStatus = :healthStatus
    		""")
    		long countByAssignedUserIdAndHealthStatus(
    		        @Param("userId") Long userId,
    		        @Param("healthStatus") DeviceHealthStatus healthStatus);
    		
    		@Query("""
    			       SELECT COUNT(d)
    			       FROM Device d
    			       WHERE d.meter.status = :status
    			       """)
    			long countByMeter_Status(
    			        @Param("status") DeviceStatus status);
    		
    		@Query("""
    			       SELECT COUNT(d)
    			       FROM Device d
    			       WHERE d.assignedAdmin.id = :adminId
    			       AND d.meter.status = :status
    			       """)
    			long countByAssignedAdminIdAndMeter_Status(
    			        @Param("adminId") Long adminId,
    			        @Param("status") DeviceStatus status);
    		
    		@Query("""
    			       SELECT COUNT(d)
    			       FROM Device d
    			       WHERE d.assignedUser.id = :userId
    			       AND d.meter.status = :status
    			       """)
    			long countByAssignedUserIdAndMeter_Status(
    			        @Param("userId") Long userId,
    			        @Param("status") DeviceStatus status);
    		
    		@Query("""
    			       SELECT d
    			       FROM Device d
    			       WHERE d.healthStatus = :healthStatus
    			       ORDER BY d.lastSyncTime ASC
    			       LIMIT 10
    			       """)
    			List<Device> findTop10ByHealthStatusOrderByLastSyncTimeAsc(
    			        @Param("healthStatus") DeviceHealthStatus healthStatus);


    			@Query("""
    			       SELECT d
    			       FROM Device d
    			       WHERE d.assignedAdmin.id = :adminId
    			       AND d.healthStatus = :healthStatus
    			       ORDER BY d.lastSyncTime ASC
    			       LIMIT 10
    			       """)
    			List<Device> findTop10ByAssignedAdminIdAndHealthStatusOrderByLastSyncTimeAsc(
    			        @Param("adminId") Long adminId,
    			        @Param("healthStatus") DeviceHealthStatus healthStatus);


    			@Query("""
    			       SELECT d
    			       FROM Device d
    			       WHERE d.assignedUser.id = :userId
    			       AND d.healthStatus = :healthStatus
    			       ORDER BY d.lastSyncTime ASC
    			       LIMIT 10
    			       """)
    			List<Device> findTop10ByAssignedUserIdAndHealthStatusOrderByLastSyncTimeAsc(
    			        @Param("userId") Long userId,
    			        @Param("healthStatus") DeviceHealthStatus healthStatus);
}
