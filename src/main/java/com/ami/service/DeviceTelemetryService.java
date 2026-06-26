package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;

public interface DeviceTelemetryService {

    DeviceTelemetryResponseDto createTelemetry(
            CreateDeviceTelemetryRequestDto request);

    List<DeviceTelemetryResponseDto>
    getAllTelemetry();

    DeviceTelemetryResponseDto
    getTelemetryById(
            Long id);

    List<DeviceTelemetryResponseDto>
    getTelemetryByDeviceId(
            String deviceId);

    DeviceTelemetryResponseDto
    getLatestTelemetry(
            String deviceId);

    DeviceDashboardResponseDto
    getDashboard();

    DeviceAnalyticsResponseDto
    getAnalytics();

    String deleteTelemetry(
            Long id);
}