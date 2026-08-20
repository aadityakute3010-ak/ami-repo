package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "billing_settings", uniqueConstraints = {
		@UniqueConstraint(name = "uk_billing_settings_admin", columnNames = "admin_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Null admin means global/default settings
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private User admin;
	
	@Column(name = "prepaid_minimum_recharge_amount", precision = 12, scale = 2)
	@Builder.Default
	private BigDecimal prepaidMinimumRechargeAmount = BigDecimal.valueOf(500);

	// Invoice Settings
	@Column(name = "invoice_prefix", nullable = false)
	private String invoicePrefix;

	@Column(name = "invoice_due_days", nullable = false)
	private Integer invoiceDueDays;
	
	@Column(name = "currency", nullable = false)
	private String currency;

	// Tax Settings
	@Column(name = "tax_enabled", nullable = false)
	private Boolean taxEnabled;

	@Column(name = "default_tax_percentage", precision = 10, scale = 2)
	private BigDecimal defaultTaxPercentage;

	// Penalty Settings
	@Column(name = "penalty_enabled", nullable = false)
	private Boolean penaltyEnabled;

	@Column(name = "penalty_percentage", precision = 10, scale = 2)
	private BigDecimal penaltyPercentage;

	@Column(name = "grace_period_days", nullable = false)
	private Integer gracePeriodDays;

	// Reminder Settings
	@Column(name = "reminder_enabled", nullable = false)
	private Boolean reminderEnabled;

	@Column(name = "reminder_before_due_days", nullable = false)
	private Integer reminderBeforeDueDays;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}