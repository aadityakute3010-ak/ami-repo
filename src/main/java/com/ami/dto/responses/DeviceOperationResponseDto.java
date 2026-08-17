package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceOperationResponseDto {

    private Long id;

    private String deviceId;

    private SourceType sourceType;

    private String operationType;

    private String title;

    private String description;

    private String severity;

    private String status;

    private String assignedTo;

    private String rootCause;

    private Double latitude;

    private Double longitude;

    private Boolean resolved;
    
    private String responseMessage;

    private LocalDateTime executedAt;
    
    private String acknowledgedBy;

    private LocalDateTime acknowledgedAt;
}