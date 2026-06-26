package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSummaryResponseDto {

    private Long totalAlerts;

    private Long activeAlerts;

    private Long inactiveAlerts;

    private Long criticalAlerts;
}