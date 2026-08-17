package com.ami.controller;

import com.ami.dto.requests.ApplyLeaveRequestDto;
import com.ami.dto.responses.EngineerOperationResponseDto;
import com.ami.dto.requests.UpdateLeaveStatusRequestDto;
import com.ami.dto.responses.EngineerActivityResponseDto;
import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerLeaveResponseDto;
import com.ami.dto.responses.EngineerOperationsSummaryResponseDto;
import com.ami.dto.responses.EngineerPerformanceResponseDto;
import com.ami.dto.responses.EngineerStatisticsResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.service.EngineerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequestMapping("/api/engineers")
public class EngineerController {

    private final EngineerService engineerService;

    public EngineerController(
            EngineerService engineerService) {

        this.engineerService = engineerService;
    }

    @GetMapping
    public List<User> getEngineers() {

        return engineerService.getEngineers();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{engineerId}")
    public User getEngineerById(
            @PathVariable Long engineerId) {

        return engineerService.getEngineerById(
                engineerId);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/available")
    public List<User> getAvailableEngineers() {

        return engineerService.getAvailableEngineers();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{engineerId}/workload")
    public EngineerWorkloadResponseDto getWorkload(
            @PathVariable Long engineerId) {

        return engineerService.getWorkload(
                engineerId);
    }
    @GetMapping("/dashboard/{engineerId}")
    public EngineerDashboardResponseDto
    getDashboard(
            @PathVariable Long engineerId) {

        return engineerService
                .getDashboard(
                        engineerId);
    }
    @GetMapping("/{engineerId}/performance")
    public EngineerPerformanceResponseDto getPerformance(
            @PathVariable Long engineerId) {

        return engineerService.getPerformance(
                engineerId);
    }
    @GetMapping("/{engineerId}/history")
    public List<IssueResponseDto> getHistory(
            @PathVariable Long engineerId) {

        return engineerService
                .getHistory(
                        engineerId);
    }
    @GetMapping("/{engineerId}/schedule")
    public List<IssueResponseDto> getSchedule(
            @PathVariable Long engineerId) {

        return engineerService
                .getSchedule(
                        engineerId);
    }
    
   
    @PutMapping("/{engineerId}/attendance")
    public String updateAttendance(
            @PathVariable Long engineerId,
            @RequestParam EngineerAttendanceStatus status) {

        return engineerService
                .updateAttendance(
                        engineerId,
                        status);
    }
    @PutMapping("/{engineerId}/availability")
    public String updateAvailability(
            @PathVariable Long engineerId,
            @RequestParam EngineerAvailabilityStatus status) {

        return engineerService
                .updateAvailability(
                        engineerId,
                        status);
    }
    @PostMapping("/{engineerId}/leave")
    public String applyLeave(
            @PathVariable Long engineerId,
            @RequestBody ApplyLeaveRequestDto request) {

        return engineerService
                .applyLeave(
                        engineerId,
                        request);
    }
    @GetMapping("/{engineerId}/leave-history")
    public List<EngineerLeaveResponseDto>
    getLeaveHistory(
            @PathVariable Long engineerId) {

        return engineerService
                .getLeaveHistory(
                        engineerId);
    }
    @GetMapping("/{engineerId}/leave-balance")
    public Integer getLeaveBalance(
            @PathVariable Long engineerId) {

        return engineerService
                .getLeaveBalance(
                        engineerId);
    }
    @PutMapping("/leave/{leaveId}/status")
    public String updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestBody UpdateLeaveStatusRequestDto request) {

        return engineerService.updateLeaveStatus(
                leaveId,
                request.getStatus());
    }
    
    @GetMapping("/operations/summary")
    public EngineerOperationsSummaryResponseDto
    getOperationsSummary() {

        return engineerService
                .getOperationsSummary();
    }
    @GetMapping("/{engineerId}/activity")
    public List<EngineerActivityResponseDto>
    getActivity(
            @PathVariable Long engineerId) {

        return engineerService
                .getActivity(
                        engineerId);
    }
    @GetMapping("/statistics")
    public EngineerStatisticsResponseDto
    getStatistics() {

        return engineerService
                .getStatistics();
    }
    @GetMapping("/operations")
    public Page<EngineerOperationResponseDto> getEngineerOperations(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            EngineerAttendanceStatus attendanceStatus,

            @RequestParam(required = false)
            EngineerAvailabilityStatus availabilityStatus,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection) {

        return engineerService.getEngineerOperations(
                search,
                attendanceStatus,
                availabilityStatus,
                page,
                size,
                sortBy,
                sortDirection);
    }
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {

        byte[] data = engineerService.exportEngineersCsv();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=engineers.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {

        byte[] data = engineerService.exportEngineersExcel();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=engineers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {

        byte[] data = engineerService.exportEngineersPdf();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=engineers.pdf")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
    @GetMapping("/{engineerId}/export")
    public ResponseEntity<byte[]> exportEngineer(

            @PathVariable Long engineerId,

            @RequestParam String format) {

        byte[] data = engineerService.exportEngineer(
                engineerId,
                format);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=engineer." + format)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}