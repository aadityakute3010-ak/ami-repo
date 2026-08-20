package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.PayloadStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayloadSummaryDTO {

	private Long id;

	// Device Info
	private String deviceName;

	private String meterNumber;

	private String macAddress;

	// Payload Info
	private LocalDateTime timestamp;

	private Double startReading;

	private Double endReading;

	private Integer batteryPercentage;

	private Integer signalQuality;

	private PayloadStatus status;
}