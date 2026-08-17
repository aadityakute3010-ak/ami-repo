package com.ami.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import com.ami.entity.User;
import com.ami.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.CreateAlertEventRequestDto;
import com.ami.dto.responses.AlertEventResponseDto;
import com.ami.entity.AlertEvent;
import com.ami.entity.Device;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;
import com.ami.repository.AlertEventRepository;
import com.ami.repository.DeviceRepository;
import com.ami.service.AlertEventService;
import com.ami.entity.Alert;
import com.ami.repository.AlertRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertEventServiceImpl implements AlertEventService {

    private final AlertEventRepository alertEventRepository;
    
    private final UserRepository userRepository;

    private final DeviceRepository deviceRepository;
    
    private final AlertRepository alertRepository;
    
    

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public AlertEventResponseDto createEvent(
            CreateAlertEventRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Event request cannot be null");
        }

        if (request.getAlertId() == null) {
            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        if (request.getDeviceId() == null) {
            throw new IllegalArgumentException(
                    "Device ID cannot be null");
        }

        Device device =
                deviceRepository.findById(
                        request.getDeviceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Device not found"));
        
        Alert alert =
                alertRepository.findById(request.getAlertId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"));

        AlertEvent event = AlertEvent.builder()
                .alertId(alert.getId())
                .deviceId(request.getDeviceId())
                .message(request.getMessage())
                .severity(alert.getSeverity())
                .thresholdValue(null)
                .status(AlertStatus.ACTIVE)
                .acknowledged(false)
                .resolved(false)
                .triggeredAt(LocalDateTime.now())
                .build();

        return mapToResponse(
                alertEventRepository.save(event),
                device);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AlertEventResponseDto getEventById(
            Long eventId) {

        AlertEvent event =
                alertEventRepository.findById(eventId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert event not found"));

        return mapToResponse(event);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getAllEvents() {

        return alertEventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BY DEVICE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventsByDevice(
            Long deviceId) {

        return alertEventRepository
                .findByDeviceIdOrderByTriggeredAtDesc(deviceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BY ALERT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventsByAlert(
            Long alertId) {

        return alertEventRepository
                .findByAlertIdOrderByTriggeredAtDesc(alertId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BY STATUS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventsByStatus(
            AlertStatus status) {

        return alertEventRepository
                .findByStatusOrderByTriggeredAtDesc(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BY SEVERITY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventsBySeverity(
            AlertSeverity severity) {

        return alertEventRepository
                .findBySeverityOrderByTriggeredAtDesc(severity)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // RECENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getRecentEvents() {

        return alertEventRepository
                .findTop10ByOrderByTriggeredAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // ACTIVE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getActiveEvents() {

        return getEventsByStatus(AlertStatus.ACTIVE);
    }

    // =========================================================
    // HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventHistory() {

        return alertEventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // BETWEEN DATES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertEventResponseDto> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end dates are required");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        return alertEventRepository
                .findByTriggeredAtBetweenOrderByTriggeredAtDesc(
                        start,
                        end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // ACKNOWLEDGE
    // =========================================================

    @Override
    public AlertEventResponseDto acknowledgeEvent(
            Long eventId,
            Long userId) {

        AlertEvent event = getEventEntity(eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        event.setAcknowledged(true);
        event.setAcknowledgedAt(LocalDateTime.now());
        event.setAcknowledgedById(user.getId());
        event.setAcknowledgedByName(
                user.getFirstName() + " " + user.getLastName());
        event.setStatus(AlertStatus.ACKNOWLEDGED);

        return mapToResponse(
                alertEventRepository.save(event));
    }

    // =========================================================
    // RESOLVE
    // =========================================================

    @Override
    public AlertEventResponseDto resolveEvent(
            Long eventId,
            String resolutionNotes,
            Long userId) {

        AlertEvent event = getEventEntity(eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        event.setResolved(true);
        event.setResolvedAt(LocalDateTime.now());
        event.setResolvedById(user.getId());
        event.setResolvedByName(
                user.getFirstName() + " " + user.getLastName());
        event.setResolutionNotes(resolutionNotes);
        event.setStatus(AlertStatus.RESOLVED);

        return mapToResponse(
                alertEventRepository.save(event));
    }

    // =========================================================
    // REOPEN
    // =========================================================

    @Override
    public AlertEventResponseDto reopenEvent(
            Long eventId) {

        AlertEvent event =
                getEventEntity(eventId);

        event.setResolved(false);
        event.setResolvedAt(null);
        event.setResolvedById(null);
        event.setResolvedByName(null);
        event.setResolutionNotes(null);
        event.setStatus(AlertStatus.ACTIVE);

        return mapToResponse(
                alertEventRepository.save(event));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public String deleteEvent(
            Long eventId) {

        AlertEvent event =
                getEventEntity(eventId);

        alertEventRepository.delete(event);

        return "Alert event deleted successfully";
    }

    // =========================================================
    // DASHBOARD COUNTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getTotalEvents() {

        return alertEventRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTriggeredToday() {

        LocalDate today = LocalDate.now();

        LocalDateTime start =
                today.atStartOfDay();

        LocalDateTime end =
                today.plusDays(1).atStartOfDay();

        return alertEventRepository
                .countByTriggeredAtBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTriggeredThisWeek() {

        LocalDate today = LocalDate.now();

        LocalDate monday =
                today.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY));

        LocalDateTime start =
                monday.atStartOfDay();

        LocalDateTime end =
                monday.plusDays(7).atStartOfDay();

        return alertEventRepository
                .countByTriggeredAtBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTriggeredThisMonth() {

        LocalDate today = LocalDate.now();

        LocalDate firstDay =
                today.withDayOfMonth(1);

        LocalDateTime start =
                firstDay.atStartOfDay();

        LocalDateTime end =
                firstDay.plusMonths(1)
                        .atStartOfDay();

        return alertEventRepository
                .countByTriggeredAtBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveEventCount() {

        return alertEventRepository
                .countByStatus(AlertStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public long getAcknowledgedEventCount() {

        return alertEventRepository
                .countByStatus(
                        AlertStatus.ACKNOWLEDGED);
    }

    @Override
    @Transactional(readOnly = true)
    public long getResolvedEventCount() {

        return alertEventRepository
                .countByStatus(
                        AlertStatus.RESOLVED);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCriticalEventCount() {

        return alertEventRepository
                .countBySeverity(AlertSeverity.CRITICAL);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private AlertEvent getEventEntity(
            Long eventId) {

        return alertEventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert event not found"));
    }

    private AlertEventResponseDto mapToResponse(
            AlertEvent event) {

        Device device = null;

        if (event.getDeviceId() != null) {
            device = deviceRepository
                    .findById(event.getDeviceId())
                    .orElse(null);
        }

        return mapToResponse(event, device);
    }

    private AlertEventResponseDto mapToResponse(
            AlertEvent event,
            Device device) {

        Alert alert = null;

        if (event.getAlertId() != null) {
            alert = alertRepository
                    .findById(event.getAlertId())
                    .orElse(null);
        }

        return AlertEventResponseDto.builder()
                .id(event.getId())

                .alertId(event.getAlertId())

                .alertName(
                        alert != null
                                ? alert.getName()
                                : null)

                .alertCategory(
                        alert != null && alert.getCategory() != null
                                ? alert.getCategory().name()
                                : null)

                .source(
                        alert != null && alert.getSource() != null
                                ? alert.getSource().name()
                                : null)

                .deviceId(event.getDeviceId())

                .deviceName(
                        device != null
                                ? device.getDeviceName()
                                : null)

                .deviceSerialNumber(
                        device != null
                                ? device.getSerialNumber()
                                : null)

                .actualValue(event.getActualValue())

                .thresholdValue(event.getThresholdValue())

                .message(event.getMessage())

                .severity(event.getSeverity())

                .status(event.getStatus())

                .acknowledged(event.getAcknowledged())

                .acknowledgedAt(
                        event.getAcknowledgedAt())

                .acknowledgedById(
                        event.getAcknowledgedById())

                .acknowledgedByName(
                        event.getAcknowledgedByName())

                .resolved(event.getResolved())

                .resolvedAt(event.getResolvedAt())

                .resolvedById(
                        event.getResolvedById())

                .resolvedByName(
                        event.getResolvedByName())

                .resolutionNotes(
                        event.getResolutionNotes())

                .triggeredAt(
                        event.getTriggeredAt())

                .build();
    }
}