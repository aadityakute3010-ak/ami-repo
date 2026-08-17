package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "installation_checklists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Checklist Items

    @Builder.Default
    private Boolean meterMounted = false;

    @Builder.Default
    private Boolean wiringCompleted = false;

    @Builder.Default
    private Boolean communicationVerified = false;

    @Builder.Default
    private Boolean meterActivated = false;

    @Builder.Default
    private Boolean readingVerified = false;

    @Builder.Default
    private Boolean customerVerified = false;

    // New Fields

    @Builder.Default
    private Boolean mandatory = true;

    private String checkedBy;

    private LocalDateTime checkedAt;

    @Column(length = 1000)
    private String remarks;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installation_id",
            nullable = false,
            unique = true)
    private Installation installation;

    @PrePersist
    public void prePersist() {

        if (checkedAt == null) {
            checkedAt = LocalDateTime.now();
        }
    }
}