package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceHealthChartDto {

    private long totalDevices;

    private long healthyDevices;

    private long warningDevices;

    private long criticalDevices;

    private long offlineDevices;
}
