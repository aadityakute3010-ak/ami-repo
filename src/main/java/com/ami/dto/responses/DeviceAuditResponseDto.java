package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAuditResponseDto {

    private Long id;

    private String action;

    private String description;

    private String performedBy;

    private LocalDateTime actionTime;
}