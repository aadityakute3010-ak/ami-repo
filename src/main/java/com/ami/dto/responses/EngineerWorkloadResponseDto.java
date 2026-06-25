package com.ami.dto.responses;

import lombok.Data;

@Data
public class EngineerWorkloadResponseDto {

    private Long activeIssues;
    private Long resolvedIssues;
    private Long rejectedIssues;
}