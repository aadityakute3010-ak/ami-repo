package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Alert configuration which triggered this event.
     */
    @Column(nullable = false)
    private Long alertId;

    /*
     * Device on which the alert was triggered.
     * Frontend sends the numeric Device.id.
     */
    @Column(nullable = false)
    private Long deviceId;

    /*
     * Runtime value which caused the event.
     */
    private Double actualValue;

    /*
     * Threshold configured for the alert.
     */
    private Double thresholdValue;

    /*
     * Event message.
     */
    @Column(length = 1000)
    private String message;

    /*
     * Event severity.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    /*
     * Event status.
     *
     * Expected frontend values:
     * ACTIVE
     * ACKNOWLEDGED
     * RESOLVED
     * IGNORED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    /*
     * Acknowledgement information.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean acknowledged = false;

    private LocalDateTime acknowledgedAt;

    private Long acknowledgedById;

    private String acknowledgedByName;

    /*
     * Resolution information.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean resolved = false;

    private LocalDateTime resolvedAt;

    private Long resolvedById;

    private String resolvedByName;

    /*
     * Time at which the alert event was triggered.
     */
    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    /*
     * Audit timestamps.
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @Column(length = 1000)
    private String resolutionNotes;

    @jakarta.persistence.PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (triggeredAt == null) {
            triggeredAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (acknowledged == null) {
            acknowledged = false;
        }

        if (resolved == null) {
            resolved = false;
        }
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}