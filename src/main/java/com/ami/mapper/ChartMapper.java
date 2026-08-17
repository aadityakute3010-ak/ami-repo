package com.ami.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.ami.dto.responses.DashboardChartResponseDto;
import com.ami.entity.DeviceTelemetry;

@Component
public class ChartMapper {

    public DashboardChartResponseDto buildChart(
            List<DeviceTelemetry> telemetryList,
            Function<DeviceTelemetry, Double> valueExtractor) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM HH:mm");

        return DashboardChartResponseDto
                .builder()
                .labels(
                        telemetryList.stream()
                                .map(t ->
                                        t.getReadingTime()
                                                .format(formatter))
                                .toList())
                .values(
                        telemetryList.stream()
                                .map(t -> {
                                    Double value =
                                            valueExtractor.apply(t);
                                    return value == null
                                            ? 0.0
                                            : value;
                                })
                                .toList())
                .build();
    }
}