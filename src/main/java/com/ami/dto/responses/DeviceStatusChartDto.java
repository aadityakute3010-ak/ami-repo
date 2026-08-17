package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusChartDto {

    private long totalDevices;

    private long activeDevices;

    private long inactiveDevices;

    private long faultyDevices;

    private long offlineDevices;
}
