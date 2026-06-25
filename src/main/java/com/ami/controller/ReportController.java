package com.ami.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.responses.ReportSummaryResponseDto;
import com.ami.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService) {

        this.reportService = reportService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/revenue")
    public ReportSummaryResponseDto getRevenueReport() {

        return reportService.getRevenueReport();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/collection")
    public ReportSummaryResponseDto getCollectionReport() {

        return reportService.getCollectionReport();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/pending")
    public ReportSummaryResponseDto getPendingReport() {

        return reportService.getPendingReport();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/overdue")
    public ReportSummaryResponseDto getOverdueReport() {

        return reportService.getOverdueReport();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/recharge")
    public ReportSummaryResponseDto getRechargeReport() {

        return reportService.getRechargeReport();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/revenue/export")
    public ResponseEntity<byte[]> exportRevenueReport() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=revenue-report.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(
                        reportService.exportRevenueReport());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/collection/export")
    public ResponseEntity<byte[]>
    exportCollectionReport() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=collection-report.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(
                        reportService.exportCollectionReport());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/pending/export")
    public ResponseEntity<byte[]>
    exportPendingReport() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=pending-report.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(
                        reportService.exportPendingReport());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/overdue/export")
    public ResponseEntity<byte[]>
    exportOverdueReport() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=overdue-report.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(
                        reportService.exportOverdueReport());
    }
}