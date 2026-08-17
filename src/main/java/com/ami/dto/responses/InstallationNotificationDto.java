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
public class InstallationNotificationDto {

    private Long installationId;

    private String installationNumber;

    private String title;

    private String message;

    private String event;

    private String performedBy;

    private LocalDateTime timestamp;
}