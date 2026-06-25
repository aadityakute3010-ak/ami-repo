package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateAlertRequestDto;
import com.ami.dto.requests.UpdateAlertRequestDto;
import com.ami.dto.responses.AlertHistoryResponseDto;
import com.ami.dto.responses.AlertResponseDto;
import com.ami.dto.responses.AlertSummaryResponseDto;
import com.ami.enums.AlertStatus;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AlertService {

	List<AlertResponseDto> getAllAlerts(
	        String search,
	        String status,
	        String source,
	        String severity);

    AlertResponseDto getAlertById(Long id);

    AlertResponseDto createAlert(
            CreateAlertRequestDto request);

    AlertResponseDto updateAlert(
            Long id,
            UpdateAlertRequestDto request);

    String toggleAlert(Long id);

    String resetAlerts();
    
    AlertSummaryResponseDto getSummary();
    
    String deleteAlert(Long id);
    
    Page<AlertResponseDto> getAlertsWithPagination(
            int page,
            int limit);
    
    String updateAlertStatus(
            Long id,
            AlertStatus status);
    
    List<AlertHistoryResponseDto>
    getAlertHistory(Long alertId);
    
    String importAlerts(
            MultipartFile file);
    
    byte[] exportAlerts();
}