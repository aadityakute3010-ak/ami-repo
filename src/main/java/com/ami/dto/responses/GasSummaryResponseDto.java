package com.ami.dto.responses;

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
public class GasSummaryResponseDto {

    private Long totalDevices;

    private Long onlineDevices;

    private Long offlineDevices;

    private Double totalConsumption;

    private Double totalFlow;

    private Double averagePressure;

    private Double averageTemperature;
    
    private Double averageGasConcentration;

    private Double averageGasDensity;

    private Long activeLeaks;

    private Long activeAlarms;

    private Long emergencyShutdownCount;

}