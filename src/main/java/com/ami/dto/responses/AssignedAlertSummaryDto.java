package com.ami.dto.responses;

import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedAlertSummaryDto {

    private Long alertId;

    private String alertName;

    private AlertSource source;

    private AlertSeverity severity;
}