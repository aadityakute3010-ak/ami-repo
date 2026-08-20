package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.PayloadStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

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
public class PayloadSummaryDTO {

	private Long id;

	private LocalDateTime timestamp;

	private PayloadStatus status;

	private String failureReason;

	// =====================================================
	// Device Information
	// =====================================================

	/*
	 * Device entity primary key.
	 * Device field name is "id".
	 * Frontend response field name is "devicePkId".
	 */
	private Long devicePkId;

	private String deviceId;

	private String deviceName;

	/*
	 * Business rule:
	 * meterNumber is the same as Device.deviceId.
	 */
	private String meterNumber;

	private String meterName;

	private String consumerNumber;

	private String macAddress;

	private SourceType sourceType;

	private TechnologyType technologyType;

	private Boolean online;

	private DeviceHealthStatus deviceHealth;

	private LocalDateTime lastSyncTime;

	// =====================================================
	// Reading Information
	// =====================================================

	private Double startReading;

	private Double endReading;

	private Double consumption;

	// =====================================================
	// Communication Information
	// =====================================================

	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	private String firmwareVersion;

	private String simNumber;

	private String networkType;

}