package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.PayloadStatus;
import com.ami.enums.SensorStatus;
import com.ami.enums.SourceType;
import com.ami.enums.ValveStatus;

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
public class DevicePayloadHistoryDTO {

	private Long payloadId;

	private LocalDateTime receivedAt;

	private PayloadStatus status;

	private SourceType sourceType;

	private String failureReason;

	// =====================================================
	// Reading Information
	// =====================================================

	private Double startReading;

	private Double endReading;

	private Double consumption;

	private Double rechargeAmount;

	// =====================================================
	// Communication Information
	// =====================================================

	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	// =====================================================
	// Device State
	// =====================================================

	private ValveStatus valveStatus;

	private SensorStatus sensorStatus;

	// =====================================================
	// Energy Telemetry
	// =====================================================

	private Double voltage;

	private Double current;

	private Double power;

	private Double frequency;

	private Double powerFactor;

	private Double energyConsumed;

	private Double activePower;

	private Double reactivePower;

	private Double apparentPower;

	private Double load;

	private Double demand;

	// =====================================================
	// Water Telemetry
	// =====================================================

	private Double flowRate;

	private Double pressure;

	private Double tankLevel;

	private String pumpStatus;

	private Boolean leakDetected;

	// =====================================================
	// Gas Telemetry
	// =====================================================

	private Double gasFlow;

	private Double gasPressure;

	private Double gasVolume;

	private Double temperature;

	private String pipelineHealth;

	// =====================================================
	// Solar Telemetry
	// =====================================================

	private Double solarVoltage;

	private Double solarCurrent;

	private Double solarPower;

	private Double energyGenerated;

	private Double solarGeneration;

	private Double solarConsumption;

	private Double panelTemperature;

	private Double irradiance;

	private String inverterStatus;

	private Double batteryStorage;

	private Double gridImport;

	private Double gridExport;

	private Double efficiency;
}