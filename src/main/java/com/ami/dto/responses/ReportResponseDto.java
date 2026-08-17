package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportResponseDto {

    private String reportName;

    private String reportType;

    private String generatedBy;

    private String generatedAt;

    private Long totalRecords;

    private String downloadUrl;
}