package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import com.ami.enums.AssignmentMethod;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponseDto {

    private Long id;

    private String ticketNumber;

    private String title;

    private String description;

    private IssueCategory category;

    private IssuePriority priority;

    private IssueStatus status;

    private SourceType sourceType;

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

    private String address;

    private Double latitude;

    private Double longitude;

    // Engineer
    private AssignedEngineerResponseDto assignedEngineer;

    // Workflow
    private LocalDateTime assignedAt;

    private String assignedBy;

    private LocalDateTime acceptedAt;
    
    private String acceptedBy;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
    
    private Integer progressPercentage;

    private String currentWork;

    private LocalDateTime estimatedCompletion;

    private String updatedBy;

    private LocalDateTime closedAt;

    // SLA
    private LocalDateTime responseDueAt;

    private LocalDateTime resolutionDueAt;

    private Boolean slaBreached;

    private String slaStatus;

    // Resolution
    private String rootCause;

    private String actionTaken;

    private String resolutionNotes;

    private String resolvedBy;

    private LocalDateTime resolvedAt;

    // Escalation
    private Boolean escalated;

    private String escalatedBy;

    private LocalDateTime escalatedAt;

    private String escalationReason;

    // Rejection
    private Integer rejectionCount;

    private String lastRejectReason;

    // Child Data
    private List<IssueTimelineResponseDto> timeline;

    private List<IssueCommentResponseDto> comments;

    private List<IssueAttachmentResponseDto> attachments;

    private List<IssueMaterialResponseDto> materials;

    private List<FieldVisitResponseDto> fieldVisits;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}