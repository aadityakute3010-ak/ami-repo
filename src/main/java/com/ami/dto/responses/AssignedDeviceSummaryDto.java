package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedDeviceSummaryDto {

    private String deviceId;

    private String deviceName;

    private String deviceType;

    private String status;
}