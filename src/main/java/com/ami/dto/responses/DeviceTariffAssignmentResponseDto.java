package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.BillingType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceTariffAssignmentResponseDto {

	private Long assignmentId;

	private Long deviceId;

	private String deviceNumber;

	private String deviceName;

	private String meterName;

	private SourceType sourceType;

	private BillingType billingType;

	private Long tariffId;

	private String tariffName;

	private TariffCategory tariffCategory;

	private String unit;

	private java.math.BigDecimal baseRate;

	private java.math.BigDecimal fixedCharge;

	private java.math.BigDecimal tax;

	private TariffStatus tariffStatus;

	private Boolean active;

	private Long assignedById;

	private String assignedByName;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}