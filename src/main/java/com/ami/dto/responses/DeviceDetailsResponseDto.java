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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDetailsResponseDto {

    // Header Section
    private Long id;

    private String deviceId;

    private String deviceName;

    private String meterName;

    private SourceType sourceType;

    private DeviceStatus status;

    private Boolean online;

    // Basic Information
    private String macAddress;

    private String serialNumber;

    private TechnologyType technologyType;

    private LocalDateTime lastSyncTime;

    private DeviceHealthStatus healthStatus;
    
    private BillingType billingType;

    // Assignment
    private String assignedAdmin;

    private String assignedUser;

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

    // Meter Configuration - Common
    private Double meterStartReading;

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
}