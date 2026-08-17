package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirmwareHistoryResponseDto {

    private Long id;

    private String deviceId;

    private String firmwareVersion;

    private String previousVersion;

    private String updateStatus;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private String remarks;
}