package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.ami.enums.SourceType;
import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.requests.ReportFilterRequestDto;
import com.ami.dto.responses.DashboardChartResponseDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceHealthResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.service.DeviceTelemetryService;
import com.ami.dto.responses.DeviceLogResponseDto;
import com.ami.dto.responses.LeakSummaryResponseDto;
import com.ami.dto.responses.MapLocationResponseDto;
import com.ami.dto.responses.PumpStatusResponseDto;
import com.ami.dto.responses.ReportResponseDto;
import com.ami.dto.responses.TankLevelResponseDto;
import com.ami.dto.responses.TimelineResponseDto;
import com.ami.dto.responses.ValveStatusResponseDto;
import com.ami.dto.responses.WaterAnalyticsResponseDto;
import com.ami.dto.responses.WaterHistoryResponseDto;
import com.ami.dto.responses.WaterLiveTelemetryResponseDto;
import com.ami.dto.responses.WaterQualityResponseDto;
import com.ami.dto.responses.WaterSummaryResponseDto;
import com.ami.dto.responses.GasAlarmResponseDto;
import com.ami.dto.responses.GasAnalyticsResponseDto;
import com.ami.dto.responses.GasDashboardResponseDto;
import com.ami.dto.responses.GasHistoryResponseDto;
import com.ami.dto.responses.GasLeakResponseDto;
import com.ami.dto.responses.GasLiveTelemetryResponseDto;
import com.ami.dto.responses.GasQualityResponseDto;
import com.ami.dto.responses.GasSummaryResponseDto;
@RestController
@RequestMapping("/api/telemetry")
public class DeviceTelemetryController {

    private final DeviceTelemetryService service;

    public DeviceTelemetryController(
            DeviceTelemetryService service) {

        this.service = service;
    }

    @PostMapping
    public DeviceTelemetryResponseDto createTelemetry(
            @RequestBody CreateDeviceTelemetryRequestDto request) {

        return service.createTelemetry(request);
    }

    @GetMapping
    public Page<DeviceTelemetryResponseDto> getAllTelemetry(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            SourceType sourceType,

            @RequestParam(required = false)
            Boolean online,

            @RequestParam(required = false)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            LocalDateTime toDate,

            @RequestParam(defaultValue = "readingTime")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getAllTelemetry(
                page,
                size,
                search,
                sourceType,
                online,
                fromDate,
                toDate,
                sortBy,
                direction);
    }

    @GetMapping("/{id}")
    public DeviceTelemetryResponseDto getTelemetryById(
            @PathVariable Long id) {

        return service.getTelemetryById(id);
    }
    @GetMapping("/device/{deviceId}")
    public List<DeviceTelemetryResponseDto> getTelemetryByDeviceId(
            @PathVariable String deviceId,
            @RequestParam SourceType sourceType) {

        return service.getTelemetryByDeviceId(
                deviceId,
                sourceType);
    }
    @GetMapping("/latest/{deviceId}")
    public ResponseEntity<DeviceTelemetryResponseDto> getLatestTelemetry(
            @PathVariable String deviceId,
            @RequestParam SourceType sourceType) {

        return ResponseEntity.ok(
                service.getLatestTelemetry(
                        deviceId,
                        sourceType));
    }
    @GetMapping("/dashboard")
    public DeviceDashboardResponseDto
    getDashboard(

            @RequestParam(required = false)
            SourceType sourceType) {

        return service.getDashboard(
                sourceType);
    }

