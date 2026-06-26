package com.ami.dto.responses;

import lombok.Data;

@Data
public class EngineerDashboardResponseDto {

    private Long assigned;

    private Long inProgress;

    private Long resolved;

    private Long escalated;
}