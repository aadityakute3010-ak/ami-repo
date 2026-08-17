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
public class InstallationAttachmentResponseDto {

    private Long id;

    private String fileName;

    private String originalFileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String attachmentType;

    private String uploadedBy;

    private LocalDateTime uploadedAt;

}