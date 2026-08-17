package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertAssignmentOverviewResponseDto {

    private AdminAlertAssignmentPageResponseDto byAdmin;

    private DeviceAlertAssignmentPageResponseDto byDevice;

    private AlertRuleAssignmentPageResponseDto byAlertRule;
}