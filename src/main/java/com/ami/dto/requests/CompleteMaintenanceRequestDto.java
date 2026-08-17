package com.ami.dto.requests;

import lombok.Data;

@Data
public class CompleteMaintenanceRequestDto {

    private String remarks;

    private Double maintenanceCost;

    private Double totalCost;
}