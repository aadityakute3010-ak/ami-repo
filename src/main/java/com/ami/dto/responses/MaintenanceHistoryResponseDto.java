package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceHistoryResponseDto {

    private Long maintenanceId;

    private String deviceId;

    private String title;

    private String maintenanceType;

    private String source;

    private String priority;

    private String status;

    private String assignedEngineer;

    private Long assignedEngineerId;

    private LocalDateTime createdAt;

    private LocalDateTime assignedAt;

    private LocalDateTime preferredDate;

    private LocalDateTime scheduledAt;

    private LocalDateTime rescheduledAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private Integer rescheduleCount;

    private Integer assignmentFailureCount;

    private Boolean manualAssignmentRequired;

    private Integer estimatedDuration;

    private Integer actualDuration;

    private Double maintenanceCost;

    private Double totalCost;

    private String replacementParts;

    private String remarks;
}