package com.ami.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ami.entity.Device;
import com.ami.entity.Invoice;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import java.math.BigDecimal;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	boolean existsByDeviceAndBillingPeriodFromAndBillingPeriodTo(Device device, LocalDate billingPeriodFrom,
			LocalDate billingPeriodTo);

	Optional<Invoice> findByDeviceAndBillingPeriodFromAndBillingPeriodTo(Device device, LocalDate billingPeriodFrom,
			LocalDate billingPeriodTo);

	@Query("""
			SELECT i
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND (:status IS NULL OR i.status = :status)
			AND (:paymentStatus IS NULL OR i.paymentStatus = :paymentStatus)
			AND (:source IS NULL OR i.source = :source)
			AND (:billingType IS NULL OR i.billingType = :billingType)
			AND (:fromDateTime IS NULL OR i.createdAt >= :fromDateTime)
			AND (:toDateTime IS NULL OR i.createdAt <= :toDateTime)
			AND (
			    :search IS NULL
			    OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(i.customerName) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(i.email) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(i.phone) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(i.meterNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			ORDER BY i.createdAt DESC
			""")
	Page<Invoice> findInvoicesWithFilters(@Param("adminId") Long adminId, @Param("userId") Long userId,
			@Param("search") String search, @Param("status") InvoiceStatus status,
			@Param("paymentStatus") PaymentStatus paymentStatus, @Param("source") SourceType source,
			@Param("billingType") BillingType billingType, @Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime, Pageable pageable);

	@Query("""
			SELECT COALESCE(SUM(i.netAmount), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.status <> com.ami.enums.InvoiceStatus.FAILED
			""")
	BigDecimal getTotalRevenue(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(SUM(i.paidAmount), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.paymentStatus = com.ami.enums.PaymentStatus.PAID
			""")
	BigDecimal getCollectedRevenue(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(SUM(i.balanceAmount), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.status = com.ami.enums.InvoiceStatus.PENDING
			AND i.paymentStatus <> com.ami.enums.PaymentStatus.PAID
			""")
	BigDecimal getPendingRevenue(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(SUM(i.balanceAmount), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.status = com.ami.enums.InvoiceStatus.OVERDUE
			AND i.paymentStatus <> com.ami.enums.PaymentStatus.PAID
			""")
	BigDecimal getOverdueRevenue(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT COUNT(i)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			""")
	long countInvoices(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT COUNT(i)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.status = :status
			""")
	long countByStatusForDashboard(@Param("adminId") Long adminId, @Param("userId") Long userId,
			@Param("status") InvoiceStatus status);

	@Query("""
			SELECT MONTH(i.invoiceDate),
			       COALESCE(SUM(i.netAmount), 0),
			       COALESCE(SUM(i.paidAmount), 0),
			       COALESCE(SUM(CASE WHEN i.status = com.ami.enums.InvoiceStatus.PENDING THEN i.balanceAmount ELSE 0 END), 0),
			       COALESCE(SUM(CASE WHEN i.status = com.ami.enums.InvoiceStatus.OVERDUE THEN i.balanceAmount ELSE 0 END), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND YEAR(i.invoiceDate) = :year
			AND i.status <> com.ami.enums.InvoiceStatus.FAILED
			GROUP BY MONTH(i.invoiceDate)
			ORDER BY MONTH(i.invoiceDate)
			""")
	List<Object[]> getRevenueTrend(@Param("adminId") Long adminId, @Param("userId") Long userId,
			@Param("year") int year);

	@Query("""
			SELECT i.status, COUNT(i)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			GROUP BY i.status
			""")
	List<Object[]> getInvoiceStatusSummary(@Param("adminId") Long adminId, @Param("userId") Long userId);

	@Query("""
			SELECT i.source,
			       COUNT(i),
			       COALESCE(SUM(i.netAmount), 0),
			       COALESCE(SUM(i.paidAmount), 0),
			       COALESCE(SUM(CASE WHEN i.status = com.ami.enums.InvoiceStatus.PENDING THEN i.balanceAmount ELSE 0 END), 0),
			       COALESCE(SUM(CASE WHEN i.status = com.ami.enums.InvoiceStatus.OVERDUE THEN i.balanceAmount ELSE 0 END), 0)
			FROM Invoice i
			JOIN i.device d
			WHERE (:adminId IS NULL OR d.assignedAdmin.id = :adminId)
			AND (:userId IS NULL OR d.assignedUser.id = :userId)
			AND i.status <> com.ami.enums.InvoiceStatus.FAILED
			GROUP BY i.source
			""")
	List<Object[]> getSourceWiseRevenue(@Param("adminId") Long adminId, @Param("userId") Long userId);

	long countByInvoiceDate(LocalDate invoiceDate);

	List<Invoice> findByDueDateBeforeAndPaymentStatusNotAndBalanceAmountGreaterThanAndStatusNotIn(LocalDate today,
			PaymentStatus paymentStatus, BigDecimal balanceAmount, List<InvoiceStatus> excludedStatuses);

	boolean existsByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatusNot(Device device, LocalDate billingPeriodFrom,
			LocalDate billingPeriodTo, InvoiceStatus status);

	@Transactional 
	void deleteByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatus(Device device, LocalDate billingPeriodFrom,
			LocalDate billingPeriodTo, InvoiceStatus status);

	@Query("""
			SELECT COALESCE(SUM(i.balanceAmount), 0)
			FROM Invoice i
			WHERE i.device = :device
			AND i.billingPeriodTo < :billingPeriodFrom
			AND i.paymentStatus <> com.ami.enums.PaymentStatus.PAID
			AND i.status IN (
			    com.ami.enums.InvoiceStatus.PENDING,
			    com.ami.enums.InvoiceStatus.OVERDUE
			)
			""")
	BigDecimal calculatePreviousDues(@Param("device") Device device,
			@Param("billingPeriodFrom") LocalDate billingPeriodFrom);

}