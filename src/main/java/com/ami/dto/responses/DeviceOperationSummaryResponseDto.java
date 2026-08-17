package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceOperationSummaryResponseDto {

    private Long totalOperations;

    private Long resolvedOperations;

    private Long pendingOperations;

    private Long waterOperations;

    private Long gasOperations;

    private Long energyOperations;

    private Long solarOperations;
}