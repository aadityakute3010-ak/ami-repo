package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ami.entity.Payload;
import com.ami.enums.PayloadStatus;

public interface PayloadRepository extends JpaRepository<Payload, Long> {

	// Dashboard Statistics
	long countByStatus(PayloadStatus status);

	// Online Devices
	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			WHERE p.device.active = true
			AND p.receivedAt >= :since
			""")
	long countOnlineDevices(@Param("since") LocalDateTime since);

	// Payload Listing with Filters
	@Query("""
			SELECT p
			FROM Payload p
			JOIN p.device d
			WHERE d.active = true

			AND (:deviceId IS NULL OR d.id = :deviceId)

			AND (
			    :consumer IS NULL
			    OR LOWER(p.consumerNumber)
			       LIKE LOWER(CONCAT('%', :consumer, '%'))
			)

			AND (:status IS NULL OR p.status = :status)

			AND (:from IS NULL OR p.receivedAt >= :from)

			AND (:to IS NULL OR p.receivedAt <= :to)

			AND (
			    :search IS NULL

			    OR LOWER(d.deviceName)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.serialNumber)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.macAddress)
			       LIKE LOWER(CONCAT('%', :search, '%'))
			)

			ORDER BY p.receivedAt DESC
			""")
	Page<Payload> findWithFiltersForSuperAdmin(@Param("deviceId") Long deviceId, @Param("consumer") String consumer,
			@Param("status") PayloadStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
			@Param("search") String search, Pageable pageable);

	@Query("""
			SELECT p
			FROM Payload p
			JOIN p.device d
			WHERE d.active = true
			AND d.assignedAdmin.id = :adminId

			AND (:deviceId IS NULL OR d.id = :deviceId)

			AND (
			    :consumer IS NULL
			    OR LOWER(p.consumerNumber)
			       LIKE LOWER(CONCAT('%', :consumer, '%'))
			)

			AND (:status IS NULL OR p.status = :status)

			AND (:from IS NULL OR p.receivedAt >= :from)

			AND (:to IS NULL OR p.receivedAt <= :to)

			AND (
			    :search IS NULL

			    OR LOWER(d.deviceName)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.serialNumber)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.macAddress)
			       LIKE LOWER(CONCAT('%', :search, '%'))
			)

			ORDER BY p.receivedAt DESC
			""")
	Page<Payload> findWithFiltersForAdmin(@Param("adminId") Long adminId, @Param("deviceId") Long deviceId,
			@Param("consumer") String consumer, @Param("status") PayloadStatus status,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("search") String search,
			Pageable pageable);

	@Query("""
			SELECT p
			FROM Payload p
			JOIN p.device d
			WHERE d.active = true
			AND d.assignedUser.id = :userId

			AND (:deviceId IS NULL OR d.id = :deviceId)

			AND (
			    :consumer IS NULL
			    OR LOWER(p.consumerNumber)
			       LIKE LOWER(CONCAT('%', :consumer, '%'))
			)

			AND (:status IS NULL OR p.status = :status)

			AND (:from IS NULL OR p.receivedAt >= :from)

			AND (:to IS NULL OR p.receivedAt <= :to)

			AND (
			    :search IS NULL

			    OR LOWER(d.deviceName)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.serialNumber)
			       LIKE LOWER(CONCAT('%', :search, '%'))

			    OR LOWER(d.macAddress)
			       LIKE LOWER(CONCAT('%', :search, '%'))
			)

			ORDER BY p.receivedAt DESC
			""")
	Page<Payload> findWithFiltersForUser(@Param("userId") Long userId, @Param("deviceId") Long deviceId,
			@Param("consumer") String consumer, @Param("status") PayloadStatus status,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("search") String search,
			Pageable pageable);

	// Consumption Trend
	@Query("""
			    SELECT p
			    FROM Payload p
			    WHERE p.device.id = :deviceId
			    AND p.receivedAt BETWEEN :from AND :to
			    AND p.status = com.ami.enums.PayloadStatus.SUCCESS
			    ORDER BY p.receivedAt ASC
			""")
	List<Payload> findForConsumptionTrend(@Param("deviceId") Long deviceId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	// 24 Hour Readings
	@Query("""
			    SELECT p
			    FROM Payload p
			    WHERE p.device.id = :deviceId
			    AND p.receivedAt BETWEEN :from AND :to
			    AND p.status = com.ami.enums.PayloadStatus.SUCCESS
			    ORDER BY p.receivedAt ASC
			""")
	List<Payload> find24HourReadings(@Param("deviceId") Long deviceId, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedAdmin.id = :adminId
			""")
	long countByAssignedAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedAdmin.id = :adminId
			AND p.status = :status
			""")
	long countByAssignedAdminAndStatus(@Param("adminId") Long adminId, @Param("status") PayloadStatus status);

	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedAdmin.id = :adminId
			AND p.receivedAt >= :since
			""")
	long countOnlineDevicesByAdmin(@Param("adminId") Long adminId, @Param("since") LocalDateTime since);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedUser.id = :userId
			""")
	long countByAssignedUser(@Param("userId") Long userId);

	@Query("""
			SELECT COUNT(p)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedUser.id = :userId
			AND p.status = :status
			""")
	long countByAssignedUserAndStatus(@Param("userId") Long userId, @Param("status") PayloadStatus status);

	@Query("""
			SELECT COUNT(DISTINCT p.device.id)
			FROM Payload p
			WHERE p.device.active = true
			AND p.device.assignedUser.id = :userId
			AND p.receivedAt >= :since
			""")
	long countOnlineDevicesByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

}