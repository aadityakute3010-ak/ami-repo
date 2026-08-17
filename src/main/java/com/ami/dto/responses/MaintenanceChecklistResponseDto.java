package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceChecklistResponseDto {

    private Long maintenanceId;

    private Boolean inspectionCompleted;

    private Boolean cleaningCompleted;

    private Boolean calibrationCompleted;

    private Boolean firmwareUpdated;

    private Boolean partsVerified;

    private Boolean testingCompleted;

    private Boolean customerVerified;

    private Boolean allCompleted;
}