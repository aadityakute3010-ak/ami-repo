package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationDashboardResponseDto {

    private Long total;

    private Long pending;

    private Long assigned;

    private Long inProgress;

    private Long completed;

    private Long cancelled;

    private Long todayScheduled;

    private Long activeEngineers;

}