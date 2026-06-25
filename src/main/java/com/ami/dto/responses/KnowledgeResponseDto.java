package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnowledgeResponseDto {

    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String uploadedBy;

    private String description;

    private String status;

    private String filePath;
}