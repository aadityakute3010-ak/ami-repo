package com.ami.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.responses.HourlyReadingDTO;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadStatsDTO;
import com.ami.dto.responses.PayloadSummaryDTO;
import com.ami.service.PayloadService;

@RestController
@RequestMapping("/api/payloads")
@RequiredArgsConstructor
public class PayloadController {

	private final PayloadService payloadService;
	
	@PostMapping("/ingest")
	public ResponseEntity<String> ingestPayload(@RequestBody TelemetryIngestRequest request) {
		payloadService.receivePayload(request);
		return ResponseEntity.ok("Payload received successfully");
	} 

	// Dashboard Cards
	@GetMapping("/payloadStats")
	public ResponseEntity<PayloadStatsDTO> getStats() {
		return ResponseEntity.ok(payloadService.getStats());
	} 

	// Main Table
	@GetMapping("/getPayloads")
	public ResponseEntity<Page<PayloadSummaryDTO>> getPayloads(PayloadFilterRequest request) {
		return ResponseEntity.ok(payloadService.getPayloads(request));
	} 

	// Overview Modal
	@GetMapping("/getPayloadDetails/{payloadId}")
	public ResponseEntity<PayloadDetailDTO> getPayloadDetail(@PathVariable Long payloadId) {
		return ResponseEntity.ok(payloadService.getPayloadDetail(payloadId));
	}
 
	// 24 Hour Reading Tab
	@GetMapping("/get24hoursReading/{deviceId}")
	public ResponseEntity<List<HourlyReadingDTO>> get24HourReadings(@PathVariable Long deviceId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(payloadService.get24HourReadings(deviceId, date));
	}

	// Consumption Trend Chart
	@GetMapping("/ConsumptionTrend/{deviceId}")
	public ResponseEntity<List<HourlyReadingDTO>> getConsumptionTrend(@PathVariable Long deviceId,
			                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(payloadService.getConsumptionTrend(deviceId, from, to));
	} 

	// Delete Payload
	@DeleteMapping("/deletePayload/{payloadId}")
	public ResponseEntity<Void> deletePayload(@PathVariable Long payloadId) {
		payloadService.deletePayload(payloadId);
		return ResponseEntity.noContent().build();
	}
}