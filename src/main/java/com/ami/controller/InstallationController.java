package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ami.dto.requests.AddInstallationRemarkRequestDto;
import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.requests.CancelInstallationRequestDto;
import com.ami.dto.requests.CompleteInstallationRequestDto;
import com.ami.dto.requests.CreateInstallationRequestDto;
import com.ami.dto.requests.InstallationAssignmentFailureRequestDto;
import com.ami.dto.requests.InstallationChecklistRequestDto;
import com.ami.dto.requests.ReassignInstallationEngineerRequestDto;
import com.ami.dto.requests.RescheduleInstallationRequestDto;
import com.ami.dto.requests.UpdateInstallationRequestDto;
import com.ami.dto.responses.InstallationAnalyticsResponseDto;
import com.ami.dto.responses.InstallationAssignmentAttemptResponseDto;
import com.ami.dto.responses.InstallationChecklistResponseDto;
import com.ami.dto.responses.InstallationDashboardResponseDto;
import com.ami.dto.responses.InstallationEngineerWorkloadResponseDto;
import com.ami.dto.responses.InstallationHistoryResponseDto;
import com.ami.dto.responses.InstallationPhotoResponseDto;
import com.ami.dto.responses.InstallationRemarkResponseDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.dto.responses.InstallationSourceSummaryResponseDto;
import com.ami.dto.responses.InstallationStatisticsResponseDto;
import com.ami.dto.responses.InstallationTimelineResponseDto;
import com.ami.dto.responses.PageResponseDto;
import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;
import com.ami.service.InstallationAssignmentService;
import com.ami.service.InstallationService;

import jakarta.validation.Valid;
import com.ami.dto.requests.UploadInstallationAttachmentRequestDto;
import com.ami.dto.responses.InstallationAttachmentResponseDto;
@RestController
@RequestMapping("/api/installations")
public class InstallationController {

    private final InstallationService installationService;
    
    private final InstallationAssignmentService
    installationAssignmentService;

