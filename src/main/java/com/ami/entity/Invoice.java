package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceGenerationType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoices", uniqueConstraints = {
		@UniqueConstraint(name = "uk_invoice_device_billing_period", columnNames = { "device_id", "billing_period_from",
				"billing_period_to" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "invoice_number", nullable = false, unique = true)
	private String invoiceNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_id")
	private Tariff tariff;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private User customer;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "meter_number")
	private String meterNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "source")
	private SourceType source;

	@Enumerated(EnumType.STRING)
	@Column(name = "billing_type")
	private BillingType billingType;

	@Column(name = "previous_reading", precision = 15, scale = 2)
	private BigDecimal previousReading;

	@Column(name = "current_reading", precision = 15, scale = 2)
	private BigDecimal currentReading;

	@Column(name = "consumption", precision = 15, scale = 2)
	private BigDecimal consumption;

	@Column(name = "amount", precision = 15, scale = 2)
	private BigDecimal amount;
	
	@Column(name = "previous_dues", precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal previousDues = BigDecimal.ZERO;

	@Column(name = "fixed_charge", precision = 15, scale = 2)
	private BigDecimal fixedCharge;

	@Column(name = "tax", precision = 15, scale = 2)
	private BigDecimal tax;

	@Column(name = "discount", precision = 15, scale = 2)
	private BigDecimal discount;

	@Column(name = "net_amount", precision = 15, scale = 2)
	private BigDecimal netAmount;

	@Column(name = "paid_amount", precision = 15, scale = 2)
	private BigDecimal paidAmount;

	@Column(name = "balance_amount", precision = 15, scale = 2)
	private BigDecimal balanceAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private InvoiceStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status")
	private PaymentStatus paymentStatus;

	@Column(name = "invoice_date")
	private LocalDate invoiceDate;

	@Column(name = "due_date")
	private LocalDate dueDate;
	
	@Column(name = "penalty_amount", precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal penaltyAmount = BigDecimal.ZERO;

	@Column(name = "penalty_applied", nullable = false)
	@Builder.Default
	private Boolean penaltyApplied = false;

	@Column(name = "billing_period_from")
	private LocalDate billingPeriodFrom;

	@Column(name = "billing_period_to")
	private LocalDate billingPeriodTo;

	@Column(name = "remarks", length = 1000)
	private String remarks;

	@Enumerated(EnumType.STRING)
	@Column(name = "generation_type")
	private InvoiceGenerationType generationType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "generated_by")
	private User generatedBy;

	@Column(name = "failure_reason", length = 1000)
	private String failureReason;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	// Billing settings snapshot at invoice generation time

	@Column(name = "invoice_due_days_snapshot")
	private Integer invoiceDueDaysSnapshot;

	@Column(name = "grace_period_days_snapshot")
	private Integer gracePeriodDaysSnapshot;

	@Column(name = "penalty_enabled_snapshot", nullable = false)
	@Builder.Default
	private Boolean penaltyEnabledSnapshot = false;

	@Column(name = "penalty_percentage_snapshot", precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal penaltyPercentageSnapshot = BigDecimal.ZERO;
}