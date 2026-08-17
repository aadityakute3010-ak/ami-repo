package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertHistoryResponseDto {

    private Long id;

    private Long alertId;

    private String action;

    private String description;

    private LocalDateTime timestamp;
}