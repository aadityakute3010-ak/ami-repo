package com.ami.controller;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ami.dto.requests.TelemetryRequestDto;
import com.ami.dto.responses.TelemetryHistoryResponseDto;
import com.ami.dto.responses.TelemetryResponseDto;
import com.ami.service.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/telemetry") 
@RequiredArgsConstructor
public class TelemetryController {

	private final TelemetryService telemetryService;

	@PostMapping("/saveTelemetry")
	public ResponseEntity<String> saveTelemetry(@Valid @RequestBody TelemetryRequestDto request) {
		telemetryService.saveTelemetry(request);
		return ResponseEntity.ok("Telemetry saved successfully");
	}

	@GetMapping("/getTelemetryData/{id}")
	public ResponseEntity<TelemetryResponseDto> getLatestTelemetry(@PathVariable Long id) {
		return ResponseEntity.ok(telemetryService.getLatestTelemetry(id));
	}

	@GetMapping("/history/{Id}")
	public ResponseEntity<TelemetryHistoryResponseDto> getTelemetryHistory(@PathVariable Long Id,
			@RequestParam LocalDate from, @RequestParam LocalDate to) {
		return ResponseEntity.ok(telemetryService.getTelemetryHistory(Id, from, to));
	}

	
 
}
