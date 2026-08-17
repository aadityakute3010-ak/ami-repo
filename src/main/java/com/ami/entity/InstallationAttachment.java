package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installation_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String originalFileName;

    private String filePath;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String attachmentType;

    private String uploadedBy;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installation_id", nullable = false)
    private Installation installation;

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
    }
}