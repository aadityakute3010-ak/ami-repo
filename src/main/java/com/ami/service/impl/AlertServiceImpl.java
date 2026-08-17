package com.ami.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ami.service.AlertAssignmentService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.ami.dto.requests.CreateAlertRequestDto;
import com.ami.dto.requests.UpdateAlertRequestDto;
import com.ami.dto.responses.AlertResponseDto;
import com.ami.dto.responses.AlertSummaryResponseDto;
import com.ami.entity.Alert;
import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;
import com.ami.repository.AlertRepository;
import com.ami.service.AlertService;
import com.ami.service.NotificationManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.ami.dto.responses.AlarmCategoryResponseDto;
import com.ami.dto.responses.AlarmDashboardResponseDto;
import com.ami.dto.responses.AlarmHistoryResponseDto;
import com.ami.dto.responses.AlarmSeverityResponseDto;
import com.ami.dto.responses.AlarmStatisticsResponseDto;
import com.ami.dto.responses.AlarmTimelineResponseDto;
import com.ami.dto.responses.AlertHistoryResponseDto;
import com.ami.dto.responses.AlertNotificationDto;
import com.ami.entity.AlertHistory;
import com.ami.repository.AlertHistoryRepository;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import com.ami.service.NotificationManagementService;
import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.enums.NotificationType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    
    private final AlertHistoryRepository alertHistoryRepository;

    private final SimpMessagingTemplate messagingTemplate;
    
    private final NotificationManagementService
    notificationManagementService;
    
    private final AlertAssignmentService alertAssignmentService;
    
    public AlertServiceImpl(

            AlertRepository alertRepository,

            AlertHistoryRepository alertHistoryRepository,

            SimpMessagingTemplate messagingTemplate,

            NotificationManagementService
                    notificationManagementService,
                    AlertAssignmentService alertAssignmentService) {

        this.alertRepository = alertRepository;
        this.alertHistoryRepository = alertHistoryRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationManagementService =
                notificationManagementService;
        this.alertAssignmentService =
                alertAssignmentService;
    }
    @Override
    public Page<AlertResponseDto> getAllAlerts(

            int page,

            int size,

            String search,

            AlertSeverity severity,

            AlertCategory category,

            AlertSource source,

            Boolean enabled,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort);

        Specification<Alert> spec =
                (root, query, cb) ->
                        cb.equal(
                                root.get("archived"),
                                false);

        if (search != null && !search.isBlank()) {

            spec = spec.and((root, query, cb) ->
                    cb.or(

                            cb.like(
                                    cb.lower(root.get("name")),
                                    "%" + search.toLowerCase() + "%"),

                            cb.like(
                                    cb.lower(root.get("description")),
                                    "%" + search.toLowerCase() + "%")

                    ));
        }

        if (severity != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("severity"),
                            severity));
        }

        if (category != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("category"),
                            category));
        }

        if (source != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("source"),
                            source));
        }

        if (enabled != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("enabled"),
                            enabled));
        }

        return alertRepository.findAll(
                spec,
                pageable)
                .map(this::mapToResponse);
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
                .deviceId(request.getDeviceId())

                .message(request.getMessage())
                .status(AlertStatus.ACTIVE)
                .archived(false)
                .build();

        alert = alertRepository.save(alert);
        sendAlertNotification(alert);
        
        saveHistory(
                alert.getId(),
                "CREATE",
                "Alert created");
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Alert Created")

                        .message(
                                alert.getName()
                                        + " alert created")

                        .recipient("ADMIN")

                        .build());

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
        alert.setDeviceId(request.getDeviceId());

        alert.setMessage(request.getMessage());
        alert.setUnit(request.getUnit());
        alert.setUpdatedAt(LocalDateTime.now());

        alert = alertRepository.save(alert);
        sendAlertNotification(alert);
        
        saveHistory(
                alert.getId(),
                "UPDATE",
                "Alert updated");
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Alert Updated")

                        .message(
                                alert.getName()
                                        + " alert updated")

                        .recipient("ADMIN")

                        .build());

        return mapToResponse(alert);
    }
    @Override
    public String enableAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));

        alert.setEnabled(true);
        alert.setUpdatedAt(LocalDateTime.now());

        alertRepository.save(alert);

        saveHistory(
                alert.getId(),
                "ENABLE",
                "Alert enabled");

        return "Alert enabled successfully";
    }
    @Override
    public String disableAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));

        alert.setEnabled(false);
        alert.setUpdatedAt(LocalDateTime.now());

        alertRepository.save(alert);

        saveHistory(
                alert.getId(),
                "DISABLE",
                "Alert disabled");

        return "Alert disabled successfully";
    }
    @Override
    public String bulkDisableAlerts(
            List<Long> alertIds) {

        if (alertIds == null || alertIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        List<Alert> alerts =
                alertRepository.findAllById(alertIds);

        if (alerts.isEmpty()) {
            throw new RuntimeException(
                    "No alerts found");
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (Alert alert : alerts) {

            alert.setEnabled(false);
            alert.setUpdatedAt(now);

            saveHistory(
                    alert.getId(),
                    "BULK_DISABLE",
                    "Alert disabled through bulk action");
        }

        alertRepository.saveAll(alerts);

        return alerts.size()
                + " alert(s) disabled successfully";
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
    public String archiveAlert(
            Long id,
            String reason) {

        Alert alert =
                alertRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"));

        if (Boolean.TRUE.equals(
                alert.getArchived())) {

            return "Alert is already archived";
        }

        LocalDateTime now =
                LocalDateTime.now();

        alert.setArchived(true);

        alert.setArchivedAt(now);

        alert.setArchivedBy("SYSTEM");

        /*
         * Archived alerts should not remain
         * enabled for monitoring.
         */
        alert.setEnabled(false);

        alert.setUpdatedAt(now);

        alertRepository.save(alert);

        saveHistory(
                alert.getId(),
                "ARCHIVE",
                reason != null &&
                !reason.isBlank()
                        ? reason
                        : "Alert archived");

        return "Alert archived successfully";
    }
    @Override
    public String restoreAlert(
            Long id,
            String reason) {

        Alert alert =
                alertRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"));

        if (!Boolean.TRUE.equals(
                alert.getArchived())) {

            return "Alert is not archived";
        }

        LocalDateTime now =
                LocalDateTime.now();

        alert.setArchived(false);

        alert.setArchivedAt(null);

        alert.setArchivedBy(null);

        /*
         * Restored alert becomes enabled again.
         */
        alert.setEnabled(true);

        alert.setUpdatedAt(now);

        alertRepository.save(alert);

        saveHistory(
                alert.getId(),
                "RESTORE",
                reason != null &&
                !reason.isBlank()
                        ? reason
                        : "Alert restored");

        return "Alert restored successfully";
    }
    @Override
    public String bulkArchiveAlerts(
            List<Long> alertIds,
            String reason) {

        if (alertIds == null ||
                alertIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        List<Alert> alerts =
                alertRepository.findAllById(
                        alertIds);

        if (alerts.isEmpty()) {

            throw new RuntimeException(
                    "No alerts found");
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (Alert alert : alerts) {

            alert.setArchived(true);

            alert.setArchivedAt(now);

            alert.setArchivedBy("SYSTEM");

            alert.setEnabled(false);

            alert.setUpdatedAt(now);

            saveHistory(
                    alert.getId(),
                    "BULK_ARCHIVE",
                    reason != null &&
                    !reason.isBlank()
                            ? reason
                            : "Alert archived through bulk action");
        }

        alertRepository.saveAll(alerts);

        return alerts.size()
                + " alert(s) archived successfully";
    }
    @Override
    public String bulkRestoreAlerts(
            List<Long> alertIds,
            String reason) {

        if (alertIds == null ||
                alertIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        List<Alert> alerts =
                alertRepository.findAllById(
                        alertIds);

        if (alerts.isEmpty()) {

            throw new RuntimeException(
                    "No alerts found");
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (Alert alert : alerts) {

            alert.setArchived(false);

            alert.setArchivedAt(null);

            alert.setArchivedBy(null);

            alert.setEnabled(true);

            alert.setUpdatedAt(now);

            saveHistory(
                    alert.getId(),
                    "BULK_RESTORE",
                    reason != null &&
                    !reason.isBlank()
                            ? reason
                            : "Alert restored through bulk action");
        }

        alertRepository.saveAll(alerts);

        return alerts.size()
                + " alert(s) restored successfully";
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
                .deviceId(alert.getDeviceId())

                .message(alert.getMessage())

                .createdAt(alert.getCreatedAt())

                .updatedAt(alert.getUpdatedAt())

                .resolvedAt(alert.getResolvedAt())

                .acknowledgedAt(alert.getAcknowledgedAt())

                .acknowledgedBy(alert.getAcknowledgedBy())

                .resolvedBy(alert.getResolvedBy())
                .archived(alert.getArchived())
                .archivedAt(alert.getArchivedAt())
                .archivedBy(alert.getArchivedBy())
                .build();
    }
    @Override
    public AlertSummaryResponseDto getSummary() {

        List<Alert> alerts =
                alertRepository.findAll();

        long totalAlerts =
                alerts.size();

        long activeAlerts =
                alerts.stream()
                        .filter(alert ->
                                Boolean.TRUE.equals(
                                        alert.getEnabled()))
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .count();

        long inactiveAlerts =
                alerts.stream()
                        .filter(alert ->
                                Boolean.FALSE.equals(
                                        alert.getEnabled()))
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .count();

        long disabledAlerts =
                alerts.stream()
                        .filter(alert ->
                                Boolean.FALSE.equals(
                                        alert.getEnabled()))
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .count();

        long archivedAlerts =
                alerts.stream()
                        .filter(alert ->
                                Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .count();

        long criticalAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.CRITICAL)
                        .count();

        long highAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.HIGH)
                        .count();

        long mediumAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.MEDIUM)
                        .count();

        long lowAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.LOW)
                        .count();

        long warningAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.WARNING)
                        .count();

        long infoAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSeverity()
                                        == AlertSeverity.INFO)
                        .count();

        long systemAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSource() == null)
                        .count();

        long waterAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSource()
                                        == AlertSource.WATER)
                        .count();

        long gasAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSource()
                                        == AlertSource.GAS)
                        .count();

        long energyAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSource()
                                        == AlertSource.ENERGY)
                        .count();

        long solarAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getSource()
                                        == AlertSource.SOLAR)
                        .count();

        long activeStatusAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getStatus()
                                        == AlertStatus.ACTIVE)
                        .count();

        long resolvedAlerts =
                alerts.stream()
                        .filter(alert ->
                                !Boolean.TRUE.equals(
                                        alert.getArchived()))
                        .filter(alert ->
                                alert.getStatus()
                                        == AlertStatus.RESOLVED)
                        .count();

        return AlertSummaryResponseDto.builder()

                .totalAlerts(totalAlerts)

                .activeAlerts(activeAlerts)

                .inactiveAlerts(inactiveAlerts)

                .disabledAlerts(disabledAlerts)

                .archivedAlerts(archivedAlerts)

                .criticalAlerts(criticalAlerts)

                .highAlerts(highAlerts)

                .mediumAlerts(mediumAlerts)

                .lowAlerts(lowAlerts)

                .warningAlerts(warningAlerts)

                .infoAlerts(infoAlerts)

                .systemAlerts(systemAlerts)

                .waterAlerts(waterAlerts)

                .gasAlerts(gasAlerts)

                .energyAlerts(energyAlerts)

                .solarAlerts(solarAlerts)

                .activeStatusAlerts(
                        activeStatusAlerts)

                .resolvedAlerts(
                        resolvedAlerts)

                /*
                 * Assignment module is not implemented yet.
                 */
                .assignedAlerts(
                        alertAssignmentService
                                .getAssignedAlertCount())

                .unassignedAlerts(
                        alertAssignmentService
                                .getUnassignedAlertCount())

                .totalAdminAssignments(
                        alertAssignmentService
                                .getTotalAdminAssignments())

                .totalDeviceAssignments(
                        alertAssignmentService
                                .getTotalDeviceAssignments())

                /*
                 * Alert trigger/event module is not
                 * implemented yet.
                 */
                .totalTriggers(0L)

                .triggeredToday(0L)

                .triggeredThisWeek(0L)

                .triggeredThisMonth(0L)

                .build();
    }
    @Override
    public AlertSummaryResponseDto getDashboard() {

        return getSummary();
    }
    @Override
    public String deleteAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found"));
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Alert Deleted")

                        .message(
                                alert.getName()
                                        + " alert deleted")

                        .recipient("ADMIN")

                        .build());

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
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        Specification<Alert> specification =
                (root, query, cb) ->
                        cb.equal(
                                root.get("archived"),
                                false);

        return alertRepository
                .findAll(
                        specification,
                        pageable)
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
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Alert Status Updated")

                        .message(
                                "Alert "
                                        + alert.getName()
                                        + " status changed to "
                                        + status)

                        .recipient("ADMIN")

                        .build());

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
    public Page<AlertHistoryResponseDto> getAlertHistory(

            Long alertId,

            int page,

            int size,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort);

        Specification<AlertHistory> spec =
                (root, query, cb) ->
                        cb.equal(
                                root.get("alertId"),
                                alertId);

        return alertHistoryRepository.findAll(
                spec,
                pageable)
                .map(history -> AlertHistoryResponseDto.builder()

                        .id(history.getId())

                        .alertId(history.getAlertId())

                        .action(history.getAction())

                        .description(history.getDescription())

                        .timestamp(history.getTimestamp())

                        .build());
    }
    @Override
    public Page<AlertResponseDto> searchAlerts(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"));

        Specification<Alert> specification =
                (root, query, cb) -> {

                    // Only search non-archived alerts
                    Predicate notArchived =
                            cb.equal(
                                    root.get("archived"),
                                    false);

                    // If no keyword is provided,
                    // return all non-archived alerts
                    if (keyword == null ||
                            keyword.isBlank()) {

                        return notArchived;
                    }

                    String value =
                            "%" +
                            keyword.trim()
                                    .toLowerCase() +
                            "%";

                    Predicate searchPredicate =
                            cb.or(

                                    cb.like(
                                            cb.lower(
                                                    root.get("name")),
                                            value),

                                    cb.like(
                                            cb.lower(
                                                    root.get("description")),
                                            value),

                                    cb.like(
                                            cb.lower(
                                                    root.get("message")),
                                            value),

                                    cb.like(
                                            cb.lower(
                                                    root.get("fieldLabel")),
                                            value)
                            );

                    // archived = false
                    // AND
                    // keyword matches one of the fields
                    return cb.and(
                            notArchived,
                            searchPredicate);
                };

        return alertRepository
                .findAll(
                        specification,
                        pageable)
                .map(this::mapToResponse);
    }
    @Override
    public List<AlertResponseDto> getAlertsBySource(
            AlertSource source) {

        return alertRepository
                .findBySource(source)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<AlertResponseDto> getAlertsBySeverity(
            AlertSeverity severity) {

        return alertRepository
                .findBySeverity(severity)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<AlertResponseDto> getAlertsByCategory(
            AlertCategory category) {

        return alertRepository
                .findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<AlertResponseDto> getAlertsByStatus(
            AlertStatus status) {

        return alertRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public AlertResponseDto duplicateAlert(Long id) {

        Alert original =
                alertRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"));

        Alert duplicate = Alert.builder()

                .name(
                        original.getName()
                                + " - Copy")

                .fieldLabel(
                        original.getFieldLabel())

                .placeholder(
                        original.getPlaceholder())

                .enabled(
                        original.getEnabled())

                .value(
                        original.getValue())

                .deviceId(
                        original.getDeviceId())

                .message(
                        original.getMessage())

                .severity(
                        original.getSeverity())

                .source(
                        original.getSource())

                .category(
                        original.getCategory())

                .description(
                        original.getDescription())

                .unit(
                        original.getUnit())

                /*
                 * A duplicate is a new alert
                 * configuration.
                 */
                .status(
                        AlertStatus.ACTIVE)

                .createdAt(
                        LocalDateTime.now())

                .updatedAt(
                        LocalDateTime.now())

                /*
                 * Duplicate should never
                 * inherit archive state.
                 */
                .archived(false)

                .build();

        Alert saved =
                alertRepository.save(duplicate);

        saveHistory(
                saved.getId(),
                "DUPLICATE",
                "Alert duplicated from alert ID "
                        + original.getId());

        return mapToResponse(saved);
    }
    @Override
    public List<AlertResponseDto> getRecentAlerts() {

        return alertRepository
                .findTop10ByArchivedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public String importAlerts(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new RuntimeException("Invalid file");
        }

        int totalRecords = 0;
        int importedRecords = 0;
        int skippedRecords = 0;

        StringBuilder errors = new StringBuilder();

        try {

            // ================= CSV IMPORT =================
            if (fileName.toLowerCase().endsWith(".csv")) {

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        file.getInputStream()));

                CSVParser parser =
                        CSVFormat.DEFAULT
                                .builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .build()
                                .parse(reader);

                for (CSVRecord record : parser) {

                    totalRecords++;

                    try {

                        String name =
                                record.get("name").trim();

                        String fieldLabel =
                                record.get("fieldLabel").trim();

                        String placeholder =
                                record.get("placeholder").trim();

                        String enabled =
                                record.get("enabled").trim();

                        String value =
                                record.get("value").trim();

                        String severity =
                                record.get("severity").trim();

                        String source =
                                record.get("source").trim();

                        String category =
                                record.get("category").trim();

                        String description =
                                record.get("description").trim();

                        String unit =
                                record.get("unit").trim();

                        String status =
                                record.isMapped("status")
                                        ? record.get("status").trim()
                                        : "ACTIVE";
                        // ========= Validation =========

                        if (name.isBlank()
                                || fieldLabel.isBlank()
                                || severity.isBlank()
                                || source.isBlank()
                                || category.isBlank()
                                
                                ) {

                            skippedRecords++;

                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Mandatory field missing\n");

                            continue;
                        }

                        if (alertRepository
                                .findByNameIgnoreCase(name)
                                .isPresent()) {

                            skippedRecords++;

                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Alert already exists\n");

                            continue;
                        }

                        Boolean enabledValue =
                                enabled.equalsIgnoreCase("true")
                                || enabled.equalsIgnoreCase("yes")
                                || enabled.equals("1");

                        AlertSeverity severityEnum;
                        AlertSource sourceEnum;
                        AlertCategory categoryEnum;
                        AlertStatus statusEnum;

                        try {
                            severityEnum = AlertSeverity.valueOf(severity.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Severity\n");
                            continue;
                        }

                        try {
                            sourceEnum = AlertSource.valueOf(source.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Source\n");
                            continue;
                        }

                        try {
                            categoryEnum = AlertCategory.valueOf(category.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Category\n");
                            continue;
                        }

                        try {
                            statusEnum = status.isBlank()
                                    ? AlertStatus.ACTIVE
                                    : AlertStatus.valueOf(status.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Status\n");
                            continue;
                        }

                        Alert alert = Alert.builder()
                                .name(name)
                                .fieldLabel(fieldLabel)
                                .placeholder(placeholder)
                                .enabled(enabledValue)
                                .value(value)
                                .severity(severityEnum)
                                .source(sourceEnum)
                                .category(categoryEnum)
                                .description(description)
                                .unit(unit)
                                .status(statusEnum)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .archived(false)
                                .build();

                        alertRepository.save(alert);

                        saveHistory(
                                alert.getId(),
                                "IMPORT",
                                "Imported from CSV");

                        importedRecords++;

                    } catch (Exception ex) {

                        skippedRecords++;

                        errors.append("Row ")
                                .append(totalRecords + 1)
                                .append(" : ")
                                .append(ex.getMessage())
                                .append("\n");
                    }
                }

            }
            

            // ================= EXCEL IMPORT =================
            else if (fileName.toLowerCase().endsWith(".xlsx")
                    || fileName.toLowerCase().endsWith(".xls")) {

            	Workbook workbook =
            	        WorkbookFactory.create(file.getInputStream());

            	Sheet sheet = workbook.getSheetAt(0);

            	DataFormatter formatter = new DataFormatter();

            	boolean firstRow = true;

            	for (Row row : sheet) {

            	    if (firstRow) {
            	        firstRow = false;
            	        continue;
            	    }

            	    if (row == null) {
            	        continue;
            	    }

            	    boolean emptyRow = true;

            	    for (Cell cell : row) {

            	        if (!formatter.formatCellValue(cell).trim().isEmpty()) {
            	            emptyRow = false;
            	            break;
            	        }
            	    }

            	    if (emptyRow) {
            	        continue;
            	    }
            	    totalRecords++;

            	    try {
                        String name =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        0,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String fieldLabel =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        1,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String placeholder =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        2,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String enabled =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        3,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String value =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        4,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String severity =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        5,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String source =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        6,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String category =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        7,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String description =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        8,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String unit =
                        		formatter.formatCellValue(
                        			    row.getCell(
                        			        9,
                        			        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        			    )
                        			).trim();

                        String status =
                                row.getCell(10) != null
                                        ? formatter.formatCellValue(
                                                row.getCell(
                                                        10,
                                                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                                                .trim()
                                                
                                        : "ACTIVE";
                        if (name.isBlank()
                                || fieldLabel.isBlank()
                                || severity.isBlank()
                                || source.isBlank()
                                || category.isBlank()
                              
                                ) {

                            skippedRecords++;

                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Mandatory field missing\n");

                            continue;
                        }

                        if (alertRepository
                                .findByNameIgnoreCase(name)
                                .isPresent()) {

                            skippedRecords++;

                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Alert already exists\n");

                            continue;
                        }

                        Boolean enabledValue =
                                enabled.equalsIgnoreCase("true")
                                || enabled.equalsIgnoreCase("yes")
                                || enabled.equals("1");

                        AlertSeverity severityEnum;
                        AlertSource sourceEnum;
                        AlertCategory categoryEnum;
                        AlertStatus statusEnum;

                        try {
                            severityEnum = AlertSeverity.valueOf(severity.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Severity\n");
                            continue;
                        }

                        try {
                            sourceEnum = AlertSource.valueOf(source.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Source\n");
                            continue;
                        }

                        try {
                            categoryEnum = AlertCategory.valueOf(category.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Category\n");
                            continue;
                        }

                        try {
                            statusEnum = status.isBlank()
                                    ? AlertStatus.ACTIVE
                                    : AlertStatus.valueOf(status.toUpperCase());
                        } catch (Exception e) {
                            skippedRecords++;
                            errors.append("Row ")
                                    .append(totalRecords + 1)
                                    .append(" : Invalid Status\n");
                            continue;
                        }

                        Alert alert = Alert.builder()
                                .name(name)
                                .fieldLabel(fieldLabel)
                                .placeholder(placeholder)
                                .enabled(enabledValue)
                                .value(value)
                                .severity(severityEnum)
                                .source(sourceEnum)
                                .category(categoryEnum)
                                .description(description)
                                .unit(unit)
                                .status(statusEnum)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        alertRepository.save(alert);

                        saveHistory(
                                alert.getId(),
                                "IMPORT",
                                "Imported from Excel");

                        importedRecords++;

                    } catch (Exception ex) {

                        skippedRecords++;

                        errors.append("Row ")
                                .append(totalRecords + 1)
                                .append(" : ")
                                .append(ex.getMessage())
                                .append("\n");
                    }
                }

                workbook.close();
                

            } else {

                throw new RuntimeException(
                        "Unsupported file type");
            }

            StringBuilder summary = new StringBuilder();

            summary.append("Import Completed\n");
            summary.append("-------------------------\n");
            summary.append("Total Records : ")
                    .append(totalRecords)
                    .append("\n");

            summary.append("Imported : ")
                    .append(importedRecords)
                    .append("\n");

            summary.append("Skipped : ")
                    .append(skippedRecords)
                    .append("\n");

            if (errors.length() > 0) {

                summary.append("\nErrors\n");
                summary.append("-------------------------\n");
                summary.append(errors);
            }

            return summary.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to import alerts : " + e.getMessage(),
                    e);
        }
    }
    @Override
    public byte[] exportAlerts(
            String format,
            String search,
            AlertSeverity severity,
            AlertCategory category,
            AlertSource source,
            Boolean enabled) {

        Specification<Alert> specification =
                (root, query, cb) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    /*
                     * Exclude archived alerts
                     * from normal exports.
                     */
                    predicates.add(
                            cb.equal(
                                    root.get("archived"),
                                    false));

                    /*
                     * Search filter
                     */
                    if (search != null &&
                            !search.isBlank()) {

                        String searchValue =
                                "%" +
                                search.trim()
                                        .toLowerCase() +
                                "%";

                        predicates.add(
                                cb.or(

                                        cb.like(
                                                cb.lower(
                                                        root.get("name")),
                                                searchValue),

                                        cb.like(
                                                cb.lower(
                                                        root.get("description")),
                                                searchValue),

                                        cb.like(
                                                cb.lower(
                                                        root.get("message")),
                                                searchValue),

                                        cb.like(
                                                cb.lower(
                                                        root.get("fieldLabel")),
                                                searchValue)
                                ));
                    }

                    /*
                     * Severity filter
                     */
                    if (severity != null) {

                        predicates.add(
                                cb.equal(
                                        root.get("severity"),
                                        severity));
                    }

                    /*
                     * Category filter
                     */
                    if (category != null) {

                        predicates.add(
                                cb.equal(
                                        root.get("category"),
                                        category));
                    }

                    /*
                     * Source filter
                     */
                    if (source != null) {

                        predicates.add(
                                cb.equal(
                                        root.get("source"),
                                        source));
                    }

                    /*
                     * Enabled filter
                     */
                    if (enabled != null) {

                        predicates.add(
                                cb.equal(
                                        root.get("enabled"),
                                        enabled));
                    }

                    return cb.and(
                            predicates.toArray(
                                    new Predicate[0]));
                };

        /*
         * Get filtered alerts.
         */
        List<Alert> alerts =
                alertRepository.findAll(
                        specification,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"));

        /*
         * Export according to requested format.
         */
        switch (format.toLowerCase()) {

            case "excel":
                return exportExcel(alerts);

            case "pdf":
                return exportPdf(alerts);

            case "csv":
            default:
                return exportCsv(alerts);
        }
    }

    private byte[] exportCsv(List<Alert> alerts) {

        StringBuilder csv = new StringBuilder();

        csv.append("Id,Name,Severity,Source,Category,Status\n");

        for (Alert alert : alerts) {

            csv.append(alert.getId()).append(",");
            csv.append(alert.getName()).append(",");
            csv.append(alert.getSeverity()).append(",");
            csv.append(alert.getSource()).append(",");
            csv.append(alert.getCategory()).append(",");
            csv.append(alert.getStatus()).append("\n");
        }

        return csv.toString().getBytes();
    }

    private byte[] exportExcel(List<Alert> alerts) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Alerts");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Severity");
            header.createCell(3).setCellValue("Source");
            header.createCell(4).setCellValue("Category");
            header.createCell(5).setCellValue("Status");

            int rowNum = 1;

            for (Alert alert : alerts) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(alert.getId());

                row.createCell(1).setCellValue(alert.getName());

                row.createCell(2).setCellValue(
                        alert.getSeverity() != null
                                ? alert.getSeverity().name()
                                : "");

                row.createCell(3).setCellValue(
                        alert.getSource() != null
                                ? alert.getSource().name()
                                : "");

                row.createCell(4).setCellValue(
                        alert.getCategory() != null
                                ? alert.getCategory().name()
                                : "");

                row.createCell(5).setCellValue(
                        alert.getStatus() != null
                                ? alert.getStatus().name()
                                : "");
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export alerts to Excel", e);
        }
    }
    private byte[] exportPdf(List<Alert> alerts) {

        try {

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    out);

            document.open();

            document.add(
                    new Paragraph("Alert Report"));

            document.add(
                    new Paragraph(" "));

            PdfPTable table =
                    new PdfPTable(6);

            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Severity");
            table.addCell("Source");
            table.addCell("Category");
            table.addCell("Status");

            for (Alert alert : alerts) {

                table.addCell(
                        String.valueOf(
                                alert.getId()));

                table.addCell(
                        alert.getName());

                table.addCell(
                        alert.getSeverity() != null
                                ? alert.getSeverity().name()
                                : "");

                table.addCell(
                        alert.getSource() != null
                                ? alert.getSource().name()
                                : "");

                table.addCell(
                        alert.getCategory() != null
                                ? alert.getCategory().name()
                                : "");

                table.addCell(
                        alert.getStatus() != null
                                ? alert.getStatus().name()
                                : "");
            }

            document.add(table);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export alerts to PDF", e);
        }
    }
    private void sendAlertNotification(Alert alert) {

        AlertNotificationDto notification =
                AlertNotificationDto.builder()
                        .alertId(alert.getId())
                        .alertName(alert.getName())
                        .severity(alert.getSeverity().name())
                        .message(alert.getName() + " alert triggered")
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                notification);
    }
    
    @Override
    public AlarmDashboardResponseDto getAlarmDashboard() {

        List<Alert> alerts = alertRepository.findAll();

        return AlarmDashboardResponseDto.builder()

                .totalAlarms(
                        (long) alerts.size())

                .activeAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getStatus() == AlertStatus.ACTIVE)
                                .count())

                .resolvedAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getStatus() == AlertStatus.RESOLVED)
                                .count())

                .criticalAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getSeverity() == AlertSeverity.CRITICAL)
                                .count())

                .highAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getSeverity() == AlertSeverity.HIGH)
                                .count())

                .mediumAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getSeverity() == AlertSeverity.MEDIUM)
                                .count())

                .lowAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getSeverity() == AlertSeverity.LOW)
                                .count())

                .acknowledgedAlarms(
                        alerts.stream()
                                .filter(a ->
                                        a.getStatus() == AlertStatus.ACKNOWLEDGED)
                                .count())

                .escalatedAlarms(0L)

                .build();
    }
    @Override
    public AlarmStatisticsResponseDto getAlarmStatistics() {

        List<Alert> alerts = alertRepository.findAll();

        return AlarmStatisticsResponseDto.builder()

                .totalAlarms((long) alerts.size())

                .activeAlarms(
                        alerts.stream()
                                .filter(a -> a.getStatus() == AlertStatus.ACTIVE)
                                .count())

                .acknowledgedAlarms(
                        alerts.stream()
                                .filter(a -> a.getStatus() == AlertStatus.ACKNOWLEDGED)
                                .count())

                .resolvedAlarms(
                        alerts.stream()
                                .filter(a -> a.getStatus() == AlertStatus.RESOLVED)
                                .count())

                .ignoredAlarms(
                        alerts.stream()
                                .filter(a -> a.getStatus() == AlertStatus.IGNORED)
                                .count())

                .criticalAlarms(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL)
                                .count())

                .highAlarms(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.HIGH)
                                .count())

                .mediumAlarms(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.MEDIUM)
                                .count())

                .lowAlarms(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.LOW)
                                .count())

                .build();
    }
    @Override
    public List<AlarmTimelineResponseDto> getAlarmTimeline(
            Long alertId) {

        return alertHistoryRepository
                .findByAlertId(alertId)
                .stream()
                .map(history ->

                        AlarmTimelineResponseDto.builder()

                                .alertId(
                                        history.getAlertId())

                                .action(
                                        history.getAction())

                                .description(
                                        history.getDescription())

                                .timestamp(
                                        history.getTimestamp())

                                .build())

                .toList();
    }
    @Override
    public List<AlarmHistoryResponseDto> getAlarmHistory(
            Long alertId) {

        return alertHistoryRepository
                .findByAlertId(alertId)
                .stream()
                .map(history ->

                        AlarmHistoryResponseDto.builder()

                                .id(
                                        history.getId())

                                .alertId(
                                        history.getAlertId())

                                .action(
                                        history.getAction())

                                .description(
                                        history.getDescription())

                                .timestamp(
                                        history.getTimestamp())

                                .build())

                .toList();
    }
    @Override
    public AlarmSeverityResponseDto getAlarmSeverity() {

        List<Alert> alerts = alertRepository.findAll();

        return AlarmSeverityResponseDto.builder()

                .critical(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL)
                                .count())

                .high(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.HIGH)
                                .count())

                .medium(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.MEDIUM)
                                .count())

                .low(
                        alerts.stream()
                                .filter(a -> a.getSeverity() == AlertSeverity.LOW)
                                .count())

                .build();
    }
    @Override
    public AlarmCategoryResponseDto getAlarmCategory() {

        List<Alert> alerts = alertRepository.findAll();

        return AlarmCategoryResponseDto.builder()

                .battery(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.BATTERY)
                                .count())

                .tamper(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.TAMPER)
                                .count())

                .communication(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.COMMUNICATION)
                                .count())

                .valve(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.VALVE)
                                .count())

                .recharge(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.RECHARGE)
                                .count())

                .consumption(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.CONSUMPTION)
                                .count())

                .system(
                        alerts.stream()
                                .filter(a ->
                                        a.getCategory() == AlertCategory.SYSTEM)
                                .count())

                .build();
    }
}