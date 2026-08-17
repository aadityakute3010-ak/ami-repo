package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerActivityResponseDto {

    private String activity;

    private String issueNumber;

    private LocalDateTime activityTime;
}