package com.ami.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;
import com.ami.service.DeviceOperationService;

@RestController
@RequestMapping("/api/device-operations")
public class DeviceOperationController {

	private final DeviceOperationService service;

	public DeviceOperationController(DeviceOperationService service) {

		this.service = service;
	}

	@PostMapping
	public DeviceOperationResponseDto createOperation(@RequestBody CreateDeviceOperationRequestDto request) {

		return service.createOperation(request);
	}

	@GetMapping
	public List<DeviceOperationResponseDto> getAllOperations() {

		return service.getAllOperations();
	}

	@GetMapping("/{id}")
	public DeviceOperationResponseDto getOperationById(@PathVariable Long id) {

		return service.getOperationById(id);
	}

	@GetMapping("/device/{deviceId}")
	public List<DeviceOperationResponseDto> getByDeviceId(@PathVariable String deviceId) {

		return service.getByDeviceId(deviceId);
	}

	@GetMapping("/type/{operationType}")
	public List<DeviceOperationResponseDto> getByOperationType(@PathVariable String operationType) {

		return service.getByOperationType(operationType);
	}

	@DeleteMapping("/{id}")
	public String deleteOperation(@PathVariable Long id) {

		return service.deleteOperation(id);
	}
}