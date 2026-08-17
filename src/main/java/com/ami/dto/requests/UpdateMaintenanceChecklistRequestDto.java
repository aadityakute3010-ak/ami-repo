package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdateMaintenanceChecklistRequestDto {

    private Boolean inspectionCompleted;

    private Boolean cleaningCompleted;

    private Boolean calibrationCompleted;

    private Boolean firmwareUpdated;

    private Boolean partsVerified;

    private Boolean testingCompleted;

    private Boolean customerVerified;
}