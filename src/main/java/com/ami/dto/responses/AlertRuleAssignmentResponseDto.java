package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

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
public class AlertRuleAssignmentResponseDto {

    private Long alertId;

    private String alertName;

    private String alertCode;

    private AlertSource source;

    private AlertSeverity severity;

    private LocalDateTime assignedOn;

    private String status;

    private List<AssignedAdminSummaryDto> assignedAdmins;

    private List<AssignedDeviceSummaryDto> assignedDevices;
}