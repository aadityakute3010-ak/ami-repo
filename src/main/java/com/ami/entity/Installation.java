package com.ami.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ami.enums.AssignmentStatus;
@Entity
@Table(name = "installations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Installation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String installationNumber;

    private String deviceId;

    private String deviceName;

    private String meterNumber;

    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private InstallationSource source;

    private String customerId;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String state;

    private String city;

    private String zone;

    private String area;

    @Column(length = 1000)
    private String address;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private InstallationPriority priority;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InstallationStatus status = InstallationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_engineer_id")
    private User assignedEngineer;
    
    private String assignedBy;

    private LocalDateTime assignedAt;

    private LocalDateTime scheduledDate;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
    
    @Builder.Default
    private Integer assignmentRetryCount = 0;

    private LocalDateTime lastAssignmentAttempt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssignmentStatus assignmentStatus =
            AssignmentStatus.PENDING;

    @Builder.Default
    private Double completionPercentage = 0.0;

    @OneToOne(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private InstallationChecklist checklist;

    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationPhoto> photos = new ArrayList<>();
    
    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationAttachment> attachments =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationTimeline> timeline = new ArrayList<>();

    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationHistory> history = new ArrayList<>();

    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationRemark> remarks = new ArrayList<>();
    
    @OneToMany(
            mappedBy = "installation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<InstallationAssignmentAttempt> assignmentAttempts =
            new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (installationNumber == null) {
            installationNumber = "INS-" + System.currentTimeMillis();
        }

        if (status == null) {
            status = InstallationStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}