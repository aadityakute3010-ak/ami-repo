package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.PayloadStatus;

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
public class PayloadLogDTO {

	private Long payloadId;

	private LocalDateTime timestamp;

	/*
	 * Device entity primary key.
	 */
	private Long deviceId;

	/*
	 * Business/device code stored in Device.deviceId.
	 */
	private String deviceCode;

	private String deviceName;

	/*
	 * Business rule: meterNumber is Device.deviceId.
	 */
	private String meterNumber;

	private PayloadStatus status;

	private String message;

	private String failureReason;

	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	private Double startReading;

	private Double endReading;

	private Double consumption;

	private String rawPayload;
}