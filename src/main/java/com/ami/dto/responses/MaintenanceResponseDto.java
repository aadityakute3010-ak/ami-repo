package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import com.ami.enums.MaintenancePriority;
import com.ami.enums.MaintenanceSource;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaintenanceResponseDto {

    private Long id;

    /*
     * Device
     */
    private String deviceId;

    /*
     * Maintenance classification
     */
    private MaintenanceType maintenanceType;

    private MaintenanceSource source;

    private MaintenancePriority priority;

    private MaintenanceStatus status;

    /*
     * Basic information
     */
    private String title;

    private String description;

    /*
     * Engineer
     */
    private Long assignedEngineerId;

    private String assignedEngineer;

    private LocalDateTime assignedAt;

    /*
     * Scheduling
     */
    private LocalDateTime preferredDate;

    private LocalDateTime scheduledAt;

    /*
     * Workflow timestamps
     */
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime rescheduledAt;

    /*
     * Workflow counters
     */
    private Integer rescheduleCount;

    private Integer autoReassignmentCount;

    private Integer assignmentFailureCount;

    private Boolean manualAssignmentRequired;

    private LocalDateTime lastAssignmentFailedAt;

    private String lastAssignmentFailureReason;

    /*
     * Duration
     */
    private Integer estimatedDuration;

    private Integer actualDuration;

    /*
     * Cost
     */
    private Double maintenanceCost;

    private Double totalCost;

    /*
     * Maintenance details
     */
    private String replacementParts;

    private String remarks;
    
    private List<String> attachmentUrls;

    private Integer attachmentCount;
}