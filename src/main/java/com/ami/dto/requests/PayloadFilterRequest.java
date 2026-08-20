package com.ami.dto.requests;

import java.time.LocalDate;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import com.ami.enums.PayloadStatus;
import com.ami.enums.SourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayloadFilterRequest {

	private String deviceId;

	private String consumer;

	private String meterNumber;

	private String macAddress;

	private PayloadStatus status;

	private SourceType sourceType;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;

	private Integer minBattery;

	private Integer maxBattery;

	private Integer minSignal;

	private Integer maxSignal;

	private String search;

	@Builder.Default
	private int page = 0;

	@Builder.Default
	private int size = 10;

	@Builder.Default
	private String sortBy = "receivedAt";

	@Builder.Default
	private Sort.Direction sortDirection = Sort.Direction.DESC;

}