package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.PayloadStatus;
import com.ami.enums.SensorStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
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
public class PayloadDetailDTO {

	private Long id;

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

	private String firmwareVersion;

	private String simNumber;

	// =====================================================
	// Customer and Location Information
	// =====================================================

	private String customerName;

	private String customerAddress;

	private String buildingOrWing;

	private String area;

	private String zone;

	private String city;

	private String state;

	private String meterLocation;

	// =====================================================
	// Meter Information
	// =====================================================

	private SourceType sourceType;

	private TechnologyType technologyType;

	private String technologyName;

	private String meterType;

	private String application;

	private String meterStatus;

	private String diameterSize;

	private Double literPerPulse;

	private String ctRatio;

	private String ptRatio;

	private String voltageClass;

	private String inverterType;

	private String plantCapacity;

	private Integer panelCount;

	private Double meterStartReading;

	// =====================================================
	// Reading and Balance Information
	// =====================================================

	private Double startReading;

	private Double endReading;

	private Double consumption;

	private Double startBalance;

	private Double endBalance;

	private Double rechargeAmount;

	// =====================================================
	// Communication Information
	// =====================================================

	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	private String networkType;

	// =====================================================
	// Device Runtime Information
	// =====================================================

	private Boolean online;

	private LocalDateTime lastSyncTime;

	private Integer dataSampleCount;

	private String wakeupTime;

	private DeviceHealthStatus deviceHealth;

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

	// =====================================================
	// Device State
	// =====================================================

	private ValveStatus valveStatus;

	private SensorStatus sensorStatus;

	// =====================================================
	// Payload Status
	// =====================================================

	private PayloadStatus status;

	private String failureReason;

	// =====================================================
	// Ownership Information
	// =====================================================

	private String createdBy;

	private String assignedAdmin;

	private String assignedUser;

	// =====================================================
	// Timeline
	// =====================================================

	private LocalDateTime receivedAt;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	// =====================================================
	// Raw Payload
	// =====================================================

	private String rawPayload;

}