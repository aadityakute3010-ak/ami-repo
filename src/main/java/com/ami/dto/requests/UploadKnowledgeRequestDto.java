package com.ami.dto.requests;

import lombok.Data;

@Data
public class UploadKnowledgeRequestDto {

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String uploadedBy;

    private String description;

    private String filePath;
}