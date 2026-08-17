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

    private Long warningAlerts;

    private Long infoAlerts;

    private Long systemAlerts;

    private Long waterAlerts;

    private Long gasAlerts;

    private Long energyAlerts;

    private Long solarAlerts;

    private Long activeStatusAlerts;

    private Long resolvedAlerts;

    /*
     * =========================================
     * Frontend Alert Management Summary
     * =========================================
     */

    private Long disabledAlerts;

    private Long archivedAlerts;

    private Long highAlerts;

    private Long mediumAlerts;

    private Long lowAlerts;

    /*
     * =========================================
     * Assignment statistics
     *
     * Assignment module will be implemented
     * in a later phase.
     * =========================================
     */

    private Long assignedAlerts;

    private Long unassignedAlerts;

    private Long totalAdminAssignments;

    private Long totalDeviceAssignments;

    /*
     * =========================================
     * Alert trigger statistics
     *
     * Alert Event/Trigger module will be
     * implemented in a later phase.
     * =========================================
     */

    private Long totalTriggers;

    private Long triggeredToday;

    private Long triggeredThisWeek;

    private Long triggeredThisMonth;
}