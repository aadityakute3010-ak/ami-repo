package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationStatisticsResponseDto {

    private Long total;

    private Long pending;

    private Long assigned;

    private Long inProgress;

    private Long completed;

    private Long cancelled;

    private Long overdue;

    private Long today;

    private Long thisWeek;

    private Long thisMonth;

}