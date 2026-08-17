package com.ami.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ami.service.AlertService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.AlertArchiveRequestDto;
import com.ami.dto.requests.BulkAlertActionRequestDto;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(
            AlertService alertService) {

        this.alertService = alertService;
    }
    @GetMapping
    public ResponseEntity<Page<AlertResponseDto>> getAllAlerts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            AlertSeverity severity,

            @RequestParam(required = false)
            AlertCategory category,

            @RequestParam(required = false)
            AlertSource source,

            @RequestParam(required = false)
            Boolean enabled,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return ResponseEntity.ok(
                alertService.getAllAlerts(
                        page,
                        size,
                        search,
                        severity,
                        category,
                        source,
                        enabled,
                        sortBy,
                        direction));
    }
    @GetMapping("/{id}")
    public AlertResponseDto getAlertById(
            @PathVariable Long id) {

        return alertService.getAlertById(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public AlertResponseDto createAlert(
            @RequestBody CreateAlertRequestDto request) {

        return alertService.createAlert(request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public AlertResponseDto updateAlert(
            @PathVariable Long id,
            @RequestBody UpdateAlertRequestDto request) {

        return alertService.updateAlert(
                id,
                request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/enable")
    public String enableAlert(
            @PathVariable Long id) {

        return alertService.enableAlert(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/disable")
    public String disableAlert(
            @PathVariable Long id) {

        return alertService.disableAlert(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/bulk-disable")
    public String bulkDisableAlerts(
            @RequestBody List<Long> alertIds) {

        return alertService.bulkDisableAlerts(
                alertIds);
    }
    @PatchMapping("/{id}/toggle")
    public String toggleAlert(
            @PathVariable Long id) {

        return alertService.toggleAlert(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/reset")
    public String resetAlerts() {

        return alertService.resetAlerts();
    }
    
    @GetMapping("/summary")
    public AlertSummaryResponseDto getSummary() {

        return alertService.getSummary();
    }
    @GetMapping("/dashboard")
    public AlertSummaryResponseDto getDashboard() {

        return alertService.getDashboard();
    }
    
    @GetMapping("/alarm/dashboard")
    public AlarmDashboardResponseDto getAlarmDashboard() {

        return alertService.getAlarmDashboard();
    }
    
    @GetMapping("/alarm/severity")
    public AlarmSeverityResponseDto getAlarmSeverity() {

        return alertService.getAlarmSeverity();
    }
    @GetMapping("/{id}/alarm-history")
    public List<AlarmHistoryResponseDto> getAlarmHistory(
            @PathVariable Long id) {

        return alertService.getAlarmHistory(id);
    }
    @GetMapping("/alarm/category")
    public AlarmCategoryResponseDto getAlarmCategory() {

        return alertService.getAlarmCategory();
    }
    
    @GetMapping("/{id}/timeline")
    public List<AlarmTimelineResponseDto> getAlarmTimeline(
            @PathVariable Long id) {

        return alertService.getAlarmTimeline(id);
    }
    
    @GetMapping("/alarm/statistics")
    public AlarmStatisticsResponseDto getAlarmStatistics() {

        return alertService.getAlarmStatistics();
    }
    @PreAuthorize("hasRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteAlert(
            @PathVariable Long id) {

        return alertService.deleteAlert(id);
    }
    @GetMapping("/paged")
    public Page<AlertResponseDto> getAlertsWithPagination(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return alertService
                .getAlertsWithPagination(
                        page,
                        size);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<AlertResponseDto>> searchAlerts(

            @RequestParam
            String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                alertService.searchAlerts(
                        keyword,
                        page,
                        size));
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @PatchMapping("/{id}/status")
    public String updateAlertStatus(

            @PathVariable Long id,

            @RequestParam AlertStatus status) {

        return alertService
                .updateAlertStatus(
                        id,
                        status);
    }
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<AlertHistoryResponseDto>> getAlertHistory(

            @PathVariable
            Long id,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return ResponseEntity.ok(
                alertService.getAlertHistory(
                        id,
                        page,
                        size,
                        sortBy,
                        direction));
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
   @PostMapping("/import")
    public String importAlerts(
            @RequestParam("file")
            MultipartFile file) {

        return alertService.importAlerts(file);
    }
    @GetMapping("/source/{source}")
    public List<AlertResponseDto> getAlertsBySource(
            @PathVariable AlertSource source) {

        return alertService.getAlertsBySource(source);
    }
    @GetMapping("/severity/{severity}")
    public List<AlertResponseDto> getAlertsBySeverity(
            @PathVariable AlertSeverity severity) {

        return alertService.getAlertsBySeverity(severity);
    }
    @GetMapping("/category/{category}")
    public List<AlertResponseDto> getAlertsByCategory(
            @PathVariable AlertCategory category) {

        return alertService.getAlertsByCategory(category);
    }
    @GetMapping("/status/{status}")
    public List<AlertResponseDto> getAlertsByStatus(
            @PathVariable AlertStatus status) {

        return alertService.getAlertsByStatus(status);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/archive")
    public String archiveAlert(

            @PathVariable Long id,

            @RequestBody
            AlertArchiveRequestDto request) {

        return alertService.archiveAlert(
                id,
                request.getReason());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/restore")
    public String restoreAlert(

            @PathVariable Long id,

            @RequestBody
            AlertArchiveRequestDto request) {

        return alertService.restoreAlert(
                id,
                request.getReason());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/archive")
    public String bulkArchiveAlerts(

            @RequestBody
            BulkAlertActionRequestDto request) {

        return alertService.bulkArchiveAlerts(
                request.getAlertIds(),
                request.getReason());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/restore")
    public String bulkRestoreAlerts(

            @RequestBody
            BulkAlertActionRequestDto request) {

        return alertService.bulkRestoreAlerts(
                request.getAlertIds(),
                request.getReason());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/{id}/duplicate")
    public AlertResponseDto duplicateAlert(
            @PathVariable Long id) {

        return alertService.duplicateAlert(id);
    }
    @GetMapping("/recent")
    public List<AlertResponseDto> getRecentAlerts() {

        return alertService.getRecentAlerts();
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAlerts(

            @RequestParam(defaultValue = "csv")
            String format,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            AlertSeverity severity,

            @RequestParam(required = false)
            AlertCategory category,

            @RequestParam(required = false)
            AlertSource source,

            @RequestParam(required = false)
            Boolean enabled) {

        byte[] data =
                alertService.exportAlerts(
                        format,
                        search,
                        severity,
                        category,
                        source,
                        enabled);

        String extension =
                switch (format.toLowerCase()) {

                    case "excel" -> "xlsx";

                    case "pdf" -> "pdf";

                    default -> "csv";
                };

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=alerts."
                                + extension)
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}