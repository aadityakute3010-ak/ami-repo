package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerPerformanceResponseDto {

    private Long engineerId;

    private String engineerName;

    private Long assignedIssues;

    private Long resolvedIssues;

    private Long rejectedIssues;

    private Long escalatedIssues;

    private Long inProgressIssues;
    
    private Double resolutionRate;

    private Double slaPerformance;

    private Long completedJobs;

    private Long pendingJobs;

    private Double monthlyPerformance;
}