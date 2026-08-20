package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.SourceType;
import com.ami.enums.TariffStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TariffSlabResponseDto {

	private Long id;

	private Long tariffId;

	private SourceType source;

	private String unit;

	private BigDecimal from;

	private BigDecimal to;

	private BigDecimal rate;

	private BigDecimal fixedCharge;

	private BigDecimal tax;

	private TariffStatus status;

	private String description;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}