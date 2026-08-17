package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateAlertRequestDto;
import com.ami.dto.requests.UpdateAlertRequestDto;
import com.ami.dto.responses.AlarmCategoryResponseDto;
import com.ami.dto.responses.AlarmDashboardResponseDto;
import com.ami.dto.responses.AlarmHistoryResponseDto;
import com.ami.dto.responses.AlarmSeverityResponseDto;
import com.ami.dto.responses.AlarmStatisticsResponseDto;
import com.ami.dto.responses.AlarmTimelineResponseDto;
import com.ami.dto.responses.AlertHistoryResponseDto;
import com.ami.dto.responses.AlertResponseDto;
import com.ami.dto.responses.AlertSummaryResponseDto;
import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.AlertArchiveRequestDto;
import com.ami.dto.requests.BulkAlertActionRequestDto;
public interface AlertService {

	Page<AlertResponseDto> getAllAlerts(

	        int page,

	        int size,

	        String search,

	        AlertSeverity severity,

	        AlertCategory category,

	        AlertSource source,

	        Boolean enabled,

	        String sortBy,

	        String direction);
    AlertResponseDto getAlertById(Long id);

    AlertResponseDto createAlert(
            CreateAlertRequestDto request);

    AlertResponseDto updateAlert(
            Long id,
            UpdateAlertRequestDto request);

    String toggleAlert(Long id);
    
    String enableAlert(Long id);
    
    String disableAlert(Long id);
    
    String bulkDisableAlerts(
            List<Long> alertIds);

    String resetAlerts();
    
    AlarmDashboardResponseDto getAlarmDashboard();
    
    AlertSummaryResponseDto getSummary();
    
    AlertSummaryResponseDto getDashboard();
    
    AlarmStatisticsResponseDto getAlarmStatistics();
    
    List<AlarmTimelineResponseDto> getAlarmTimeline(
            Long alertId);
    
    String deleteAlert(Long id);
    
    List<AlarmHistoryResponseDto> getAlarmHistory(
            Long alertId);
    
    Page<AlertResponseDto> getAlertsWithPagination(
            int page,
            int size);
    
    AlarmCategoryResponseDto getAlarmCategory();
    
    AlarmSeverityResponseDto getAlarmSeverity(); 
    
    String updateAlertStatus(
            Long id,
            AlertStatus status);
    
    Page<AlertHistoryResponseDto> getAlertHistory(

            Long alertId,

            int page,

            int size,

            String sortBy,

            String direction);
    
    String importAlerts(
            MultipartFile file);
    
    Page<AlertResponseDto> searchAlerts(
            String keyword,
            int page,
            int size);
    
    List<AlertResponseDto> getAlertsBySource(
            AlertSource source);

    List<AlertResponseDto> getAlertsBySeverity(
            AlertSeverity severity);

    List<AlertResponseDto> getAlertsByCategory(
            AlertCategory category);

    List<AlertResponseDto> getAlertsByStatus(
            AlertStatus status);
    
    String archiveAlert(
            Long id,
            String reason);

    String restoreAlert(
            Long id,
            String reason);

    String bulkArchiveAlerts(
            List<Long> alertIds,
            String reason);

    String bulkRestoreAlerts(
            List<Long> alertIds,
            String reason);
    
    AlertResponseDto duplicateAlert(Long id);
    
    List<AlertResponseDto> getRecentAlerts();
    
    byte[] exportAlerts(
            String format,
            String search,
            AlertSeverity severity,
            AlertCategory category,
            AlertSource source,
            Boolean enabled);
}