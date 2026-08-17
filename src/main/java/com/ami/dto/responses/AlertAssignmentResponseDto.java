package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertAssignmentResponseDto {

    private Long id;

    private Long alertId;

    private String assignmentType;

    private Long adminId;

    private String deviceId;

    private String assignedBy;

    private LocalDateTime assignedAt;

    private Boolean active;

    private LocalDateTime updatedAt;

    /*
     * Optional information returned to frontend.
     */
    private String reason;
}