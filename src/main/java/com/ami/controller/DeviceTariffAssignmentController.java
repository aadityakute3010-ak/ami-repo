package com.ami.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ami.dto.requests.AssignDeviceTariffRequest;
import com.ami.dto.responses.DeviceTariffAssignmentResponseDto;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.service.DeviceTariffAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devices/{deviceId}/billing/tariff")
@RequiredArgsConstructor
public class DeviceTariffAssignmentController {

	private final DeviceTariffAssignmentService assignmentService;

	@GetMapping("/applicable")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<List<TariffResponseDto>> getApplicableTariffs(@PathVariable Long deviceId) {

		return ResponseEntity.ok(assignmentService.getApplicableTariffs(deviceId));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<DeviceTariffAssignmentResponseDto> assignTariff(@PathVariable Long deviceId,
			@Valid @RequestBody AssignDeviceTariffRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assignTariff(deviceId, request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<DeviceTariffAssignmentResponseDto> getAssignedTariff(@PathVariable Long deviceId) {

		return ResponseEntity.ok(assignmentService.getAssignedTariff(deviceId));
	}

	@PutMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<DeviceTariffAssignmentResponseDto> updateAssignedTariff(@PathVariable Long deviceId,
			@Valid @RequestBody AssignDeviceTariffRequest request) {

		return ResponseEntity.ok(assignmentService.updateAssignedTariff(deviceId, request));
	}

	
	@DeleteMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<String> removeAssignedTariff(@PathVariable Long deviceId) {

		assignmentService.removeAssignedTariff(deviceId);

		return ResponseEntity.ok("Assigned Tariff Removed"); 
	}
}