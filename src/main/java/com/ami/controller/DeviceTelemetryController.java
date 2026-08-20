package com.ami.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceMetricsDashboardResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.service.DeviceTelemetryService;

@RestController
@RequestMapping("/api/telemetry")
public class DeviceTelemetryController {

	private final DeviceTelemetryService service;

	public DeviceTelemetryController(DeviceTelemetryService service) {

		this.service = service;
	}

	@PostMapping
	public DeviceTelemetryResponseDto createTelemetry(@RequestBody CreateDeviceTelemetryRequestDto request) {

		return service.createTelemetry(request);
	}

	@GetMapping
	public List<DeviceTelemetryResponseDto> getAllTelemetry() {

		return service.getAllTelemetry();
	}

	@GetMapping("/{id}")
	public DeviceTelemetryResponseDto getTelemetryById(@PathVariable Long id) {

		return service.getTelemetryById(id);
	}

	@GetMapping("/device/{deviceId}")
	public List<DeviceTelemetryResponseDto> getTelemetryByDeviceId(@PathVariable String deviceId) {

		return service.getTelemetryByDeviceId(deviceId);
	}

	@GetMapping("/latest/{deviceId}")
	public DeviceTelemetryResponseDto getLatestTelemetry(@PathVariable String deviceId) {

		return service.getLatestTelemetry(deviceId);
	}

	@GetMapping("/dashboard")
	public DeviceMetricsDashboardResponseDto getDashboard() {

		return service.getDashboard();
	}

	@GetMapping("/analytics")
	public DeviceAnalyticsResponseDto getAnalytics() {

		return service.getAnalytics();
	}

	@DeleteMapping("/{id}")
	public String deleteTelemetry(@PathVariable Long id) {

		return service.deleteTelemetry(id);
	}
}