package com.ami.dto.responses;

import java.time.LocalDateTime;
import com.ami.enums.PayloadStatus;
import com.ami.enums.SensorStatus;
import com.ami.enums.ValveStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayloadDetailDTO {

	private Long id;

	// =====================================================
	// Device Information
	// =====================================================

	private String deviceName;

	private String meterNumber;

	private String consumerNumber;

	private String macAddress;

	private String firmwareVersion;

	private String simNumber;

	// =====================================================
	// Meter Data
	// =====================================================

	private Double startReading;

	private Double endReading;

	private Double consumption;

	private Double startBalance;

	private Double endBalance;

	// =====================================================
	// Communication Data
	// =====================================================

	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	// =====================================================
	// Timeline
	// =====================================================

	private LocalDateTime receivedAt;

	private ValveStatus valveStatus;

	private SensorStatus sensorStatus;

	private PayloadStatus status;

	// =====================================================
	// Raw Payload
	// =====================================================

	private String rawPayload;
}
