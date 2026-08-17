package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerStatisticsResponseDto {

    private Long totalEngineers;

    private Long totalAssignedIssues;

    private Long totalResolvedIssues;

    private Long totalRejectedIssues;

    private Long totalEscalatedIssues;

    private Long availableEngineers;

    private Long busyEngineers;

    private Long onLeaveEngineers;
}