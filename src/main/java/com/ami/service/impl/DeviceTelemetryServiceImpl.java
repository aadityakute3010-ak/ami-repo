package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceMetricsDashboardResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.entity.DeviceTelemetry;
import com.ami.repository.DeviceTelemetryRepository;
import com.ami.service.DeviceTelemetryService;

@Service
public class DeviceTelemetryServiceImpl
        implements DeviceTelemetryService {

    private final DeviceTelemetryRepository repository;

    public DeviceTelemetryServiceImpl(
            DeviceTelemetryRepository repository) {

        this.repository = repository;
    }

    @Override
    public DeviceTelemetryResponseDto createTelemetry(
            CreateDeviceTelemetryRequestDto request) {

        DeviceTelemetry telemetry =
                DeviceTelemetry.builder()
                        .deviceId(
                                request.getDeviceId())
                        .sourceType(
                                request.getSourceType())
                        .flowRate(
                                request.getFlowRate())
                        .pressure(
                                request.getPressure())
                        .temperature(
                                request.getTemperature())
                        .consumption(
                                request.getConsumption())
                        .leakDetected(
                                request.getLeakDetected())
                        .deviceOnline(
                                request.getDeviceOnline())
                        .batteryLevel(
                                request.getBatteryLevel())
                        .valveStatus(
                                request.getValveStatus())
                        .pipelineHealthScore(
                                request.getPipelineHealthScore())
                        .sensorHealthScore(
                                request.getSensorHealthScore())
                        .status(
                                request.getStatus())
                        .readingTime(
                                LocalDateTime.now())
                        .build();

        telemetry =
                repository.save(
                        telemetry);

        return mapToResponse(
                telemetry);
    }

    @Override
    public List<DeviceTelemetryResponseDto>
    getAllTelemetry() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DeviceTelemetryResponseDto
    getTelemetryById(
            Long id) {

        DeviceTelemetry telemetry =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Telemetry not found"));

        return mapToResponse(
                telemetry);
    }

    @Override
    public List<DeviceTelemetryResponseDto>
    getTelemetryByDeviceId(
            String deviceId) {

        return repository
                .findByDeviceId(
                        deviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DeviceTelemetryResponseDto
    getLatestTelemetry(
            String deviceId) {

        DeviceTelemetry telemetry =
                repository
                        .findTopByDeviceIdOrderByReadingTimeDesc(
                                deviceId);

        if (telemetry == null) {

            throw new RuntimeException(
                    "No telemetry found");
        }

        return mapToResponse(
                telemetry);
    }

    @Override
    public DeviceMetricsDashboardResponseDto
    getDashboard() {

        List<DeviceTelemetry> list =
                repository.findAll();

        long totalDevices =
                list.stream()
                        .map(DeviceTelemetry::getDeviceId)
                        .distinct()
                        .count();

        long onlineDevices =
                list.stream()
                        .filter(t ->
                                Boolean.TRUE.equals(
                                        t.getDeviceOnline()))
                        .count();

        long leakDetectedDevices =
                list.stream()
                        .filter(t ->
                                Boolean.TRUE.equals(
                                        t.getLeakDetected()))
                        .count();

        long offlineDevices =
                totalDevices - onlineDevices;

        double totalConsumption =
                list.stream()
                        .mapToDouble(t ->
                                t.getConsumption() == null
                                        ? 0
                                        : t.getConsumption())
                        .sum();

        double avgPressure =
                list.stream()
                        .mapToDouble(t ->
                                t.getPressure() == null
                                        ? 0
                                        : t.getPressure())
                        .average()
                        .orElse(0);

        double avgTemperature =
                list.stream()
                        .mapToDouble(t ->
                                t.getTemperature() == null
                                        ? 0
                                        : t.getTemperature())
                        .average()
                        .orElse(0);

        double avgFlowRate =
                list.stream()
                        .mapToDouble(t ->
                                t.getFlowRate() == null
                                        ? 0
                                        : t.getFlowRate())
                        .average()
                        .orElse(0);

        return DeviceMetricsDashboardResponseDto
                .builder()
                .totalDevices(
                        totalDevices)
                .onlineDevices(
                        onlineDevices)
                .offlineDevices(
                        offlineDevices)
                .leakDetectedDevices(
                        leakDetectedDevices)
                .totalConsumption(
                        totalConsumption)
                .averagePressure(
                        avgPressure)
                .averageTemperature(
                        avgTemperature)
                .averageFlowRate(
                        avgFlowRate)
                .build();
    }

    @Override
    public DeviceAnalyticsResponseDto
    getAnalytics() {

        List<DeviceTelemetry> list =
                repository.findAll();

        double totalConsumption =
                list.stream()
                        .mapToDouble(t ->
                                t.getConsumption() == null
                                        ? 0
                                        : t.getConsumption())
                        .sum();

        double averageConsumption =
                list.stream()
                        .mapToDouble(t ->
                                t.getConsumption() == null
                                        ? 0
                                        : t.getConsumption())
                        .average()
                        .orElse(0);

        double peakConsumption =
                list.stream()
                        .mapToDouble(t ->
                                t.getConsumption() == null
                                        ? 0
                                        : t.getConsumption())
                        .max()
                        .orElse(0);

        double averagePressure =
                list.stream()
                        .mapToDouble(t ->
                                t.getPressure() == null
                                        ? 0
                                        : t.getPressure())
                        .average()
                        .orElse(0);

        double averageTemperature =
                list.stream()
                        .mapToDouble(t ->
                                t.getTemperature() == null
                                        ? 0
                                        : t.getTemperature())
                        .average()
                        .orElse(0);

        double averageFlowRate =
                list.stream()
                        .mapToDouble(t ->
                                t.getFlowRate() == null
                                        ? 0
                                        : t.getFlowRate())
                        .average()
                        .orElse(0);

        return DeviceAnalyticsResponseDto
                .builder()
                .totalConsumption(
                        totalConsumption)
                .averageConsumption(
                        averageConsumption)
                .peakConsumption(
                        peakConsumption)
                .averagePressure(
                        averagePressure)
                .averageTemperature(
                        averageTemperature)
                .averageFlowRate(
                        averageFlowRate)
                .totalReadings(
                        (long) list.size())
                .build();
    }

    @Override
    public String deleteTelemetry(
            Long id) {

        DeviceTelemetry telemetry =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Telemetry not found"));

        repository.delete(
                telemetry);

        return "Telemetry deleted successfully";
    }

    private DeviceTelemetryResponseDto
    mapToResponse(
            DeviceTelemetry telemetry) {

        return DeviceTelemetryResponseDto
                .builder()
                .id(
                        telemetry.getId())
                .deviceId(
                        telemetry.getDeviceId())
                .sourceType(
                        telemetry.getSourceType())
                .flowRate(
                        telemetry.getFlowRate())
                .pressure(
                        telemetry.getPressure())
                .temperature(
                        telemetry.getTemperature())
                .consumption(
                        telemetry.getConsumption())
                .leakDetected(
                        telemetry.getLeakDetected())
                .deviceOnline(
                        telemetry.getDeviceOnline())
                .batteryLevel(
                        telemetry.getBatteryLevel())
                .valveStatus(
                        telemetry.getValveStatus())
                .pipelineHealthScore(
                        telemetry.getPipelineHealthScore())
                .sensorHealthScore(
                        telemetry.getSensorHealthScore())
                .status(
                        telemetry.getStatus())
                .readingTime(
                        telemetry.getReadingTime())
                .build();
        
    }
}
