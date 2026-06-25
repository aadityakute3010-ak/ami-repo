package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.ami.dto.requests.CreateAlertRequestDto;
import com.ami.dto.requests.UpdateAlertRequestDto;
import com.ami.dto.responses.AlertResponseDto;
import com.ami.dto.responses.AlertSummaryResponseDto;
import com.ami.entity.Alert;
import com.ami.enums.AlertStatus;
import com.ami.repository.AlertRepository;
import com.ami.service.AlertService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.ami.dto.responses.AlertHistoryResponseDto;
import com.ami.dto.responses.AlertNotificationDto;
import com.ami.entity.AlertHistory;
import com.ami.repository.AlertHistoryRepository;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    
    private final AlertHistoryRepository alertHistoryRepository;

    private final SimpMessagingTemplate messagingTemplate;
    
    public AlertServiceImpl(
            AlertRepository alertRepository,
            AlertHistoryRepository alertHistoryRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.alertRepository = alertRepository;
        this.alertHistoryRepository = alertHistoryRepository;
        this.messagingTemplate = messagingTemplate;
    }
    @Override
    public List<AlertResponseDto> getAllAlerts(
            String search,
            String status,
            String source,
            String severity) {

        return alertRepository.findAll()
                .stream()

                .filter(alert ->

                        search == null ||

                        alert.getName()
                                .toLowerCase()
                                .contains(
                                        search.toLowerCase()))

                .filter(alert -> {

                    if(status == null) {

                        return true;
                    }

                    if(status.equalsIgnoreCase(
                            "ACTIVE")) {

                        return alert.getEnabled();
                    }

                    if(status.equalsIgnoreCase(
                            "INACTIVE")) {

                        return !alert.getEnabled();
                    }

                    return true;
                })

                .filter(alert ->

                        source == null ||

                        alert.getSource()
                                .name()
                                .equalsIgnoreCase(
                                        source))

                .filter(alert ->

                        severity == null ||

                        alert.getSeverity()
                                .name()
                                .equalsIgnoreCase(
                                        severity))

                .map(this::mapToResponse)

                .toList();
    }
    @Override
    public AlertResponseDto getAlertById(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));

        return mapToResponse(alert);
    }

    @Override
    public AlertResponseDto createAlert(
            CreateAlertRequestDto request) {

        Alert alert = Alert.builder()
                .name(request.getName())
                .fieldLabel(request.getFieldLabel())
                .placeholder(request.getPlaceholder())
                .enabled(request.getEnabled())
                .value(request.getValue())
                .severity(request.getSeverity())
                .source(request.getSource())
                .category(request.getCategory())
                .description(request.getDescription())
                .unit(request.getUnit())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(AlertStatus.ACTIVE)
                .build();

        alert = alertRepository.save(alert);
        sendAlertNotification(alert);
        
        saveHistory(
                alert.getId(),
                "CREATE",
                "Alert created");

        return mapToResponse(alert);
    }

    @Override
    public AlertResponseDto updateAlert(
            Long id,
            UpdateAlertRequestDto request) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));

        alert.setName(request.getName());
        alert.setFieldLabel(request.getFieldLabel());
        alert.setPlaceholder(request.getPlaceholder());
        alert.setEnabled(request.getEnabled());
        alert.setValue(request.getValue());
        alert.setSeverity(request.getSeverity());
        alert.setSource(request.getSource());
        alert.setCategory(request.getCategory());
        alert.setDescription(request.getDescription());
        alert.setUnit(request.getUnit());
        alert.setUpdatedAt(LocalDateTime.now());

        alert = alertRepository.save(alert);
        sendAlertNotification(alert);
        
        saveHistory(
                alert.getId(),
                "UPDATE",
                "Alert updated");

        return mapToResponse(alert);
    }

    @Override
    public String toggleAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));

        alert.setEnabled(!alert.getEnabled());

        alertRepository.save(alert);
        
        saveHistory(
                alert.getId(),
                "TOGGLE",
                "Alert enabled status changed");

        return "Alert toggled successfully";
    }

    @Override
    public String resetAlerts() {

        List<Alert> alerts =
                alertRepository.findAll();

        alerts.forEach(alert -> {
            alert.setEnabled(true);
            alert.setUpdatedAt(LocalDateTime.now());
        });

        alertRepository.saveAll(alerts);

        return "Alerts reset successfully";
    }

    private AlertResponseDto mapToResponse(
            Alert alert) {

        return AlertResponseDto.builder()
                .id(alert.getId())
                .name(alert.getName())
                .fieldLabel(alert.getFieldLabel())
                .placeholder(alert.getPlaceholder())
                .enabled(alert.getEnabled())
                .value(alert.getValue())
                .severity(alert.getSeverity())
                .source(alert.getSource())
                .category(alert.getCategory())
                .description(alert.getDescription())
                .unit(alert.getUnit())
                .status(alert.getStatus())
                .build();
    }
    @Override
    public AlertSummaryResponseDto getSummary() {

        List<Alert> alerts =
                alertRepository.findAll();

        long total =
                alerts.size();

        long active =
                alerts.stream()
                        .filter(Alert::getEnabled)
                        .count();

        long inactive =
                alerts.stream()
                        .filter(alert ->
                                !alert.getEnabled())
                        .count();

        long critical =
                alerts.stream()
                        .filter(alert ->
                                alert.getSeverity() != null &&
                                alert.getSeverity().name()
                                        .equals("CRITICAL"))
                        .count();

        return AlertSummaryResponseDto
                .builder()
                .totalAlerts(total)
                .activeAlerts(active)
                .inactiveAlerts(inactive)
                .criticalAlerts(critical)
                .build();
    }
    @Override
    public String deleteAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found"));

        alertRepository.delete(alert);
        saveHistory(
                alert.getId(),
                "DELETE",
                "Alert deleted");

        return "Alert deleted successfully";
    }
    @Override
    public Page<AlertResponseDto> getAlertsWithPagination(
            int page,
            int limit) {

        Pageable pageable =
                PageRequest.of(page, limit);

        return alertRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    @Override
    public String updateAlertStatus(
            Long id,
            AlertStatus status) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found"));

        alert.setStatus(status);
        alert.setUpdatedAt(LocalDateTime.now());

        alertRepository.save(alert);
        
        saveHistory(
                alert.getId(),
                "STATUS_CHANGE",
                "Status changed to " + status);

        return "Alert status updated successfully";
    }
    private void saveHistory(
            Long alertId,
            String action,
            String description) {

        AlertHistory history =
                AlertHistory.builder()
                        .alertId(alertId)
                        .action(action)
                        .description(description)
                        .timestamp(LocalDateTime.now())
                        .build();

        alertHistoryRepository.save(history);
    }

    @Override
    public List<AlertHistoryResponseDto>
    getAlertHistory(Long alertId) {

        return alertHistoryRepository
                .findByAlertId(alertId)
                .stream()
                .map(history ->
                        AlertHistoryResponseDto
                                .builder()
                                .id(history.getId())
                                .alertId(history.getAlertId())
                                .action(history.getAction())
                                .description(history.getDescription())
                                .timestamp(history.getTimestamp())
                                .build())
                .toList();
    }
    @Override
    public String importAlerts(
            MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException(
                    "File is empty");
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null) {
            throw new RuntimeException(
                    "Invalid file");
        }

        if (fileName.endsWith(".csv")) {
            return "CSV import received successfully";
        }

        if (fileName.endsWith(".xlsx")
                || fileName.endsWith(".xls")) {
            return "Excel import received successfully";
        }

        throw new RuntimeException(
                "Unsupported file type");
    }
    @Override
    public byte[] exportAlerts() {

        List<Alert> alerts =
                alertRepository.findAll();

        StringBuilder csv =
                new StringBuilder();

        csv.append(
                "Id,Name,Severity,Source,Category,Status\n");

        for(Alert alert : alerts) {

            csv.append(alert.getId()).append(",");
            csv.append(alert.getName()).append(",");
            csv.append(alert.getSeverity()).append(",");
            csv.append(alert.getSource()).append(",");
            csv.append(alert.getCategory()).append(",");
            csv.append(alert.getStatus()).append("\n");
        }

        return csv.toString().getBytes();
    }
    private void sendAlertNotification(
            Alert alert) {

        AlertNotificationDto notification =
                AlertNotificationDto.builder()
                        .alertId(alert.getId())
                        .alertName(alert.getName())
                        .severity(alert.getSeverity().name())
                        .message(
                                alert.getName() +
                                " alert triggered")
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                notification);
    }
}