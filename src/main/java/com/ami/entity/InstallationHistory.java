package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.InstallationStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ami.enums.HistoryStatus;
@Entity
@Table(name = "installation_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private InstallationStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private InstallationStatus newStatus;

    private String status;

    private String performedBy;

    private String performedByRole;

    @Column(length = 1000)
    private String remarks;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id", nullable = false)
    private Installation installation;

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}