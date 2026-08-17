package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GasAlarmResponseDto {

    private String deviceId;

    private Boolean alarmActive;

    private Boolean emergencyShutdown;

    private String status;

    private LocalDateTime readingTime;

}