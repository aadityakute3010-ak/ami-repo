package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlabCalculationResponseDto {

	private Long slabId;

	private BigDecimal from;

	private BigDecimal to;

	private BigDecimal consumedUnits;

	private BigDecimal rate;

	private BigDecimal amount;
}