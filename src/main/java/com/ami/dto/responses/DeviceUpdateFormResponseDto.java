package com.ami.dto.responses;

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
public class DeviceUpdateFormResponseDto {

    // Device Information

    private String deviceName;

    private String meterName;

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

    // Meter Configuration

    private SourceType sourceType;

    private TechnologyType technologyType;

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

    // COMMON

    private Double meterStartReading;
}