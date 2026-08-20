package com.ami.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.ami.dto.requests.BulkDeletePayloadRequest;
import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.responses.ConsumptionTrendDTO;
import com.ami.dto.responses.DailyReadingResponseDTO;
import com.ami.dto.responses.PagedDevicePayloadHistoryDTO;
import com.ami.dto.responses.PagedPayloadResponseDto;
import com.ami.dto.responses.PayloadAnalyticsResponseDTO;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadExportResponse;
import com.ami.dto.responses.PayloadLogDTO;
import com.ami.dto.responses.PayloadSourceSummaryDTO;
import com.ami.dto.responses.PayloadStatsDTO;
import com.ami.service.PayloadService;

import jakarta.validation.Valid;

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
	public ResponseEntity<PagedPayloadResponseDto> getPayloads(PayloadFilterRequest request) {
		return ResponseEntity.ok(payloadService.getPayloads(request));
	}

	// Overview Modal
	@GetMapping("/getPayloadDetails/{payloadId}")
	public ResponseEntity<PayloadDetailDTO> getPayloadDetail(@PathVariable Long payloadId) {
		return ResponseEntity.ok(payloadService.getPayloadDetail(payloadId));
	}

	// 24 Hour Reading Tab
	@GetMapping("/get24hoursReading/{deviceId}")
	public ResponseEntity<DailyReadingResponseDTO> get24HourReadings(@PathVariable Long deviceId,
			@RequestParam String date) {

		return ResponseEntity.ok(payloadService.get24HourReadings(deviceId, date));
	}

	// Consumption Trend Chart
	@GetMapping("/ConsumptionTrend/{deviceId}")
	public ResponseEntity<List<ConsumptionTrendDTO>> getConsumptionTrend(@PathVariable Long deviceId,
			@RequestParam String fromDate, @RequestParam String toDate) {
		return ResponseEntity.ok(payloadService.getConsumptionTrend(deviceId, fromDate, toDate));
	}

	// Delete Payload
	@DeleteMapping("/deletePayload/{payloadId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<String> deletePayload(@PathVariable Long payloadId) {

		payloadService.deletePayload(payloadId);

		return ResponseEntity.ok("Payload deleted successfully");
	}

	// Payload Analytics
	@GetMapping("/analytics")
	public ResponseEntity<PayloadAnalyticsResponseDTO> getPayloadAnalytics() {
		return ResponseEntity.ok(payloadService.getPayloadAnalytics());
	}

	// Payload Source Summary
	@GetMapping("/source-summary")
	public ResponseEntity<PayloadSourceSummaryDTO> getPayloadSourceSummary() {
		return ResponseEntity.ok(payloadService.getPayloadSourceSummary());
	}

	// =====================================================
	// Device Payload History
	// =====================================================

	@GetMapping("/device/{deviceId}/history")
	public ResponseEntity<PagedDevicePayloadHistoryDTO> getDevicePayloadHistory(@PathVariable Long deviceId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(payloadService.getDevicePayloadHistory(deviceId, page, size));
	}

	// =====================================================
	// Payload Processing Logs
	// =====================================================

	@GetMapping("/{payloadId}/logs")
	public ResponseEntity<List<PayloadLogDTO>> getPayloadLogs(@PathVariable Long payloadId) {

		return ResponseEntity.ok(payloadService.getPayloadLogs(payloadId));
	}

	// =====================================================
	// Retry Failed Payload
	// =====================================================

	@PostMapping("/{payloadId}/retry")
	public ResponseEntity<Void> retryPayload(@PathVariable Long payloadId) {

		payloadService.retryPayload(payloadId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> exportPayloads(@ModelAttribute PayloadFilterRequest request,
			@RequestParam String format) {
		PayloadExportResponse export = payloadService.exportPayloads(request, format);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + export.getFileName() + "\"")
				.contentType(MediaType.parseMediaType(export.getContentType())).contentLength(export.getData().length)
				.body(export.getData());
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@DeleteMapping("/bulk-delete")
	public ResponseEntity<String> bulkDeletePayloads(@Valid @RequestBody BulkDeletePayloadRequest request) {

		payloadService.bulkDeletePayloads(request.getPayloadIds());

		return ResponseEntity.ok(request.getPayloadIds().size() + " payloads deleted successfully");
	}

}