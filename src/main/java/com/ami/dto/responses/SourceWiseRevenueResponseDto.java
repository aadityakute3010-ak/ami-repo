package com.ami.dto.responses;

import java.math.BigDecimal;

import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SourceWiseRevenueResponseDto {

	private SourceType source;

	private long invoices;

	private BigDecimal revenue;

	private BigDecimal collected;

	private BigDecimal pending;

	private BigDecimal overdue;

	private BigDecimal collectionPercentage;
}