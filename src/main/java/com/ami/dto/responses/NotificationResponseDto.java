package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponseDto {

    private Long id;

    private NotificationType type;

    private NotificationStatus status;

    private String title;

    private String message;

    private String recipient;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}