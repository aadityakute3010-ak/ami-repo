package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Alert being assigned.
     */
    @Column(nullable = false)
    private Long alertId;

    /**
     * Type of assignment.
     * ADMIN or DEVICE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType assignmentType;

    /**
     * Admin assigned to the alert.
     * Used when assignmentType = ADMIN.
     */
    private Long adminId;

    /**
     * Device assigned to the alert.
     * Used when assignmentType = DEVICE.
     */
    private String deviceId;

    /**
     * User/admin who performed the assignment.
     */
    private String assignedBy;
    
    @Column(length = 500)
    private String reason;

    /**
     * Assignment creation time.
     */
    @Column(nullable = false)
    private LocalDateTime assignedAt;

    /**
     * Indicates whether this assignment is currently active.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Assignment last update time.
     */
    private LocalDateTime updatedAt;

    public enum AssignmentType {
        ADMIN,
        DEVICE
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (assignedAt == null) {
            assignedAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}