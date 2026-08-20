package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PrepaidBalanceStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prepaid_balances", indexes = { @Index(name = "idx_prepaid_balance_device", columnList = "device_id"),
		@Index(name = "idx_prepaid_balance_user", columnList = "user_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PrepaidBalance extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false, unique = true)
	private Device device;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "total_recharged_amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalRechargedAmount;

	@Column(name = "total_credited_units", nullable = false, precision = 12, scale = 3)
	private BigDecimal totalCreditedUnits;

	@Column(name = "total_used_units", nullable = false, precision = 12, scale = 3)
	private BigDecimal totalUsedUnits;

	@Column(name = "available_units", nullable = false, precision = 12, scale = 3)
	private BigDecimal availableUnits;

	@Column(name = "last_meter_reading", precision = 12, scale = 3)
	private BigDecimal lastMeterReading;
	
	@Builder.Default
	@Column(nullable = false)
	private boolean consumptionBlocked = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private PrepaidBalanceStatus status;

	@Column(name = "last_recharge_at")
	private LocalDateTime lastRechargeAt;

	@Column(name = "last_consumption_at")
	private LocalDateTime lastConsumptionAt;
}