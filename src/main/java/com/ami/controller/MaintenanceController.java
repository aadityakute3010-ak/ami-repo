package com.ami.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.AssignmentFailureRequestDto;
import com.ami.dto.requests.CancelMaintenanceRequestDto;
import com.ami.dto.requests.CompleteMaintenanceRequestDto;
import com.ami.dto.requests.CreateMaintenanceRequestDto;
import com.ami.dto.requests.ReassignEngineerRequestDto;
import com.ami.dto.requests.RescheduleMaintenanceRequestDto;
import com.ami.dto.requests.StartMaintenanceRequestDto;
import com.ami.dto.requests.UpdateMaintenanceAttachmentsRequestDto;
import com.ami.dto.requests.UpdateMaintenanceChecklistRequestDto;
import com.ami.dto.requests.UpdateMaintenancePhotoRequestDto;
import com.ami.dto.requests.UpdateMaintenanceRemarksRequestDto;
import com.ami.dto.responses.MaintenanceAttachmentsResponseDto;
import com.ami.dto.responses.MaintenanceChecklistResponseDto;
import com.ami.dto.responses.MaintenanceDashboardResponseDto;
import com.ami.dto.responses.MaintenanceHistoryResponseDto;
import com.ami.dto.responses.MaintenancePhotoResponseDto;
import com.ami.dto.responses.MaintenanceRemarksResponseDto;
import com.ami.dto.responses.MaintenanceResponseDto;
import com.ami.dto.responses.MaintenanceTimelineResponseDto;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;
import com.ami.service.MaintenanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(
            MaintenanceService maintenanceService) {

        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<MaintenanceResponseDto> createMaintenance(
            @RequestBody CreateMaintenanceRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.createMaintenance(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> getMaintenanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService.getMaintenanceById(id));
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceResponseDto>> getAllMaintenance(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            MaintenanceType maintenanceType,

            @RequestParam(required = false)
            MaintenanceStatus status,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return ResponseEntity.ok(

                maintenanceService.getAllMaintenance(

                        page,

                        size,

                        search,

                        maintenanceType,

                        status,

                        sortBy,

                        direction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> updateMaintenance(

            @PathVariable Long id,

            @RequestBody CreateMaintenanceRequestDto request) {

        return ResponseEntity.ok(

                maintenanceService.updateMaintenance(

                        id,

                        request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaintenance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService.deleteMaintenance(id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<MaintenanceDashboardResponseDto> getDashboard() {

        return ResponseEntity.ok(
                maintenanceService.getDashboard());
    }
    
    @GetMapping("/timeline")
    public ResponseEntity<List<MaintenanceTimelineResponseDto>>
    getTimeline() {

        return ResponseEntity.ok(

                maintenanceService.getTimeline());
    }
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<MaintenanceTimelineResponseDto>>
    getMaintenanceTimeline(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService
                        .getTimeline(id));
    }
    @GetMapping("/{deviceId}/history")
    public ResponseEntity<List<MaintenanceHistoryResponseDto>>
    getHistory(
            @PathVariable String deviceId) {

        return ResponseEntity.ok(

                maintenanceService.getHistory(
                        deviceId));
    }
  
    @GetMapping("/upcoming")
    public ResponseEntity<List<MaintenanceResponseDto>>
    getUpcomingMaintenance() {

        return ResponseEntity.ok(

                maintenanceService
                        .getUpcomingMaintenance());
    }
    
    @GetMapping("/completed")
    public ResponseEntity<List<MaintenanceResponseDto>>
    getCompletedMaintenance() {

        return ResponseEntity.ok(

                maintenanceService
                        .getCompletedMaintenance());
    }
    @PutMapping("/{id}/assign")
    public ResponseEntity<MaintenanceResponseDto> assignEngineer(

            @PathVariable Long id,

            @Valid
            @RequestBody AssignEngineerRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.assignEngineer(
                        id,
                        request));
    }
    @PutMapping("/{id}/reassign")
    public ResponseEntity<MaintenanceResponseDto> reassignEngineer(

            @PathVariable Long id,

            @Valid
            @RequestBody ReassignEngineerRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.reassignEngineer(
                        id,
                        request));
    }
    @PutMapping("/{id}/assignment-failure")
    public ResponseEntity<MaintenanceResponseDto>
    recordAssignmentFailure(

            @PathVariable Long id,

            @Valid
            @RequestBody AssignmentFailureRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.recordAssignmentFailure(
                        id,
                        request));
    }
    @PutMapping("/{id}/start")
    public ResponseEntity<MaintenanceResponseDto>
    startMaintenance(

            @PathVariable Long id,

            @RequestBody(required = false)
            StartMaintenanceRequestDto request) {

        if (request == null) {
            request =
                    new StartMaintenanceRequestDto();
        }

        return ResponseEntity.ok(
                maintenanceService.startMaintenance(
                        id,
                        request));
    }
    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceResponseDto>
    completeMaintenance(

            @PathVariable Long id,

            @RequestBody(required = false)
            CompleteMaintenanceRequestDto request) {

        if (request == null) {
            request =
                    new CompleteMaintenanceRequestDto();
        }

        return ResponseEntity.ok(
                maintenanceService.completeMaintenance(
                        id,
                        request));
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<MaintenanceResponseDto>
    cancelMaintenance(

            @PathVariable Long id,

            @Valid
            @RequestBody CancelMaintenanceRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.cancelMaintenance(
                        id,
                        request));
    }
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<MaintenanceResponseDto>
    rescheduleMaintenance(

            @PathVariable Long id,

            @Valid
            @RequestBody RescheduleMaintenanceRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService.rescheduleMaintenance(
                        id,
                        request));
    }
    @GetMapping("/{id}/checklist")
    public ResponseEntity<MaintenanceChecklistResponseDto>
    getMaintenanceChecklist(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService
                        .getMaintenanceChecklist(id));
    }
    @PutMapping("/{id}/checklist")
    public ResponseEntity<MaintenanceChecklistResponseDto>
    updateMaintenanceChecklist(

            @PathVariable Long id,

            @RequestBody
            UpdateMaintenanceChecklistRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService
                        .updateMaintenanceChecklist(
                                id,
                                request));
    }
    @GetMapping("/{id}/remarks")
    public ResponseEntity<MaintenanceRemarksResponseDto>
    getMaintenanceRemarks(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService
                        .getMaintenanceRemarks(id));
    }
    @PutMapping("/{id}/remarks")
    public ResponseEntity<MaintenanceRemarksResponseDto>
    updateMaintenanceRemarks(

            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateMaintenanceRemarksRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService
                        .updateMaintenanceRemarks(
                                id,
                                request));
    }
    @GetMapping("/{id}/photos")
    public ResponseEntity<MaintenancePhotoResponseDto>
    getMaintenancePhotos(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService
                        .getMaintenancePhotos(id));
    }
    @PutMapping("/{id}/photos")
    public ResponseEntity<MaintenancePhotoResponseDto>
    updateMaintenancePhotos(
            @PathVariable Long id,
            @RequestBody UpdateMaintenancePhotoRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService
                        .updateMaintenancePhotos(
                                id,
                                request));
    }
    @GetMapping("/{id}/attachments")
    public ResponseEntity<MaintenanceAttachmentsResponseDto>
    getMaintenanceAttachments(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService
                        .getMaintenanceAttachments(id));
    }
    @PutMapping("/{id}/attachments")
    public ResponseEntity<MaintenanceAttachmentsResponseDto>
    updateMaintenanceAttachments(

            @PathVariable Long id,

            @RequestBody
            UpdateMaintenanceAttachmentsRequestDto request) {

        return ResponseEntity.ok(
                maintenanceService
                        .updateMaintenanceAttachments(
                                id,
                                request));
    }
}