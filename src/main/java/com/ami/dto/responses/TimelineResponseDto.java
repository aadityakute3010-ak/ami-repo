package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimelineResponseDto {

    private String deviceId;

    private String event;

    private String status;

    private LocalDateTime timestamp;
}