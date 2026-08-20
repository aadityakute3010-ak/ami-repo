package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.BillingType;
import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDto {

	private Long id;

	// Device Identity

	private String deviceId;

	private String deviceName;

	private String meterName;

	private String macAddress;

	private String serialNumber;

	// Device Information

	private TechnologyType technologyType;

	private SourceType sourceType;

	private DeviceStatus status;
	
	private BillingType billingType;

	// Runtime
	private LocalDateTime lastSyncTime;

	// Customer Information

	private String customerName;

	private String customerAddress;

	private String buildingOrWing;

	private String area;

	private String zone;

	private String city;

	private String state;

	private String meterLocation;

	// Communication Settings
	private String wakeupTime;

	private Integer dataSampleCount;

	// Meter Information
	private Double meterStartReading;

	// Dynamic Meter Information

	private String meterType;

	private String application;

	// WATER
	private String diameterSize;

	private Double literPerPulse;

	// ENERGY
	private String ctRatio;

	private String ptRatio;

	private String voltageClass;

	// SOLAR
	private String inverterType;

	private String plantCapacity;

	private Integer panelCount;

	// assign admin and user id

	private Long assignedAdminId;
	private Long assignedUserId;

	// Assignment

	private String assignedAdminName;

	private String assignedUserName;

	// Audit Information

	private LocalDateTime createdAt;

	private DeviceHealthStatus healthStatus;

}