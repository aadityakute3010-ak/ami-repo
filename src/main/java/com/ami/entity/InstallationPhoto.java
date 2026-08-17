package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installation_photos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    // New Fields

    private String uploadedBy;

    private String caption;

    private String photoType;

    @Builder.Default
    private Boolean mandatory = false;

    @Builder.Default
    private Boolean primaryPhoto = false;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installation_id",
            nullable = false)
    private Installation installation;

    @PrePersist
    public void prePersist() {

        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}