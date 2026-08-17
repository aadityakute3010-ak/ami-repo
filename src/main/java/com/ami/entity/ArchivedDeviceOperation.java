package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "archived_device_operations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivedDeviceOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original DeviceOperation ID
    private Long originalOperationId;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String operationType;

    private String title;

    @Column(length = 5000)
    private String description;

    private String severity;

    private String status;

    private String assignedTo;

    private String rootCause;

    private Double latitude;

    private Double longitude;

    private Boolean resolved;

    private String responseMessage;

    private String requestedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    @Column(length = 3000)
    private String response;

    @Column(length = 2000)
    private String remarks;

    private LocalDateTime executedAt;

    private String acknowledgedBy;

    private LocalDateTime acknowledgedAt;

    // Archive Information

    private LocalDateTime archivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_by_id")
    private User archivedBy;

    private String archiveReason;
}