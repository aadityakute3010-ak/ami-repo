package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CalibrationRequestDto;
import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.requests.RemoteConfigurationRequestDto;
import com.ami.dto.requests.RemoteRestartRequestDto;
import com.ami.dto.requests.RemoteSyncRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;
import com.ami.service.DeviceOperationService;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceOperationSummaryResponseDto;
import com.ami.enums.SourceType;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/device-operations")
public class DeviceOperationController {

    private final DeviceOperationService service;

    public DeviceOperationController(
            DeviceOperationService service) {

        this.service = service;
    }

    @PostMapping
    public DeviceOperationResponseDto createOperation(
            @RequestBody CreateDeviceOperationRequestDto request) {

        return service.createOperation(request);
    }

    @GetMapping
    public Page<DeviceOperationResponseDto> getAllOperations(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            SourceType sourceType,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            LocalDateTime toDate,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getAllOperations(

                page,

                size,

                search,

                sourceType,

                status,

                fromDate,

                toDate,

                sortBy,

                direction);
    }
    @GetMapping("/{id}")
    public DeviceOperationResponseDto
    getOperationById(
            @PathVariable Long id) {

        return service.getOperationById(id);
    }

    @GetMapping("/device/{deviceId}")
    public Page<DeviceOperationResponseDto> getByDeviceId(

            @PathVariable
            String deviceId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getByDeviceId(

                deviceId,

                page,

                size,

                sortBy,

                direction);
    }

    @GetMapping("/type/{operationType}")
    public List<DeviceOperationResponseDto>
    getByOperationType(
            @PathVariable String operationType) {

        return service.getByOperationType(
                operationType);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/archive/{operationId}")
    public ResponseEntity<String> archiveOperation(
            @PathVariable Long operationId,
            @RequestParam(required = false) String archiveReason) {

        return ResponseEntity.ok(
        		service.archiveOperation(
                        operationId,
                        archiveReason));
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/restore/{archivedOperationId}")
    public ResponseEntity<String> restoreOperation(
            @PathVariable Long archivedOperationId) {

        return ResponseEntity.ok(
        		service.restoreOperation(
                        archivedOperationId));
    }
    @GetMapping("/summary")
    public DeviceOperationSummaryResponseDto
    getSummary() {

        return service.getSummary();
    }
    @GetMapping("/dashboard")
    public DeviceDashboardResponseDto
    getDashboard() {

        return service.getDashboard();
    }
    @GetMapping("/analytics")
    public DeviceAnalyticsResponseDto
    getAnalytics() {

        return service.getAnalytics();
    }
    @GetMapping("/source/{sourceType}")
    public List<DeviceOperationResponseDto>
    getBySourceType(
            @PathVariable SourceType sourceType) {

        return service.getBySourceType(
                sourceType);
    }
    @GetMapping("/status/{status}")
    public Page<DeviceOperationResponseDto> getByStatus(

            @PathVariable
            String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getByStatus(

                status,

                page,

                size,

                sortBy,

                direction);
    }
    @GetMapping("/resolved")
    public Page<DeviceOperationResponseDto> getResolvedOperations(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getResolvedOperations(

                page,

                size,

                sortBy,

                direction);
    }
    @GetMapping("/pending")
    public Page<DeviceOperationResponseDto> getPendingOperations(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return service.getPendingOperations(

                page,

                size,

                sortBy,

                direction);
    }
    @PatchMapping("/{id}/resolve")
    public String resolveOperation(
            @PathVariable Long id) {

        return service.resolveOperation(
                id);
    }
    @PatchMapping("/{id}/acknowledge")
    public String acknowledgeOperation(

            @PathVariable Long id,

            @RequestParam String acknowledgedBy) {

        return service
                .acknowledgeOperation(
                        id,
                        acknowledgedBy);
    }
    @PatchMapping("/{id}/assign")
    public String assignOperation(

            @PathVariable Long id,

            @RequestParam String assignedTo) {

        return service.assignOperation(
                id,
                assignedTo);
    }
    @PatchMapping("/{id}/status")
    public String updateOperationStatus(

            @PathVariable Long id,

            @RequestParam String status) {

        return service.updateOperationStatus(
                id,
                status);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]>
    exportOperations(

            @RequestParam(defaultValue = "csv")
            String format) {

        byte[] data =
                service.exportOperations(
                        format);

        String extension =
                switch (format.toLowerCase()) {

                    case "excel" -> "xlsx";

                    case "pdf" -> "pdf";

                    default -> "csv";
                };

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=device-operations."
                                + extension)
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
    
    @PatchMapping("/{id}/restart")
    public String restartDevice(
            @PathVariable Long id){

        return service.restartDevice(id);
    }
    
    @PatchMapping("/{id}/sync")
    public String syncDevice(
            @PathVariable Long id){

        return service.syncDevice(id);
    }
    
    @PatchMapping("/{id}/firmware")
    public String updateFirmware(
            @PathVariable Long id){

        return service.updateFirmware(id);
    }
    
    @PatchMapping("/{id}/valve/open")
    public String openValve(
            @PathVariable Long id){

        return service.openValve(id);
    }
    
    @PatchMapping("/{id}/valve/close")
    public String closeValve(
            @PathVariable Long id){

        return service.closeValve(id);
    }
    @PostMapping("/{id}/remote-configuration")
    public ResponseEntity<String> remoteConfiguration(

            @PathVariable Long id,

            @RequestBody
            RemoteConfigurationRequestDto request) {

    	return ResponseEntity.ok(

    	        service.remoteConfiguration(

    	                id,

    	                request));
    }
    @PostMapping("/{id}/remote-restart")
    public ResponseEntity<String> remoteRestart(

            @PathVariable Long id,

            @RequestBody RemoteRestartRequestDto request) {

        return ResponseEntity.ok(

                service.remoteRestart(

                        id,

                        request));
    }
    @PostMapping("/{id}/remote-sync")
    public ResponseEntity<String> remoteSync(

            @PathVariable Long id,

            @RequestBody RemoteSyncRequestDto request) {

        return ResponseEntity.ok(

                service.remoteSync(

                        id,

                        request));
    }
    @PostMapping("/{id}/calibration")
    public ResponseEntity<String> calibrateDevice(

            @PathVariable Long id,

            @RequestBody CalibrationRequestDto request) {

        return ResponseEntity.ok(

                service.calibrateDevice(

                        id,

                        request));
    }
    
 // =====================================================
 // Gas Module
 // =====================================================

 @PatchMapping("/{id}/gas/emergency-shutdown")
 public String emergencyShutdown(
         @PathVariable Long id) {

     return service.emergencyShutdown(id);
 }

 @PatchMapping("/{id}/gas/reset-alarm")
 public String resetGasAlarm(
         @PathVariable Long id) {

     return service.resetGasAlarm(id);
 }

 @PatchMapping("/{id}/gas/start-flow")
 public String startGasFlow(
         @PathVariable Long id) {

     return service.startGasFlow(id);
 }

 @PatchMapping("/{id}/gas/stop-flow")
 public String stopGasFlow(
         @PathVariable Long id) {

     return service.stopGasFlow(id);
 }

 @PatchMapping("/{id}/gas/purge")
 public String purgePipeline(
         @PathVariable Long id) {

     return service.purgePipeline(id);
 }

 @PatchMapping("/{id}/gas/resume")
 public String resumeGasSupply(
         @PathVariable Long id) {

     return service.resumeGasSupply(id);
 }
}