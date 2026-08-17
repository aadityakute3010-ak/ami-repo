package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDashboardResponseDto {

    private Long totalNotifications;

    private Long unreadNotifications;

    private Long readNotifications;

    private Long systemNotifications;

    private Long deviceNotifications;

    private Long alertNotifications;

    private Long maintenanceNotifications;

    private Long firmwareNotifications;

    private Long administrationNotifications;
}