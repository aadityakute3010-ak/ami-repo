package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationPhotoResponseDto {

    private Long id;

    private String fileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String uploadedBy;

    private String caption;

    private String photoType;

    private Boolean mandatory;

    private Boolean primaryPhoto;

    private LocalDateTime uploadedAt;
}