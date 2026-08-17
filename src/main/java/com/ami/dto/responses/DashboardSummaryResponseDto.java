package com.ami.dto.responses;

import lombok.Data;

@Data
public class DashboardSummaryResponseDto {

    private long waterCount;
    private long solarCount;
    private long gasCount;
    private long energyCount;

    private long wifiCount;
    private long ethernetCount;
    private long nbIotCount;
    private long fourGCount;
    
    private long activeInstallations;

    private long activeServiceEngineers;

    private long activeMaintenance;

    private long openIssues;
}