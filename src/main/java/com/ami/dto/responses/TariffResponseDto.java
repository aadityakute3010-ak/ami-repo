package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TariffResponseDto {

	private Long id;

	private String name;

	private SourceType source;

	private TariffCategory category;

	private String unit;

	private BigDecimal rate;

	private BigDecimal fixedCharge;

	private BigDecimal tax;

	private TariffStatus status;

	private List<TariffSlabResponseDto> slabs;

	private Long createdById;

	private String createdBy;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String description;

	private Long version;
}