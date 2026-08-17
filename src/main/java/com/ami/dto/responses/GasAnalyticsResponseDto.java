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
public class GasAnalyticsResponseDto {

    private DashboardChartResponseDto consumptionChart;

    private DashboardChartResponseDto pressureChart;

    private DashboardChartResponseDto flowChart;

    private DashboardChartResponseDto concentrationChart;

    private DashboardChartResponseDto densityChart;

    private DashboardChartResponseDto leakChart;

}