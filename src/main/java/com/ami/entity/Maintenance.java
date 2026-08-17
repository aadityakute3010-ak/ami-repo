package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.MaintenancePriority;
import com.ami.enums.MaintenanceSource;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "maintenance",
    indexes = {

        @Index(
            name = "idx_maintenance_device",
            columnList = "deviceId"
        ),

        @Index(
            name = "idx_maintenance_status",
            columnList = "status"
        ),

        @Index(
            name = "idx_maintenance_engineer",
            columnList = "assignedEngineer"
        ),

        @Index(
            name = "idx_maintenance_type",
            columnList = "maintenanceType"
        ),

        @Index(
            name = "idx_maintenance_source",
            columnList = "source"
        ),

        @Index(
            name = "idx_maintenance_priority",
            columnList = "priority"
        ),

        @Index(
            name = "idx_maintenance_scheduled_at",
            columnList = "scheduledAt"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Device
     */
    private String deviceId;

    /*
     * Maintenance Type
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    /*
     * Maintenance Source
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceSource source;

    /*
     * Priority
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenancePriority priority;

    /*
     * Current Maintenance Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    /*
     * Basic Information
     */
    private String title;

    @Column(length = 2000)
    private String description;

    /*
     * Engineer
     *
     * Keep the existing String field for now.
     * We will introduce engineerId / proper engineer
     * response handling in the Assignment step.
     */
    private String assignedEngineer;

    private Long assignedEngineerId;

    private LocalDateTime assignedAt;

    /*
     * Dates
     */
    private LocalDateTime preferredDate;

    private LocalDateTime scheduledAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime rescheduledAt;

    /*
     * Workflow counters
     */
    @Builder.Default
    private Integer rescheduleCount = 0;

    @Builder.Default
    private Integer autoReassignmentCount = 0;

    @Builder.Default
    private Integer assignmentFailureCount = 0;

    @Builder.Default
    private Boolean manualAssignmentRequired = false;

    private LocalDateTime lastAssignmentFailedAt;

    @Column(length = 1000)
    private String lastAssignmentFailureReason;

    /*
     * Duration
     *
     * Store duration in minutes.
     */
    private Integer estimatedDuration;

    private Integer actualDuration;

    /*
     * Cost
     *
     * Keep existing maintenanceCost for backward compatibility.
     */
    private Double maintenanceCost;

    private Double totalCost;

    /*
     * Parts / Remarks
     */
    @Column(length = 2000)
    private String replacementParts;

    @Column(length = 2000)
    private String remarks;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean inspectionCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cleaningCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean calibrationCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean firmwareUpdated = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean partsVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean testingCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean customerVerified = false;
    
    @Column(length = 1000)
    private String beforePhotoUrl;

    @Column(length = 1000)
    private String afterPhotoUrl;
    
    @Column(length = 5000)
    private String attachmentUrls;
}