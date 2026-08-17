package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.responses.DashboardChartResponseDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceHealthResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.dto.responses.GasAlarmResponseDto;
import com.ami.dto.responses.GasAnalyticsResponseDto;
import com.ami.dto.responses.GasDashboardResponseDto;
import com.ami.dto.responses.GasHistoryResponseDto;
import com.ami.dto.responses.GasLeakResponseDto;
import com.ami.dto.responses.GasLiveTelemetryResponseDto;
import com.ami.dto.responses.GasQualityResponseDto;
import com.ami.dto.responses.GasSummaryResponseDto;
import com.ami.enums.SourceType;
import com.ami.dto.requests.ReportFilterRequestDto;
import com.ami.dto.responses.DeviceLogResponseDto;
import com.ami.dto.responses.LeakSummaryResponseDto;
import com.ami.dto.responses.MapLocationResponseDto;
import com.ami.dto.responses.PumpStatusResponseDto;
import com.ami.dto.responses.ReportResponseDto;
import com.ami.dto.responses.TankLevelResponseDto;
import com.ami.dto.responses.TimelineResponseDto;
import com.ami.dto.responses.ValveStatusResponseDto;
import com.ami.dto.responses.WaterAnalyticsResponseDto;
import com.ami.dto.responses.WaterDashboardResponseDto;
import com.ami.dto.responses.WaterHistoryResponseDto;
import com.ami.dto.responses.WaterLiveTelemetryResponseDto;
import com.ami.dto.responses.WaterQualityResponseDto;
import com.ami.dto.responses.WaterSummaryResponseDto;
public interface DeviceTelemetryService {

    DeviceTelemetryResponseDto createTelemetry(
            CreateDeviceTelemetryRequestDto request);
    Page<DeviceTelemetryResponseDto> getAllTelemetry(
            int page,
            int size,
            String search,
            SourceType sourceType,
            Boolean online,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String sortBy,
            String direction);
   
    List<DeviceTelemetryResponseDto> getTelemetryByDeviceId(
            String deviceId,
            SourceType sourceType);

    DeviceTelemetryResponseDto getLatestTelemetry(
            String deviceId,
            SourceType sourceType);
    
    DeviceDashboardResponseDto
    getDashboard(
            SourceType sourceType);
    
    String refreshDashboard();
    
    DeviceTelemetryResponseDto getTelemetryById(
            Long id);

    DeviceAnalyticsResponseDto
    getAnalytics(
            SourceType sourceType);

    String archiveTelemetry(
            Long telemetryId,
            SourceType sourceType,
            String archiveReason);

    String restoreTelemetry(
            Long archivedTelemetryId);
    
    List<DeviceTelemetryResponseDto>
    getBySourceType(
            SourceType sourceType);

    List<DeviceTelemetryResponseDto>
    getOnlineDevices();

    List<DeviceTelemetryResponseDto>
    getOfflineDevices();

    byte[] exportTelemetry(
            String format,
            SourceType sourceType,
            String reportType);
    
    Page<DeviceTelemetryResponseDto> getTelemetryHistory(

            int page,

            int size,

            SourceType sourceType,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction);

    Page<DeviceTelemetryResponseDto> getDeviceHistory(

            String deviceId,

            int page,

            int size,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction);
    
    DashboardChartResponseDto getConsumptionChart(
            SourceType sourceType,
            String period);
    
    DashboardChartResponseDto getFlowRateTrend(
            SourceType sourceType);
    
    DashboardChartResponseDto getPressureTrend(
            SourceType sourceType);
    
    DashboardChartResponseDto getSignalTrend(
            SourceType sourceType);
    
    List<TimelineResponseDto> getTimeline(
            String deviceId);
    
    ReportResponseDto generateReport(
            ReportFilterRequestDto request);
    
    DeviceHealthResponseDto getDeviceHealth(
            String deviceId);
    
    // gas module 
    
    GasDashboardResponseDto getGasDashboard();

    GasSummaryResponseDto getGasSummary();

    List<GasLiveTelemetryResponseDto> getGasLiveTelemetry();

    GasLiveTelemetryResponseDto getGasLiveTelemetry(
            String deviceId);

    List<GasHistoryResponseDto> getGasHistory(
            String deviceId);

    GasAnalyticsResponseDto getGasAnalytics();

    GasAnalyticsResponseDto getGasAnalytics(
            String deviceId);

    GasQualityResponseDto getGasQuality(
            String deviceId);

    List<GasLeakResponseDto> getGasLeaks();

    List<GasAlarmResponseDto> getGasAlarms();

    List<DeviceLogResponseDto> getGasLogs(
            String deviceId);
    
    DeviceTelemetryResponseDto refreshDevice(
            String deviceId,
            SourceType sourceType);
    
    DeviceTelemetryResponseDto retryDevice(
            String deviceId,
            SourceType sourceType);
    
    
 // ==========================================
 // Water Module
 // ==========================================

 WaterDashboardResponseDto getDashboard();

 WaterSummaryResponseDto getSummary();
 


 List<WaterLiveTelemetryResponseDto> getLiveTelemetry();

 WaterLiveTelemetryResponseDto getLiveTelemetry(
         String deviceId);

 List<WaterHistoryResponseDto> getHistory(
         String deviceId);

 WaterAnalyticsResponseDto getAnalytics();

 WaterAnalyticsResponseDto getAnalytics(
         String deviceId);

 WaterQualityResponseDto getQuality(
         String deviceId);

 List<TankLevelResponseDto> getTankLevels();

 List<PumpStatusResponseDto> getPumpStatus();

 List<ValveStatusResponseDto> getValveStatus();

 List<LeakSummaryResponseDto> getLeaks();

 List<DeviceLogResponseDto> getLogs(
         String deviceId);

 List<MapLocationResponseDto> getDeviceLocations(
	        SourceType sourceType);

	List<MapLocationResponseDto> getLeakLocations(
	        SourceType sourceType);

	List<MapLocationResponseDto> getPipelineLocations(
	        SourceType sourceType);

	List<MapLocationResponseDto> getZoneMonitoring(
	        SourceType sourceType);

}