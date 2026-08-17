package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;
import com.ami.entity.User;
import com.ami.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateAlertEventRequestDto;
import com.ami.dto.responses.AlertEventResponseDto;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;
import com.ami.service.AlertEventService;

@RestController
@RequestMapping("/alert-events")
public class AlertEventController {

    private final AlertEventService alertEventService;
    
    private final UserRepository userRepository;

    public AlertEventController(
            AlertEventService alertEventService,
            UserRepository userRepository) {

        this.alertEventService = alertEventService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE EVENT
    // =========================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public ResponseEntity<AlertEventResponseDto> createEvent(
            @RequestBody CreateAlertEventRequestDto request) {

        return ResponseEntity.ok(
                alertEventService.createEvent(request));
    }

    // =========================================================
    // GET EVENT BY ID
    // =========================================================

    @GetMapping("/{eventId}")
    public ResponseEntity<AlertEventResponseDto> getEventById(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                alertEventService.getEventById(eventId));
    }

    // =========================================================
    // GET ALL EVENTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<AlertEventResponseDto>> getAllEvents() {

        return ResponseEntity.ok(
                alertEventService.getAllEvents());
    }

    // =========================================================
    // GET BY DEVICE
    // =========================================================

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventsByDevice(
            @PathVariable Long deviceId) {

        return ResponseEntity.ok(
                alertEventService
                        .getEventsByDevice(deviceId));
    }

    // =========================================================
    // GET BY ALERT
    // =========================================================

    @GetMapping("/alert/{alertId}")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventsByAlert(
            @PathVariable Long alertId) {

        return ResponseEntity.ok(
                alertEventService
                        .getEventsByAlert(alertId));
    }

    // =========================================================
    // GET BY STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventsByStatus(
            @PathVariable AlertStatus status) {

        return ResponseEntity.ok(
                alertEventService
                        .getEventsByStatus(status));
    }

    // =========================================================
    // GET BY SEVERITY
    // =========================================================

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventsBySeverity(
            @PathVariable AlertSeverity severity) {

        return ResponseEntity.ok(
                alertEventService
                        .getEventsBySeverity(severity));
    }

    // =========================================================
    // RECENT EVENTS
    // =========================================================

    @GetMapping("/recent")
    public ResponseEntity<List<AlertEventResponseDto>>
    getRecentEvents() {

        return ResponseEntity.ok(
                alertEventService.getRecentEvents());
    }

    // =========================================================
    // ACTIVE EVENTS
    // =========================================================

    @GetMapping("/active")
    public ResponseEntity<List<AlertEventResponseDto>>
    getActiveEvents() {

        return ResponseEntity.ok(
                alertEventService.getActiveEvents());
    }

    // =========================================================
    // EVENT HISTORY
    // =========================================================

    @GetMapping("/history")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventHistory() {

        return ResponseEntity.ok(
                alertEventService.getEventHistory());
    }

    // =========================================================
    // EVENTS BETWEEN DATES
    // =========================================================

    @GetMapping("/between")
    public ResponseEntity<List<AlertEventResponseDto>>
    getEventsBetween(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        return ResponseEntity.ok(
                alertEventService
                        .getEventsBetween(start, end));
    }

    // =========================================================
    // ACKNOWLEDGE
    // =========================================================
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{eventId}/acknowledge")
    public ResponseEntity<AlertEventResponseDto> acknowledgeEvent(
            @PathVariable Long eventId,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"));

        return ResponseEntity.ok(
                alertEventService.acknowledgeEvent(
                        eventId,
                        user.getId()));
    }

    // =========================================================
    // RESOLVE
    // =========================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{eventId}/resolve")
    public ResponseEntity<AlertEventResponseDto> resolveEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false)
            String resolutionNotes,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"));

        return ResponseEntity.ok(
                alertEventService.resolveEvent(
                        eventId,
                        resolutionNotes,
                        user.getId()));
    }
    // =========================================================
    // REOPEN
    // =========================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{eventId}/reopen")
    public ResponseEntity<AlertEventResponseDto>
    reopenEvent(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                alertEventService
                        .reopenEvent(eventId));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                alertEventService
                        .deleteEvent(eventId));
    }

    // =========================================================
    // DASHBOARD SUMMARY
    // =========================================================

    @GetMapping("/dashboard/summary")
    public ResponseEntity<java.util.Map<String, Long>>
    getDashboardSummary() {

        return ResponseEntity.ok(
                java.util.Map.of(
                        "totalEvents",
                        alertEventService.getTotalEvents(),

                        "triggeredToday",
                        alertEventService.getTriggeredToday(),

                        "triggeredThisWeek",
                        alertEventService.getTriggeredThisWeek(),

                        "triggeredThisMonth",
                        alertEventService.getTriggeredThisMonth(),

                        "activeEvents",
                        alertEventService.getActiveEventCount(),

                        "acknowledgedEvents",
                        alertEventService
                                .getAcknowledgedEventCount(),

                        "resolvedEvents",
                        alertEventService
                                .getResolvedEventCount(),
                                
                                "criticalEvents",
                                alertEventService.getCriticalEventCount()        
                ));
    }
}