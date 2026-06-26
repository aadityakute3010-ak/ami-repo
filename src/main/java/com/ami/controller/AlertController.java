package com.ami.controller;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ami.dto.requests.CreateAlertRequestDto;
import com.ami.dto.requests.UpdateAlertRequestDto;
import com.ami.dto.responses.AlertHistoryResponseDto;
import com.ami.dto.responses.AlertResponseDto;
import com.ami.dto.responses.AlertSummaryResponseDto;
import com.ami.enums.AlertStatus;
import com.ami.service.AlertService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(
            AlertService alertService) {

        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertResponseDto> getAllAlerts(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String source,

            @RequestParam(required = false)
            String severity) {

        return alertService.getAllAlerts(
                search,
                status,
                source,
                severity);
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
            int limit) {

        return alertService
                .getAlertsWithPagination(
                        page,
                        limit);
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{id}/history")
    public List<AlertHistoryResponseDto> getAlertHistory(
            @PathVariable Long id) {

        return alertService.getAlertHistory(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
   

    @PostMapping("/import")
    public String importAlerts(
            @RequestParam("file")
            MultipartFile file) {

        return alertService.importAlerts(file);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<byte[]>
    exportAlerts() {

        byte[] data =
                alertService.exportAlerts();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=alerts.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}