package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardChartResponseDto {

    private List<String> labels;

    private List<Double> values;
}