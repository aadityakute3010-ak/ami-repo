package com.ami.dto.responses;

import lombok.Data;

@Data
public class EngineerWorkloadResponseDto {

    private Long activeIssues;
    private Long resolvedIssues;
    private Long rejectedIssues;
    private Long assignedIssues;

    private Long completedIssues;

    private Long todayWorkload;

    private Long weeklyWorkload;
}