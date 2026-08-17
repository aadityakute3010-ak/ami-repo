package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAlertAssignmentResponseDto {

    private Long deviceId;

    private String deviceName;

    private String deviceType;

    private String imei;

    private String adminName;

    private String adminEmail;

    private String userName;

    private String userEmail;

    private LocalDateTime assignedOn;

    private String status;

    private List<AssignedAlertSummaryDto> alerts;
}