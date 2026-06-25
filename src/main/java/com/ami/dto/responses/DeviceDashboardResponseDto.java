package com.ami.dto.responses;

import java.util.List;

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
public class DeviceDashboardResponseDto {

    private DashboardSummaryResponseDto summary;

    private DeviceHealthChartDto healthChart;

    private DeviceStatusChartDto statusChart;
    
    private List<OfflineDeviceDto> recentOfflineDevices; 
} 