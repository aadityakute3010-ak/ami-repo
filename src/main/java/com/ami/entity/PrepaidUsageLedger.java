package com.ami.entity;

import java.math.BigDecimal;

import com.ami.enums.PrepaidLedgerType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prepaid_usage_ledger", indexes = { @Index(name = "idx_prepaid_ledger_device", columnList = "device_id"),
		@Index(name = "idx_prepaid_ledger_balance", columnList = "prepaid_balance_id"),
		@Index(name = "idx_prepaid_ledger_time", columnList = "created_at") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PrepaidUsageLedger extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prepaid_balance_id", nullable = false)
	private PrepaidBalance prepaidBalance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@Enumerated(EnumType.STRING)
	@Column(name = "ledger_type", nullable = false, length = 30)
	private PrepaidLedgerType ledgerType;

	@Column(name = "units", nullable = false, precision = 12, scale = 3)
	private BigDecimal units;

	@Column(name = "reading_before", precision = 12, scale = 3)
	private BigDecimal readingBefore;

	@Column(name = "reading_after", precision = 12, scale = 3)
	private BigDecimal readingAfter;

	@Column(name = "balance_before", nullable = false, precision = 12, scale = 3)
	private BigDecimal balanceBefore;

	@Column(name = "balance_after", nullable = false, precision = 12, scale = 3)
	private BigDecimal balanceAfter;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "payment_reference", length = 100)
	private String paymentReference;
}