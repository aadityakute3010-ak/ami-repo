package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmHistoryResponseDto {

    private Long id;

    private Long alertId;

    private String action;

    private String description;

    private LocalDateTime timestamp;
}