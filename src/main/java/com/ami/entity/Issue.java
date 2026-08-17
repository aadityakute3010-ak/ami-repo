package com.ami.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ami.enums.AssignmentMethod;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "issues",
        indexes = {

                @Index(
                        name = "idx_issue_ticket",
                        columnList = "ticketNumber"),

                @Index(
                        name = "idx_issue_status",
                        columnList = "status"),

                @Index(
                        name = "idx_issue_priority",
                        columnList = "priority"),

                @Index(
                	    name = "idx_issue_source",
                	    columnList = "sourceType"
                	),

                @Index(
                        name = "idx_issue_device",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_issue_created",
                        columnList = "createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private IssueCategory category;

    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IssueStatus status = IssueStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    private AssignmentMethod assignmentMethod;

    // Customer
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Device
    private String deviceId;
    private String meterId;
    private String serialNumber;
    private String firmwareVersion;
    private String meterType;

    // Location
    private String state;
    private String city;
    private String zone;
    private String area;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Double latitude;
    private Double longitude;

    // Engineer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_engineer_id")
    private User assignedEngineer;

    @Column(length = 100)
    private String assignedBy;

    private LocalDateTime assignedAt;

    // Workflow
    private LocalDateTime acceptedAt;
    
    @Column(length = 100)
    private String acceptedBy;

    private LocalDateTime startedAt;
    
    private Integer progressPercentage;

    @Column(columnDefinition = "TEXT")
    private String currentWork;

    private LocalDateTime estimatedCompletion;

    @Column(columnDefinition = "TEXT")
    private String updatedBy;

    private LocalDateTime completedAt;

    private LocalDateTime closedAt;

    // SLA
    private LocalDateTime responseDueAt;

    private LocalDateTime resolutionDueAt;

    @Builder.Default
    private Boolean slaBreached = false;

    @Column(columnDefinition = "TEXT")
    private String slaBreachReason;

    @Column(length = 50)
    private String slaStatus;

    // Resolution
    @Column(length = 500)
    private String rootCause;

    @Column(length = 500)
    private String actionTaken;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(length = 100)
    private String resolvedBy;

    private LocalDateTime resolvedAt;

    // Escalation
    @Builder.Default
    private Boolean escalated = false;

    @Column(length = 100)
    private String escalatedBy;

    private LocalDateTime escalatedAt;

    @Column(columnDefinition = "TEXT")
    private String escalationReason;

    // Rejection
    @Builder.Default
    private Integer rejectionCount = 0;

    @Column(columnDefinition = "TEXT")
    private String lastRejectReason;

    // Child Entities

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<IssueTimeline> timeline = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<IssueComment> comments = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<IssueAttachment> attachments = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<IssueMaterial> materials = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<FieldVisit> fieldVisits = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<IssueRejectionHistory> rejectionHistory = new ArrayList<>();
}