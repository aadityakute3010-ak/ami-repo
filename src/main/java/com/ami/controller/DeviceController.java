
package com.ami.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.AssignAdminRequestDto;
import com.ami.dto.requests.AssignBillingTypeRequestDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceBulkUploadResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceMapMarkerDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.enums.DeleteType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import com.ami.service.DeviceService;
import com.ami.service.LocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devices")
public class DeviceController {

	private final DeviceService deviceService;
	private final LocationService locationService;

	@PostMapping("/createDevice")
	public ResponseEntity<List<DeviceResponseDto>> createDevices(@RequestBody @Valid CreateDevicesRequestDto request) {
		return ResponseEntity.ok(deviceService.createDevices(request));
	}

	@GetMapping("/source-types")
	public ResponseEntity<List<SourceType>> getAssignedSourceTypes() {
		return ResponseEntity.ok(deviceService.getAssignedSourceTypes());
	}

	@GetMapping("/getDevices")
	public ResponseEntity<PagedDeviceResponseDto> getDevices(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) DeviceStatus status, @RequestParam(required = false) SourceType sourceType,
			@RequestParam(required = false) TechnologyType technologyType,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {

		return ResponseEntity
				.ok(deviceService.getDevices(page, size, search, status, sourceType, technologyType, fromDate, toDate));
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

	@GetMapping("/deviceDetails/{deviceId}")
	public ResponseEntity<DeviceDetailsResponseDto> getDeviceDetails(@PathVariable Long deviceId) {
		return ResponseEntity.ok(deviceService.getDeviceDetails(deviceId));
	}

	@DeleteMapping("/deleteDevice/{deviceId}")
	public ResponseEntity<String> deleteDevice(@PathVariable Long deviceId, @RequestParam DeleteType deleteType,
			@RequestParam(required = false) DeviceStatus status) {
		return ResponseEntity.ok(deviceService.deleteDevice(deviceId, deleteType, status));
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

	@GetMapping("/getAudit/{deviceId}")
	public ResponseEntity<List<DeviceAuditResponseDto>> getDeviceAudit(@PathVariable Long deviceId) {
		return ResponseEntity.ok(deviceService.getDeviceAudit(deviceId));
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	@PutMapping("/assign-admin/{deviceId}")
	public ResponseEntity<String> assignAdminToDevice(@PathVariable Long deviceId,
			@RequestBody AssignAdminRequestDto request) {
		deviceService.assignAdminToDevice(deviceId, request.getAdminId());
		return ResponseEntity.ok("Admin assigned successfully");
	}

	@GetMapping("/dashboard")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
	public DeviceDashboardResponseDto getDeviceDashboard() {
		return deviceService.getDeviceDashboard();
	}

	@PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public DeviceBulkUploadResponseDto bulkUploadDevices(@RequestParam("file") MultipartFile file) {
		return deviceService.bulkUploadDevices(file);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
	@GetMapping("/export")
	public ResponseEntity<byte[]> exportDevices(@RequestParam(defaultValue = "csv") String fileType,
			@RequestParam(required = false) String search, @RequestParam(required = false) DeviceStatus status,
			@RequestParam(required = false) SourceType sourceType,
			@RequestParam(required = false) TechnologyType technologyType,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {

		ExportFileResponseDto exportFile = deviceService.exportDevices(fileType, search, status, sourceType,
				technologyType, fromDate, toDate);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + exportFile.getFileName())
				.contentType(MediaType.parseMediaType(exportFile.getContentType())).body(exportFile.getFile());
	}

	@GetMapping("/map-markers")
	public ResponseEntity<List<DeviceMapMarkerDto>> getDeviceMapMarkers() {
		return ResponseEntity.ok(deviceService.getDeviceMapMarkers());
	}

	@PostMapping("/locations/backfill/devices")
	public ResponseEntity<String> backfillDeviceLocations() {
		locationService.backfillDeviceLocations();
		return ResponseEntity.ok("Device locations backfilled successfully");
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	@PutMapping("/assign-billing-type/{deviceId}")
	public ResponseEntity<DeviceResponseDto> assignBillingType(@PathVariable Long deviceId,
			@RequestBody @Valid AssignBillingTypeRequestDto request) {

		return ResponseEntity.ok(deviceService.assignBillingType(deviceId, request));
	}

}