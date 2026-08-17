package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDashboardResponseDto {

    private Long totalAlarms;

    private Long activeAlarms;

    private Long resolvedAlarms;

    private Long criticalAlarms;

    private Long highAlarms;

    private Long mediumAlarms;

    private Long lowAlarms;

    private Long acknowledgedAlarms;

    private Long escalatedAlarms;
}