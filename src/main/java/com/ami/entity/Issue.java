package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.AssignmentMethod;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "issues")
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

    @Column(length = 5000)
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

    // Customer Details
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Device Details
    private String meterId;
    private String meterType;
    private String serialNumber;
    private String firmwareVersion;

    // Location Details
    private String state;
    private String city;
    private String zone;
    private String area;

    @Column(length = 1000)
    private String address;

    private Double latitude;
    private Double longitude;

    // Assigned Engineer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_engineer_id")
    private User assignedEngineer;

    // Rejection Workflow
    @Builder.Default
    private Integer rejectionCount = 0;

    @Column(length = 3000)
    private String rejectionReason;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String attachments;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comments;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String timeline;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rejectionHistory;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String fieldObservation;

    // SLA
    private Integer slaTargetMinutes;

    @Builder.Default
    private Boolean slaBreached = false;

    private LocalDateTime acceptedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    private LocalDateTime responseDueAt;
    private LocalDateTime resolutionDueAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.ticketNumber == null) {
            this.ticketNumber = "ISS-" + System.currentTimeMillis();
        }

        if (this.timeline == null) {
            this.timeline = "Issue Created : " + LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}