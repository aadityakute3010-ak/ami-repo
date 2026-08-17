package com.ami.dto.requests;

import java.time.LocalDateTime;

import com.ami.enums.MaintenancePriority;
import com.ami.enums.MaintenanceSource;
import com.ami.enums.MaintenanceType;

import lombok.Data;

@Data
public class CreateMaintenanceRequestDto {

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

    /*
     * Basic information
     */
    private String title;

    private String description;

    /*
     * Existing engineer field
     *
     * Kept for backward compatibility.
     * Proper engineer assignment will be handled
     * through AssignEngineerRequestDto.
     */
    private String assignedEngineer;

    /*
     * Scheduling
     */
    private LocalDateTime preferredDate;

    private LocalDateTime scheduledAt;

    /*
     * Maintenance details
     */
    private Double maintenanceCost;

    private Double totalCost;

    private String replacementParts;

    private String remarks;

    /*
     * Estimated duration in minutes
     */
    private Integer estimatedDuration;
}