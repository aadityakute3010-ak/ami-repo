package com.ami.service;

import java.time.LocalDate;

import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.requests.TelemetryRequestDto;
import com.ami.dto.responses.TelemetryHistoryResponseDto;
import com.ami.dto.responses.TelemetryResponseDto;

public interface TelemetryService {

	void saveTelemetry(TelemetryRequestDto request);

	TelemetryResponseDto getLatestTelemetry(Long deviceId);

	TelemetryHistoryResponseDto getTelemetryHistory(Long deviceId, LocalDate from, LocalDate to);
	
	void saveTelemetryFromIngest(TelemetryIngestRequest request); 

}
 