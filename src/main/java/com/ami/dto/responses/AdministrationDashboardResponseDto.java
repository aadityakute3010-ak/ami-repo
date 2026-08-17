package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdministrationDashboardResponseDto {

    private Long totalConfigurations;

    private Long activeConfigurations;

    private Long inactiveConfigurations;

    private Long pendingConfigurations;

    private Long deviceConfigurations;

    private Long alertConfigurations;

    private Long thresholdConfigurations;

    private Long firmwareConfigurations;

    private Long communicationConfigurations;
}