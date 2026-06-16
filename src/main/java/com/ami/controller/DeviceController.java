package com.ami.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ami.dto.requests.AssignAdminRequestDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceLocationRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import com.ami.service.DeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

	private final DeviceService deviceService;

	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@PostMapping("/createDevice")
	public ResponseEntity<List<DeviceResponseDto>> createDevices(@RequestBody CreateDevicesRequestDto request) {
		return ResponseEntity.ok(deviceService.createDevices(request));
	}

	@GetMapping("/getDevices")
	public ResponseEntity<PagedDeviceResponseDto> getDevices(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) DeviceStatus status, @RequestParam(required = false) SourceType sourceType,
			@RequestParam(required = false) TechnologyType technologyType) {

		return ResponseEntity.ok(deviceService.getDevices(page, size, search, status, sourceType, technologyType));
	}

	@PutMapping("/assign-user/{deviceId}/{userId}")
	public ResponseEntity<DeviceResponseDto> assignDeviceToUser(@PathVariable Long deviceId,
			@PathVariable Long userId) {
		return ResponseEntity.ok(deviceService.assignDeviceToUser(deviceId, userId));
	}

	@GetMapping("/getAvailableDevices/{userId}")
	public ResponseEntity<List<DeviceResponseDto>> getAvailableDevicesForAssignment(@PathVariable Long userId) {
		List<DeviceResponseDto> devices = deviceService.getAvailableDevicesForAssignment(userId);
		return ResponseEntity.ok(devices);
	}

	@GetMapping("/dashboard-summary")
	public ResponseEntity<DashboardSummaryResponseDto> getDashboardSummary() {
		return ResponseEntity.ok(deviceService.getDashboardSummary());
	}

	@GetMapping("/deviceDetails/{deviceId}")
	public ResponseEntity<DeviceDetailsResponseDto> getDeviceDetails(@PathVariable Long deviceId) {
		return ResponseEntity.ok(deviceService.getDeviceDetails(deviceId));
	}

	@DeleteMapping("/soft-delete/{deviceId}")
	public ResponseEntity<String> softDeleteDevice(@PathVariable Long deviceId) {
		deviceService.softDeleteDevice(deviceId);
		return ResponseEntity.ok("Device soft deleted successfully");
	}

	@DeleteMapping("/hard-delete/{deviceId}")
	public ResponseEntity<String> hardDeleteDevice(@PathVariable Long deviceId) {
		deviceService.hardDeleteDevice(deviceId);
		return ResponseEntity.ok("Device permanently deleted");
	}

	@GetMapping("/recycle-bin")
	public ResponseEntity<PagedDeviceResponseDto> getDeletedDevices(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return ResponseEntity.ok(deviceService.getDeletedDevices(page, size));
	}

	@PutMapping("/{deviceId}/restore")
	public ResponseEntity<String> restoreDevice(@PathVariable Long deviceId) {
		deviceService.restoreDevice(deviceId);
		return ResponseEntity.ok("Device restored successfully");
	}

	@GetMapping("/update-form/{deviceId}")
	public ResponseEntity<DeviceUpdateFormResponseDto> getDeviceForUpdate(@PathVariable Long deviceId) {
		return ResponseEntity.ok(deviceService.getDeviceForUpdate(deviceId));
	}

	@PutMapping("/updateDevice/{deviceId}")
	public ResponseEntity<DeviceResponseDto> updateDevice(@PathVariable Long deviceId,
			@RequestBody @Valid UpdateDeviceRequestDto request) {
		return ResponseEntity.ok(deviceService.updateDevice(deviceId, request));
	}

	@PutMapping("/updateDeviceLocation/{deviceId}")
	public ResponseEntity<DeviceResponseDto> updateDeviceLocation(@PathVariable Long deviceId,
			@RequestBody @Valid UpdateDeviceLocationRequestDto request) {
		return ResponseEntity.ok(deviceService.updateDeviceLocation(deviceId, request));
	}

	@GetMapping("/getAudit/{deviceId}")
	public ResponseEntity<List<DeviceAuditResponseDto>> getDeviceAudit(@PathVariable Long deviceId) {
		return ResponseEntity.ok(deviceService.getDeviceAudit(deviceId));
	}

	@PutMapping("/assign-admin/{deviceId}")
	public ResponseEntity<String> assignAdminToDevice(@PathVariable Long deviceId,
			                                          @RequestBody AssignAdminRequestDto request) {
		deviceService.assignAdminToDevice(deviceId, request.getAdminId());
		return ResponseEntity.ok("Admin assigned successfully");
	}

}