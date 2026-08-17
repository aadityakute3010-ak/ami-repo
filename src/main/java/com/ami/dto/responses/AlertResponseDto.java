package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertResponseDto {

    private Long id;

    private String name;

    private String fieldLabel;

    private String placeholder;

    private Boolean enabled;

    private String value;

    private AlertSeverity severity;

    private AlertSource source;

    private AlertCategory category;

    private String description;

    private String unit;
    
    private AlertStatus status;
    
    private String deviceId;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime acknowledgedAt;

    private String acknowledgedBy;

    private String resolvedBy;
    
    private Boolean archived;

    private LocalDateTime archivedAt;

    private String archivedBy;
    
    
}