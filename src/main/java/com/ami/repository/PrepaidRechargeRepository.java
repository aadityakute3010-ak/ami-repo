package com.ami.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;
import com.ami.entity.PrepaidRecharge;
import com.ami.enums.RechargeStatus;

public interface PrepaidRechargeRepository extends JpaRepository<PrepaidRecharge, Long> {

	boolean existsByTransactionId(String transactionId);

	boolean existsByReferenceNumber(String referenceNumber);

	Optional<PrepaidRecharge> findByRechargeNumber(String rechargeNumber);

	@Query("""
			SELECT r
			FROM PrepaidRecharge r
			WHERE (:status IS NULL OR r.status = :status)
			AND (:search IS NULL OR
			    LOWER(r.rechargeNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(r.transactionId) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(r.customerName) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(r.device.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(r.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:fromDate IS NULL OR r.rechargeDate >= :fromDate)
			AND (:toDate IS NULL OR r.rechargeDate <= :toDate)
			ORDER BY r.rechargeDate DESC
			""")
	Page<PrepaidRecharge> findRechargesWithFilters(@Param("search") String search,
			@Param("status") RechargeStatus status, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, Pageable pageable);

	Optional<PrepaidRecharge> findByRazorpayOrderId(String razorpayOrderId);

	boolean existsByRazorpayPaymentId(String razorpayPaymentId);

	@Query("""
			    SELECT COALESCE(SUM(r.amount), 0)
			    FROM PrepaidRecharge r
			    WHERE r.status = :status
			      AND r.rechargeDate BETWEEN :from AND :to
			""")
	BigDecimal findTotalRechargeBetween(@Param("status") RechargeStatus status, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	@Query("""
			    SELECT COALESCE(SUM(r.amount), 0)
			    FROM PrepaidRecharge r
			    WHERE r.status = :status
			      AND r.device.assignedAdmin.id = :adminId
			      AND r.rechargeDate BETWEEN :from AND :to
			""")
	BigDecimal findTotalRechargeByAdminBetween(@Param("adminId") Long adminId, @Param("status") RechargeStatus status,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("""
			    SELECT COALESCE(SUM(r.amount), 0)
			    FROM PrepaidRecharge r
			    WHERE r.status = :status
			      AND r.device.assignedUser.id = :userId
			      AND r.rechargeDate BETWEEN :from AND :to
			""")
	BigDecimal findTotalRechargeByUserBetween(@Param("userId") Long userId, @Param("status") RechargeStatus status,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	List<PrepaidRecharge> findByDeviceAndStatusOrderByRechargeDateAsc(Device device, RechargeStatus status);

	Optional<PrepaidRecharge> findTopByDeviceAndStatusAndRechargeDateLessThanEqualOrderByRechargeDateDesc(Device device,
			RechargeStatus status, LocalDateTime rechargeDate);

	Page<PrepaidRecharge> findByDeviceAndRechargeDateBetweenOrderByRechargeDateDesc(Device device,
			LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

	@Query("""
			SELECT r
			FROM PrepaidRecharge r
			WHERE r.device = :device
			  AND (:status IS NULL OR r.status = :status)
			  AND (:fromDate IS NULL OR r.rechargeDate >= :fromDate)
			  AND (:toDate IS NULL OR r.rechargeDate <= :toDate)
			  AND (
			      :search IS NULL OR
			      LOWER(r.rechargeNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			      OR LOWER(r.transactionId) LIKE LOWER(CONCAT('%', :search, '%'))
			      OR LOWER(r.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			  )
			ORDER BY r.rechargeDate DESC
			""")
	Page<PrepaidRecharge> findDeviceRechargesWithFilters(@Param("device") Device device, @Param("search") String search,
			@Param("status") RechargeStatus status, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, Pageable pageable);

	Optional<PrepaidBalance> findByDevice(Device device);
}