    @GetMapping("/analytics")
    public DeviceAnalyticsResponseDto
    getAnalytics(

            @RequestParam(required = false)
            SourceType sourceType) {

        return service.getAnalytics(
                sourceType);
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/archive/{telemetryId}")
    public ResponseEntity<String> archiveTelemetry(
            @PathVariable Long telemetryId,
            @RequestParam SourceType sourceType,
            @RequestParam(required = false) String archiveReason) {

        return ResponseEntity.ok(
                service.archiveTelemetry(
                        telemetryId,
                        sourceType,
                        archiveReason));
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/restore/{archivedTelemetryId}")
    public ResponseEntity<String> restoreTelemetry(
            @PathVariable Long archivedTelemetryId) {

        return ResponseEntity.ok(
                service.restoreTelemetry(
                        archivedTelemetryId));
    }
    @GetMapping("/source/{sourceType}")
    public List<DeviceTelemetryResponseDto>
    getBySourceType(
            @PathVariable SourceType sourceType){

        return service.getBySourceType(
                sourceType);
    }
    @GetMapping("/online")
    public List<DeviceTelemetryResponseDto>
    getOnlineDevices(){

        return service.getOnlineDevices();
    }
    
    @GetMapping("/offline")
    public List<DeviceTelemetryResponseDto>
    getOfflineDevices(){

        return service.getOfflineDevices();
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]>
    exportTelemetry(

            @RequestParam(defaultValue = "csv")
            String format,

            @RequestParam(required = false)
            SourceType sourceType,

            @RequestParam(defaultValue = "ALL")
            String reportType){

    	byte[] data =
    	        service.exportTelemetry(
    	                format,
    	                sourceType,
    	                reportType);

        String extension =
                switch(format.toLowerCase()){

                    case "excel" -> "xlsx";

                    case "pdf" -> "pdf";

                    default -> "csv";
                };

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=telemetry."
                                + extension)
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
    @GetMapping("/history")
    public Page<DeviceTelemetryResponseDto> getTelemetryHistory(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            SourceType sourceType,

            @RequestParam(required = false)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            LocalDateTime toDate,

            @RequestParam(defaultValue = "readingTime")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getTelemetryHistory(

                page,

                size,

                sourceType,

                fromDate,

                toDate,

                sortBy,

                direction);
    }
    
    @GetMapping("/history/device/{deviceId}")
    public Page<DeviceTelemetryResponseDto> getDeviceHistory(

            @PathVariable
            String deviceId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            LocalDateTime toDate,

            @RequestParam(defaultValue = "readingTime")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getDeviceHistory(

                deviceId,

                page,

                size,

                fromDate,

                toDate,

                sortBy,

                direction);
    }
    @GetMapping("/consumption-chart")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<DashboardChartResponseDto> getConsumptionChart(

            @RequestParam(required = false)
            SourceType sourceType,

            @RequestParam(defaultValue = "MONTHLY")
            String period) {

        return ResponseEntity.ok(
                service.getConsumptionChart(
                        sourceType,
                        period));
    }
   
    @GetMapping("/dashboard/charts/flow-rate")
    public DashboardChartResponseDto getFlowRateTrend(

            @RequestParam(required = false)
            SourceType sourceType) {

        return service.getFlowRateTrend(
                sourceType);
    }
    
    @GetMapping("/dashboard/charts/pressure")
    public DashboardChartResponseDto getPressureTrend(

            @RequestParam(required = false)
            SourceType sourceType) {

        return service.getPressureTrend(
                sourceType);
    }
    
    @GetMapping("/dashboard/charts/signal")
    public DashboardChartResponseDto getSignalTrend(

            @RequestParam(required = false)
            SourceType sourceType) {

        return service.getSignalTrend(
                sourceType);
    }
    
    @GetMapping("/{deviceId}/timeline")
    public List<TimelineResponseDto> getTimeline(
            @PathVariable String deviceId) {

        return service.getTimeline(deviceId);
    }
    
    @PostMapping("/reports")
    public ReportResponseDto generateReport(
            @RequestBody ReportFilterRequestDto request) {

        return service.generateReport(request);
    }
    
    @GetMapping("/health/{deviceId}")
    public DeviceHealthResponseDto getDeviceHealth(
            @PathVariable String deviceId) {

        return service.getDeviceHealth(
                deviceId);
    }
    
 // ======================================================
 // Water Module
 // ======================================================

 @GetMapping("/summary")
 public WaterSummaryResponseDto getSummary() {

     return service.getSummary();
 }

 @GetMapping("/live")
 public List<WaterLiveTelemetryResponseDto> getLiveTelemetry() {

     return service.getLiveTelemetry();
 }

 @GetMapping("/live/{deviceId}")
 public WaterLiveTelemetryResponseDto getLiveTelemetry(
         @PathVariable String deviceId) {

     return service.getLiveTelemetry(deviceId);
 }

 @GetMapping("/water/history/{deviceId}")
 public List<WaterHistoryResponseDto> getWaterHistory(
         @PathVariable String deviceId) {

     return service.getHistory(deviceId);
 }

 @GetMapping("/water/analytics/{deviceId}")
 public WaterAnalyticsResponseDto getWaterAnalytics(
         @PathVariable String deviceId) {

     return service.getAnalytics(deviceId);
 }

 @GetMapping("/quality/{deviceId}")
 public WaterQualityResponseDto getQuality(
         @PathVariable String deviceId) {

     return service.getQuality(deviceId);
 }

 @GetMapping("/tank-levels")
 public List<TankLevelResponseDto> getTankLevels() {

     return service.getTankLevels();
 }

 @GetMapping("/pump-status")
 public List<PumpStatusResponseDto> getPumpStatus() {

     return service.getPumpStatus();
 }

 @GetMapping("/valve-status")
 public List<ValveStatusResponseDto> getValveStatus() {

     return service.getValveStatus();
 }

 @GetMapping("/leaks")
 public List<LeakSummaryResponseDto> getLeaks() {

     return service.getLeaks();
 }

 @GetMapping("/logs/{deviceId}")
 public List<DeviceLogResponseDto> getLogs(
         @PathVariable String deviceId) {

     return service.getLogs(deviceId);
 }
//=====================================================
//Gas Module
//=====================================================

@GetMapping("/gas/dashboard")
public ResponseEntity<GasDashboardResponseDto> getGasDashboard() {

	return ResponseEntity.ok(
	        service.getGasDashboard());
}

@GetMapping("/gas/summary")
public ResponseEntity<GasSummaryResponseDto> getGasSummary() {

  return ResponseEntity.ok(
          service.getGasSummary());
}

@GetMapping("/gas/live")
public ResponseEntity<List<GasLiveTelemetryResponseDto>> getGasLiveTelemetry() {

  return ResponseEntity.ok(
          service.getGasLiveTelemetry());
}

@GetMapping("/gas/live/{deviceId}")
public ResponseEntity<GasLiveTelemetryResponseDto> getGasLiveTelemetry(
      @PathVariable String deviceId) {

  return ResponseEntity.ok(
          service.getGasLiveTelemetry(deviceId));
}

@GetMapping("/gas/history/{deviceId}")
public ResponseEntity<List<GasHistoryResponseDto>> getGasHistory(
      @PathVariable String deviceId) {

  return ResponseEntity.ok(
          service.getGasHistory(deviceId));
}

@GetMapping("/gas/analytics")
public ResponseEntity<GasAnalyticsResponseDto> getGasAnalytics() {

  return ResponseEntity.ok(
          service.getGasAnalytics());
}

@GetMapping("/gas/analytics/{deviceId}")
public ResponseEntity<GasAnalyticsResponseDto> getGasAnalytics(
      @PathVariable String deviceId) {

  return ResponseEntity.ok(
          service.getGasAnalytics(deviceId));
}

@GetMapping("/gas/quality/{deviceId}")
public ResponseEntity<GasQualityResponseDto> getGasQuality(
      @PathVariable String deviceId) {

  return ResponseEntity.ok(
          service.getGasQuality(deviceId));
}

@GetMapping("/gas/leaks")
public ResponseEntity<List<GasLeakResponseDto>> getGasLeaks() {

  return ResponseEntity.ok(
          service.getGasLeaks());
}

@GetMapping("/gas/alarms")
public ResponseEntity<List<GasAlarmResponseDto>> getGasAlarms() {

  return ResponseEntity.ok(
          service.getGasAlarms());
}

@GetMapping("/gas/logs/{deviceId}")
public ResponseEntity<List<DeviceLogResponseDto>> getGasLogs(
      @PathVariable String deviceId) {

  return ResponseEntity.ok(
          service.getGasLogs(deviceId));
}
@PostMapping("/dashboard/refresh")
public ResponseEntity<String> refreshDashboard() {

    return ResponseEntity.ok(
            service.refreshDashboard());
}
@PostMapping("/{deviceId}/refresh")
public ResponseEntity<DeviceTelemetryResponseDto> refreshDevice(

        @PathVariable String deviceId,

        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.refreshDevice(
                    deviceId,
                    sourceType));
}
@PostMapping("/{deviceId}/retry")
public ResponseEntity<DeviceTelemetryResponseDto> retryDevice(

        @PathVariable String deviceId,

        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.retryDevice(
                    deviceId,
                    sourceType));
}
@GetMapping("/maps/devices")
public ResponseEntity<List<MapLocationResponseDto>> getDeviceLocations(
        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.getDeviceLocations(sourceType));
}
@GetMapping("/maps/leaks")
public ResponseEntity<List<MapLocationResponseDto>> getLeakLocations(
        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.getLeakLocations(sourceType));
}
@GetMapping("/maps/pipelines")
public ResponseEntity<List<MapLocationResponseDto>> getPipelineLocations(
        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.getPipelineLocations(sourceType));
}
@GetMapping("/maps/zones")
public ResponseEntity<List<MapLocationResponseDto>> getZoneMonitoring(
        @RequestParam SourceType sourceType) {

    return ResponseEntity.ok(
            service.getZoneMonitoring(sourceType));
}
}