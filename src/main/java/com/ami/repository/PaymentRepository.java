package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.Invoice;
import com.ami.entity.Payment;
import com.ami.enums.PaymentTransactionStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByTransactionId(String transactionId);

	boolean existsByTransactionId(String transactionId);

	List<Payment> findByInvoiceOrderByPaymentDateDesc(Invoice invoice);

	@Query("""
			SELECT p
			FROM Payment p
			WHERE (:status IS NULL OR p.status = :status)
			AND (:search IS NULL OR
			    LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(p.invoice.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(p.customerName) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:fromDate IS NULL OR p.paymentDate >= :fromDate)
			AND (:toDate IS NULL OR p.paymentDate <= :toDate)
			ORDER BY p.paymentDate DESC
			""")
	Page<Payment> findPaymentsWithFilters(@Param("search") String search,
			@Param("status") PaymentTransactionStatus status, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, Pageable pageable);
	
	boolean existsByRazorpayPaymentId(String razorpayPaymentId);
}