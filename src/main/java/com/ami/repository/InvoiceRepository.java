package com.ami.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import com.ami.entity.Invoice;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {
	
	List<Invoice> findByCustomerNameContainingIgnoreCase(
	        String customerName);

	List<Invoice> findByStatus(
	        InvoiceStatus status);

	List<Invoice> findByPaymentStatus(
	        PaymentStatus paymentStatus);

	List<Invoice> findBySource(
	        SourceType source);

	List<Invoice> findByBillingType(
	        BillingType billingType);

}