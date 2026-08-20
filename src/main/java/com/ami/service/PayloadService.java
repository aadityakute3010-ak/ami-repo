package com.ami.service;

import java.util.List;
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

public interface PayloadService {

	// =====================================================
	// Dashboard
	// =====================================================

	PayloadStatsDTO getStats();

	// =====================================================
	// Payload Listing
	// =====================================================

	PagedPayloadResponseDto getPayloads(PayloadFilterRequest request);

	// =====================================================
	// Payload Detail
	// =====================================================

	PayloadDetailDTO getPayloadDetail(Long payloadId);

	// =====================================================
	// 24 Hour Readings
	// =====================================================

	DailyReadingResponseDTO get24HourReadings(Long deviceId, String date);

	// =====================================================
	// Consumption Trend
	// =====================================================

	List<ConsumptionTrendDTO> getConsumptionTrend(Long deviceId, String fromDate, String toDate);

	// =====================================================
	// Telemetry Ingestion
	// =====================================================

	void receivePayload(TelemetryIngestRequest request);

	// =====================================================
	// Delete Payload
	// =====================================================

	void deletePayload(Long payloadId);

	PayloadAnalyticsResponseDTO getPayloadAnalytics();
	PayloadSourceSummaryDTO getPayloadSourceSummary();
	PagedDevicePayloadHistoryDTO getDevicePayloadHistory(Long deviceId, int page, int size);
	List<PayloadLogDTO> getPayloadLogs(Long payloadId);

	void retryPayload(Long payloadId);

	PayloadExportResponse exportPayloads(PayloadFilterRequest request, String format);
	
	void bulkDeletePayloads(List<Long> payloadIds);

}