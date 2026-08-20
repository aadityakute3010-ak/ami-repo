package com.ami.dto.responses;

import java.math.BigDecimal;
import java.util.List;

import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillCalculationResponseDto {

	private Long tariffId;

	private String tariffName;

	private SourceType source;

	private TariffCategory category;

	private String unit;

	private BigDecimal previousReading;

	private BigDecimal currentReading;

	private BigDecimal totalConsumption;

	private BigDecimal baseRate;

	private BigDecimal consumptionAmount;

	private BigDecimal fixedCharge;

	private BigDecimal taxableAmount;

	private BigDecimal taxPercentage;

	private BigDecimal taxAmount;

	private BigDecimal previousDues;

	private BigDecimal totalAmount;

	private boolean slabBased;

	private List<SlabCalculationResponseDto> slabBreakdown;
}