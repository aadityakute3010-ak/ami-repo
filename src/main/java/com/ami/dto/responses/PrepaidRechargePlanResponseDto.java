package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidRechargePlanResponseDto {

	private Long id;

	private String planName;

	private BigDecimal amount;

	private SourceType sourceType;

	private PrepaidPlanStatus status;

	private String description;

	private Long createdById;

	private String createdByName;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}