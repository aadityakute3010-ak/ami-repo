package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import com.ami.enums.RechargeStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prepaid_recharges", indexes = { @Index(name = "idx_prepaid_recharge_device", columnList = "device_id"),
		@Index(name = "idx_prepaid_recharge_user", columnList = "user_id"),
		@Index(name = "idx_prepaid_recharge_transaction", columnList = "transaction_id"),
		@Index(name = "idx_prepaid_recharge_date", columnList = "recharge_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PrepaidRecharge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "recharge_number", nullable = false, unique = true, length = 80)
	private String rechargeNumber;

	@Column(name = "transaction_id", nullable = false, unique = true, length = 100)
	private String transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "customer_email")
	private String customerEmail;

	@Column(name = "customer_phone")
	private String customerPhone;

	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name = "tax_amount", precision = 12, scale = 2)
	private BigDecimal taxAmount;

	@Column(name = "net_recharge_amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal netRechargeAmount;

	@Column(name = "credited_units", nullable = false, precision = 12, scale = 3)
	private BigDecimal creditedUnits;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_type", nullable = false, length = 30)
	private SourceType sourceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_id", nullable = false)
	private Tariff tariff;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 30)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_gateway", length = 30)
	private PaymentGateway paymentGateway;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private RechargeStatus status;

	@Column(name = "reference_number", length = 120)
	private String referenceNumber;

	@Column(name = "remarks", length = 500)
	private String remarks;

	@Column(name = "recharge_date", nullable = false)
	private LocalDateTime rechargeDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = false)
	private PrepaidRechargePlan plan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;
	
	@Column(name = "razorpay_order_id", unique = true, length = 120)
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id", unique = true, length = 120)
	private String razorpayPaymentId;

	@Column(name = "razorpay_signature", length = 255)
	private String razorpaySignature;
	
}