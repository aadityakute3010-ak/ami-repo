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
public class IssueAttachmentResponseDto {

    private Long id;

    private String fileName;

    private String fileUrl;

    private String fileType;

    private Long fileSize;

    private String uploadedBy;

    private LocalDateTime uploadedAt;
}