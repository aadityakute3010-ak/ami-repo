package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.InstallationStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installation_assignment_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAssignmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id", nullable = false)
    private Installation installation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id")
    private User engineer;

    @Column(length = 1000)
    private String failureReason;

    @Builder.Default
    private Boolean successful = false;

    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;

    private String assignedBy;

    @Builder.Default
    private LocalDateTime attemptedAt = LocalDateTime.now();
}