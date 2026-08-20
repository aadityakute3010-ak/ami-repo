package com.ami.dto.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingReadingResponseDto {

	private Long deviceId;

	private String deviceNumber;

	private String meterNumber;

	private SourceType sourceType;

	private LocalDate billingPeriodFrom;

	private LocalDate billingPeriodTo;

	private Double openingReading;

	private Double closingReading;

	private Double consumption;

	private LocalDateTime openingReadingTime;

	private LocalDateTime closingReadingTime;

	private Long payloadCount;
}