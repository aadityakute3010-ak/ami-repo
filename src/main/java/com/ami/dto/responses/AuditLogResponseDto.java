package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogResponseDto {

    private Long id;

    private String module;

    private Long entityId;

    private String action;

    private String performedBy;

    private String description;

    private LocalDateTime timestamp;
}