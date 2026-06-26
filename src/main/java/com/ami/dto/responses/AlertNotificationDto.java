package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertNotificationDto {

    private Long alertId;

    private String alertName;

    private String severity;

    private String message;
}