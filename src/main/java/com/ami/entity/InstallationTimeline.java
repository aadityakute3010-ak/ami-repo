package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.InstallationTimelineEvent;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installation_timeline")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InstallationTimelineEvent event =
            InstallationTimelineEvent.OTHER;

    @Column(length = 1000)
    private String description;

    private String performedBy;

    private String performedByRole;

    @Column(length = 1000)
    private String remarks;

    @Builder.Default
    private LocalDateTime eventTime =
            LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installation_id",
            nullable = false)
    private Installation installation;

    @PrePersist
    public void prePersist() {

        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }
}