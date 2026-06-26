package com.ami.dto.requests;

import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class CreateDeviceOperationRequestDto {

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
    
    private String acknowledgedBy;
}