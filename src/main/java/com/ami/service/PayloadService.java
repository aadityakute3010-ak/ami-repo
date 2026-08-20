package com.ami.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.responses.HourlyReadingDTO;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadStatsDTO;
import com.ami.dto.responses.PayloadSummaryDTO;

public interface PayloadService {

	// =====================================================
	// Dashboard
	// =====================================================

	PayloadStatsDTO getStats();

	// =====================================================
	// Payload Listing
	// =====================================================

	Page<PayloadSummaryDTO> getPayloads(PayloadFilterRequest request);

	// =====================================================
	// Payload Detail
	// =====================================================

	PayloadDetailDTO getPayloadDetail(Long payloadId);

	// =====================================================
	// 24 Hour Readings
	// =====================================================

	List<HourlyReadingDTO> get24HourReadings(Long deviceId, LocalDate date);

	// =====================================================
	// Consumption Trend
	// =====================================================

	List<HourlyReadingDTO> getConsumptionTrend(Long deviceId, LocalDate from, LocalDate to);

	// =====================================================
	// Telemetry Ingestion
	// =====================================================

	void receivePayload(TelemetryIngestRequest request);

	// ===================================================== 
	// Delete Payload
	// =====================================================

	void deletePayload(Long payloadId);
}