package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaintenanceDashboardResponseDto {

    private Long totalMaintenance;

    private Long scheduledMaintenance;

    private Long inProgressMaintenance;

    private Long completedMaintenance;

    private Long cancelledMaintenance;

    private Long preventiveMaintenance;

    private Long correctiveMaintenance;

    private Double totalMaintenanceCost;
}