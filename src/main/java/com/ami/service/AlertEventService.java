package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ami.dto.requests.CreateAlertEventRequestDto;
import com.ami.dto.responses.AlertEventResponseDto;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertStatus;

public interface AlertEventService {

    // =========================================================
    // CREATE
    // =========================================================

    AlertEventResponseDto createEvent(
            CreateAlertEventRequestDto request);

    // =========================================================
    // GET
    // =========================================================

    AlertEventResponseDto getEventById(
            Long eventId);

    List<AlertEventResponseDto> getAllEvents();

    List<AlertEventResponseDto> getEventsByDevice(
            Long deviceId);

    List<AlertEventResponseDto> getEventsByAlert(
            Long alertId);

    List<AlertEventResponseDto> getEventsByStatus(
            AlertStatus status);

    List<AlertEventResponseDto> getEventsBySeverity(
            AlertSeverity severity);

    List<AlertEventResponseDto> getRecentEvents();

    List<AlertEventResponseDto> getActiveEvents();

    List<AlertEventResponseDto> getEventHistory();

    List<AlertEventResponseDto> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end);

    // =========================================================
    // EVENT ACTIONS
    // =========================================================

    AlertEventResponseDto acknowledgeEvent(
            Long eventId,
            Long userId);

    AlertEventResponseDto resolveEvent(
            Long eventId,
            String resolutionNotes,
            Long userId);

    AlertEventResponseDto reopenEvent(
            Long eventId);

    // =========================================================
    // DELETE
    // =========================================================

    String deleteEvent(
            Long eventId);

    // =========================================================
    // DASHBOARD
    // =========================================================

    long getTotalEvents();

    long getTriggeredToday();

    long getTriggeredThisWeek();

    long getTriggeredThisMonth();

    long getActiveEventCount();

    long getAcknowledgedEventCount();

    long getResolvedEventCount();
    
    long getCriticalEventCount();
}