    public InstallationController(

            InstallationService installationService,

            InstallationAssignmentService
                    installationAssignmentService) {

        this.installationService =
                installationService;

        this.installationAssignmentService =
                installationAssignmentService;
    }
    @PostMapping
    public ResponseEntity<InstallationResponseDto> createInstallation(
            @Valid @RequestBody CreateInstallationRequestDto request) {

        return ResponseEntity.ok(
                installationService.createInstallation(request));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<InstallationResponseDto>> getAllInstallations(

            @RequestParam(required = false) String search,

            @RequestParam(required = false)
            InstallationStatus status,

            @RequestParam(required = false)
            InstallationPriority priority,

            @RequestParam(required = false)
            InstallationSource source,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            Long engineerId,

            @RequestParam(required = false)
            String customerId,

            @RequestParam(defaultValue = "0")
            Integer page,

            @RequestParam(defaultValue = "10")
            Integer size,
            
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate,

            @RequestParam(defaultValue = "createdAt,desc")
            String sort) {

        return ResponseEntity.ok(
                installationService.getAllInstallations(
                        search,
                        status,
                        priority,
                        source,
                        city,
                        engineerId,
                        customerId,
                         fromDate,
                        toDate,
                        page,
                        size,
                        sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallationResponseDto> getInstallationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getInstallationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstallationResponseDto> updateInstallation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInstallationRequestDto request) {

        return ResponseEntity.ok(
                installationService.updateInstallation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstallation(
            @PathVariable Long id) {

        installationService.deleteInstallation(id);

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/assign")
    public ResponseEntity<InstallationResponseDto> assignEngineer(
            @PathVariable Long id,
            @Valid @RequestBody AssignInstallationEngineerRequestDto request) {

        return ResponseEntity.ok(
                installationService.assignEngineer(id, request));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<InstallationResponseDto> startInstallation(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.startInstallation(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<InstallationResponseDto> completeInstallation(
            @PathVariable Long id,
            @Valid @RequestBody CompleteInstallationRequestDto request) {

        return ResponseEntity.ok(
                installationService.completeInstallation(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InstallationResponseDto> cancelInstallation(
            @PathVariable Long id,
            @Valid @RequestBody CancelInstallationRequestDto request) {

        return ResponseEntity.ok(
                installationService.cancelInstallation(id, request));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<InstallationResponseDto> rescheduleInstallation(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleInstallationRequestDto request) {

        return ResponseEntity.ok(
                installationService.rescheduleInstallation(id, request));
    }
    @GetMapping("/dashboard")
    public ResponseEntity<InstallationDashboardResponseDto> getDashboard() {

        return ResponseEntity.ok(
                installationService.getDashboard());
    }

    @GetMapping("/statistics")
    public ResponseEntity<InstallationStatisticsResponseDto> getStatistics() {

        return ResponseEntity.ok(
                installationService.getStatistics());
    }

    @GetMapping("/analytics")
    public ResponseEntity<InstallationAnalyticsResponseDto> getAnalytics() {

        return ResponseEntity.ok(
                installationService.getAnalytics());
    }

    @GetMapping("/source-summary")
    public ResponseEntity<List<InstallationSourceSummaryResponseDto>> getSourceSummary() {

        return ResponseEntity.ok(
                installationService.getSourceSummary());
    }

    @GetMapping("/engineers")
    public ResponseEntity<List<InstallationEngineerWorkloadResponseDto>> getAllEngineers() {

        return ResponseEntity.ok(
                installationService.getAllEngineers());
    }

    @GetMapping("/engineers/available")
    public ResponseEntity<List<InstallationEngineerWorkloadResponseDto>> getAvailableEngineers() {

        return ResponseEntity.ok(
                installationService.getAvailableEngineers());
    }

    @GetMapping("/engineers/workload")
    public ResponseEntity<List<InstallationEngineerWorkloadResponseDto>> getEngineerWorkload() {

        return ResponseEntity.ok(
                installationService.getEngineerWorkload());
    }
    @GetMapping("/{id}/history")
    public ResponseEntity<List<InstallationHistoryResponseDto>> getHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getHistory(id));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<InstallationTimelineResponseDto>> getTimeline(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getTimeline(id));
    }

    @GetMapping("/{id}/remarks")
    public ResponseEntity<List<InstallationRemarkResponseDto>> getRemarks(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getRemarks(id));
    }

    @PostMapping("/{id}/remarks")
    public ResponseEntity<InstallationRemarkResponseDto> addRemark(
            @PathVariable Long id,
            @Valid @RequestBody AddInstallationRemarkRequestDto request) {

        return ResponseEntity.ok(
                installationService.addRemark(id, request));
    }

    @DeleteMapping("/{id}/remarks/{remarkId}")
    public ResponseEntity<Void> deleteRemark(
            @PathVariable Long id,
            @PathVariable Long remarkId) {

        installationService.deleteRemark(id, remarkId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/checklist")
    public ResponseEntity<InstallationChecklistResponseDto> getChecklist(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getChecklist(id));
    }

    @PutMapping("/{id}/checklist")
    public ResponseEntity<InstallationChecklistResponseDto> updateChecklist(
            @PathVariable Long id,
            @Valid @RequestBody InstallationChecklistRequestDto request) {

        return ResponseEntity.ok(
                installationService.updateChecklist(id, request));
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<InstallationPhotoResponseDto>> getPhotos(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getPhotos(id));
    }

    @PostMapping(
            value = "/{id}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InstallationPhotoResponseDto> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                installationService.uploadPhoto(id, file));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long id,
            @PathVariable Long photoId) {

        installationService.deletePhoto(id, photoId);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<InstallationAttachmentResponseDto>>
    getAttachments(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getAttachments(id));
    }
    @PostMapping(
            value = "/{id}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InstallationAttachmentResponseDto>
    uploadAttachment(

            @PathVariable Long id,

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("attachmentType")
            String attachmentType) {

        UploadInstallationAttachmentRequestDto request =
                UploadInstallationAttachmentRequestDto
                        .builder()
                        .attachmentType(attachmentType)
                        .build();

        return ResponseEntity.ok(
                installationService.uploadAttachment(
                        id,
                        file,
                        request));
    }
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(

            @PathVariable Long id,

            @PathVariable Long attachmentId) {

        installationService.deleteAttachment(
                id,
                attachmentId);

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/reassign")
    public ResponseEntity<InstallationResponseDto> reassignEngineer(
            @PathVariable Long id,
            @RequestBody ReassignInstallationEngineerRequestDto request) {

        return ResponseEntity.ok(
                installationService.reassignEngineer(
                        id,
                        request));
    }
    @PostMapping("/{id}/assignment-failed")
    public ResponseEntity<InstallationResponseDto> markAssignmentFailed(
            @PathVariable Long id,
            @RequestBody InstallationAssignmentFailureRequestDto request) {

        return ResponseEntity.ok(
                installationService.markAssignmentFailed(
                        id,
                        request));
    }
    @GetMapping("/{id}/assignment-attempts")
    public ResponseEntity<List<InstallationAssignmentAttemptResponseDto>>
    getAssignmentAttempts(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                installationService.getAssignmentAttempts(
                        id));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=installations.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(installationService.exportCsv());
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=installations.xlsx")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(installationService.exportExcel());
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=installations.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(installationService.exportPdf());
    }
    
    @GetMapping("/engineer/{engineerId}/assignments")
    public ResponseEntity<List<InstallationResponseDto>>
    getEngineerAssignments(
            @PathVariable Long engineerId) {

        return ResponseEntity.ok(

                installationAssignmentService
                        .getEngineerAssignments(
                                engineerId));
    }
}
    

