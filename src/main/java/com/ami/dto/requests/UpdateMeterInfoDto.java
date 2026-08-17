package com.ami.dto.requests;

import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import lombok.Data;

@Data
public class UpdateMeterInfoDto {

    // Locked Meter Fields
    // SUPER_ADMIN only

    private String meterName;

    private SourceType sourceType;

    private TechnologyType technologyType;

    private Double meterStartReading;

    // Dynamic Meter Fields
    // SUPER_ADMIN + ADMIN

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