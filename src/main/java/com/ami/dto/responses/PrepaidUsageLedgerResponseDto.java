package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PrepaidLedgerType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidUsageLedgerResponseDto {

	private Long id;

	private Long deviceId;

	private String deviceIdentifier;

	private PrepaidLedgerType ledgerType;

	private BigDecimal units;

	private BigDecimal readingBefore;

	private BigDecimal readingAfter;

	private BigDecimal balanceBefore;

	private BigDecimal balanceAfter;

	private String description;

	private LocalDateTime createdAt;
}