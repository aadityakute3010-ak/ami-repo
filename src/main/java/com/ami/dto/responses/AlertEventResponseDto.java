package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertEventResponseDto {

    private Long id;

    private Long alertId;

    private String alertName;

    private String alertCategory;

    private String source;

    private Long deviceId;

    private String deviceName;

    private String deviceSerialNumber;

    private Double actualValue;

    private Double thresholdValue;

    private String message;

    private AlertSeverity severity;

    private AlertStatus status;

    private Boolean acknowledged;

    private LocalDateTime acknowledgedAt;

    private Long acknowledgedById;

    private String acknowledgedByName;

    private Boolean resolved;

    private LocalDateTime resolvedAt;

    private Long resolvedById;

    private String resolvedByName;

    private String resolutionNotes;

    private LocalDateTime triggeredAt;
}