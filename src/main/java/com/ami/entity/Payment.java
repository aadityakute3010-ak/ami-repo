package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import com.ami.enums.PaymentTransactionStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", indexes = { @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
		@Index(name = "idx_payment_transaction_id", columnList = "transaction_id"),
		@Index(name = "idx_payment_status", columnList = "status"),
		@Index(name = "idx_payment_date", columnList = "payment_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "transaction_id", nullable = false, unique = true, length = 80)
	private String transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id", nullable = false)
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private User customer;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "method", nullable = false, length = 30)
	private PaymentMethod method;

	@Enumerated(EnumType.STRING)
	@Column(name = "gateway", length = 30)
	private PaymentGateway gateway;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private PaymentTransactionStatus status;

	@Column(name = "reference_number", length = 120)
	private String referenceNumber;

	@Column(name = "remarks", length = 500)
	private String remarks;

	@Column(name = "payment_date", nullable = false)
	private LocalDateTime paymentDate;

	@Column(name = "razorpay_order_id", length = 120)
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id", length = 120)
	private String razorpayPaymentId;

	@Column(name = "razorpay_signature", length = 255)
	private String razorpaySignature;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", length = 30)
	private SourceType source;

	@Column(name = "billing_period", length = 50)
	private String billingPeriod;

	@Column(name = "due_date")
	private java.time.LocalDate dueDate;

	@Column(name = "meter_number")
	private String meterNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;
}