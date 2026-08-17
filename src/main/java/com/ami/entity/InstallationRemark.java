package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.RemarkType;
import com.ami.enums.RemarkVisibility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installation_remarks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String remark;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RemarkType remarkType =
            RemarkType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RemarkVisibility visibility =
            RemarkVisibility.INTERNAL;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installation_id",
            nullable = false)
    private Installation installation;

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();
    }
}