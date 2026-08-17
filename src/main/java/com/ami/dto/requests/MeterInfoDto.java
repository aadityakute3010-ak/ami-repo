package com.ami.dto.requests;

import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MeterInfoDto {

    
    private String meterName;

    @NotNull
    private SourceType sourceType;

    @NotNull
    private TechnologyType technologyType;

    // Common dynamic fields
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