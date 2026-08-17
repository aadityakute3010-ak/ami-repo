package com.ami.service.impl;
import com.ami.entity.Device;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ami.dto.requests.AssignAdminAlertsRequestDto;
import com.ami.dto.requests.AssignAlertRequestDto;
import com.ami.dto.requests.AssignDeviceAlertsRequestDto;
import com.ami.dto.requests.BulkAssignAlertsRequestDto;
import com.ami.dto.requests.BulkAssignDeviceAlertsRequestDto;
import com.ami.dto.responses.AdminAlertAssignmentPageResponseDto;
import com.ami.dto.responses.AdminAlertAssignmentResponseDto;
import com.ami.dto.responses.AlertAssignmentOverviewResponseDto;
import com.ami.dto.responses.AlertAssignmentResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentPageResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentResponseDto;
import com.ami.dto.responses.AssignedAdminSummaryDto;
import com.ami.dto.responses.AssignedAlertSummaryDto;
import com.ami.dto.responses.AssignedDeviceSummaryDto;
import com.ami.dto.responses.DeviceAlertAssignmentPageResponseDto;
import com.ami.dto.responses.DeviceAlertAssignmentResponseDto;
import com.ami.entity.Alert;
import com.ami.entity.AlertAssignment;
import com.ami.entity.AlertAssignment.AssignmentType;
import com.ami.repository.AlertAssignmentRepository;
import com.ami.repository.AlertRepository;
import com.ami.repository.DeviceRepository;
import com.ami.service.AlertAssignmentService;
import com.ami.repository.UserRepository;
import com.ami.entity.User;
import com.ami.enums.RoleType;

@Service
@Transactional
public class AlertAssignmentServiceImpl
        implements AlertAssignmentService {

	private final AlertAssignmentRepository alertAssignmentRepository;

	private final AlertRepository alertRepository;

	private final UserRepository userRepository;

	private final DeviceRepository deviceRepository;

	public AlertAssignmentServiceImpl(
	        AlertAssignmentRepository alertAssignmentRepository,
	        AlertRepository alertRepository,
	        UserRepository userRepository,
	        DeviceRepository deviceRepository) {

	    this.alertAssignmentRepository =
	            alertAssignmentRepository;

	    this.alertRepository =
	            alertRepository;

	    this.userRepository =
	            userRepository;

	    this.deviceRepository =
	            deviceRepository;
	}

    // =========================================================
    // PHASE 3 - ADMIN ASSIGNMENT
    // =========================================================

    @Override
    public List<AlertAssignmentResponseDto> assignAlertsToAdmins(
            AssignAdminAlertsRequestDto request) {

        validateAdminAssignmentRequest(request);
        
        validateAdminIds(
                request.getAdminIds());

        Long alertId = request.getAlertId();

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found with ID: "
                                        + alertId));

        List<AlertAssignmentResponseDto> results =
                new ArrayList<>();

        for (Long adminId : request.getAdminIds()) {

            if (adminId == null) {
                continue;
            }

            boolean alreadyAssigned =
                    alertAssignmentRepository
                            .findByAlertIdAndAdminIdAndActiveTrue(
                                    alertId,
                                    adminId)
                            .isPresent();

            if (alreadyAssigned) {
                continue;
            }

            AlertAssignment assignment =
                    AlertAssignment.builder()
                            .alertId(alert.getId())
                            .assignmentType(
                                    AssignmentType.ADMIN)
                            .adminId(adminId)
                            .deviceId(null)
                            .assignedBy(
                                    getCurrentUsername())
                            .reason(null)
                            .active(true)
                            .build();

            AlertAssignment saved =
                    alertAssignmentRepository.save(
                            assignment);

            results.add(
                    mapToResponse(saved));
        }

        return results;
    }

    // =========================================================
    // BULK ADMIN ASSIGNMENT
    // =========================================================

    @Override
    public List<AlertAssignmentResponseDto>
    bulkAssignAlertsToAdmins(
            BulkAssignAlertsRequestDto request) {

        validateBulkAdminAssignmentRequest(request);
        
        validateAdminIds(
                request.getAdminIds());

        List<AlertAssignmentResponseDto> results =
                new ArrayList<>();

        for (Long alertId : request.getAlertIds()) {

            if (alertId == null) {
                continue;
            }

            Alert alert =
                    alertRepository.findById(alertId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Alert not found with ID: "
                                                    + alertId));

            for (Long adminId : request.getAdminIds()) {

                if (adminId == null) {
                    continue;
                }

                boolean alreadyAssigned =
                        alertAssignmentRepository
                                .findByAlertIdAndAdminIdAndActiveTrue(
                                        alertId,
                                        adminId)
                                .isPresent();

                if (alreadyAssigned) {
                    continue;
                }

                AlertAssignment assignment =
                        AlertAssignment.builder()
                                .alertId(alert.getId())
                                .assignmentType(
                                        AssignmentType.ADMIN)
                                .adminId(adminId)
                                .deviceId(null)
                                .assignedBy(
                                        getCurrentUsername())
                                .reason(null)
                                .active(true)
                                .build();

                AlertAssignment saved =
                        alertAssignmentRepository.save(
                                assignment);

                results.add(
                        mapToResponse(saved));
            }
        }

        return results;
    }

    // =========================================================
    // GET ADMIN ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto>
    getAssignmentsByAdmin(Long adminId) {

        if (adminId == null) {

            throw new IllegalArgumentException(
                    "Admin ID cannot be null");
        }

        return alertAssignmentRepository
                .findByAdminIdAndActiveTrue(adminId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET ALERT ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto>
    getAssignmentsByAlert(Long alertId) {

        if (alertId == null) {

            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        return alertAssignmentRepository
                .findByAlertIdAndActiveTrue(alertId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // REMOVE ADMIN FROM ALERT
    // =========================================================

    @Override
    public String removeAdminFromAlert(
            Long alertId,
            Long adminId) {

        if (alertId == null) {

            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        if (adminId == null) {

            throw new IllegalArgumentException(
                    "Admin ID cannot be null");
        }

        AlertAssignment assignment =
                alertAssignmentRepository
                        .findByAlertIdAndAdminIdAndActiveTrue(
                                alertId,
                                adminId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active admin assignment not found"));

        assignment.setActive(false);

        alertAssignmentRepository.save(
                assignment);

        return "Admin removed from alert successfully";
    }

    // =========================================================
    // ENABLE ASSIGNMENT
    // =========================================================

    @Override
    public AlertAssignmentResponseDto enableAssignment(
            Long assignmentId) {

        AlertAssignment assignment =
                getAssignment(assignmentId);

        assignment.setActive(true);

        AlertAssignment saved =
                alertAssignmentRepository.save(
                        assignment);

        return mapToResponse(saved);
    }

    // =========================================================
    // DISABLE ASSIGNMENT
    // =========================================================

    @Override
    public AlertAssignmentResponseDto disableAssignment(
            Long assignmentId) {

        AlertAssignment assignment =
                getAssignment(assignmentId);

        assignment.setActive(false);

        AlertAssignment saved =
                alertAssignmentRepository.save(
                        assignment);

        return mapToResponse(saved);
    }

    // =========================================================
    // CHECK ADMIN ASSIGNMENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public boolean isAlertAssignedToAdmin(
            Long alertId,
            Long adminId) {

        if (alertId == null ||
                adminId == null) {

            return false;
        }

        return alertAssignmentRepository
                .findByAlertIdAndAdminIdAndActiveTrue(
                        alertId,
                        adminId)
                .isPresent();
    }

    // =========================================================
    // ASSIGNMENT OVERVIEW
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public AdminAlertAssignmentPageResponseDto getAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        /*
         * Get active assignments.
         */
        List<AlertAssignment> assignments =
                alertAssignmentRepository
                        .findByActiveTrue();

        /*
         * Group assignments by admin.
         */
        Map<Long, AdminAlertAssignmentResponseDto> adminMap =
                new LinkedHashMap<>();

        for (AlertAssignment assignment : assignments) {

            if (assignment.getAdminId() == null) {
                continue;
            }

            Long adminId =
                    assignment.getAdminId();

            AdminAlertAssignmentResponseDto adminResponse =
                    adminMap.get(adminId);

            /*
             * Create admin entry if it doesn't exist.
             */
            if (adminResponse == null) {

                User admin =
                        userRepository.findById(adminId)
                                .orElse(null);

                String adminName = null;
                String adminEmail = null;

                if (admin != null) {

                    adminName =
                            buildUserName(admin);

                    adminEmail =
                            admin.getEmail();
                }

                adminResponse =
                        AdminAlertAssignmentResponseDto
                                .builder()
                                .adminId(adminId)
                                .adminName(adminName)
                                .adminEmail(adminEmail)
                                .assignedBy(
                                        assignment.getAssignedBy())
                                .assignedOn(
                                        assignment.getAssignedAt())
                                .status(
                                        Boolean.TRUE.equals(
                                                assignment.getActive())
                                                ? "ACTIVE"
                                                : "INACTIVE")
                                .sources(
                                        new java.util.ArrayList<>())
                                .alerts(
                                        new java.util.ArrayList<>())
                                .build();

                adminMap.put(
                        adminId,
                        adminResponse);
            }

            /*
             * Add alert information.
             */
            if (assignment.getAlertId() != null) {

                Alert alert =
                        alertRepository.findById(
                                assignment.getAlertId())
                                .orElse(null);

                if (alert != null) {

                    AssignedAlertSummaryDto alertSummary =
                            AssignedAlertSummaryDto
                                    .builder()
                                    .alertId(
                                            alert.getId())
                                    .alertName(
                                            alert.getName())
                                    .source(
                                            alert.getSource())
                                    .severity(
                                            alert.getSeverity())
                                    .build();

                    adminResponse
                            .getAlerts()
                            .add(alertSummary);

                    /*
                     * Add unique source.
                     */
                    if (alert.getSource() != null) {

                        String source =
                                alert.getSource()
                                        .name();

                        if (!adminResponse
                                .getSources()
                                .contains(source)) {

                            adminResponse
                                    .getSources()
                                    .add(source);
                        }
                    }
                }
            }
        }

        /*
         * Convert grouped map to list.
         */
        List<AdminAlertAssignmentResponseDto> results =
                new java.util.ArrayList<>(
                        adminMap.values());

        /*
         * Search.
         */
        if (search != null &&
                !search.isBlank()) {

            String keyword =
                    search.trim()
                            .toLowerCase();

            results =
                    results.stream()
                            .filter(item ->

                                    String.valueOf(
                                            item.getAdminId())
                                            .contains(keyword)

                                    ||

                                    (item.getAdminName() != null
                                            && item.getAdminName()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getAdminEmail() != null
                                            && item.getAdminEmail()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getSources() != null
                                            && item.getSources()
                                            .stream()
                                            .anyMatch(source ->
                                                    source
                                                            .toLowerCase()
                                                            .contains(keyword)))

                                    ||

                                    (item.getAlerts() != null
                                            && item.getAlerts()
                                            .stream()
                                            .anyMatch(alert ->

                                                    (alert.getAlertName() != null
                                                            && alert.getAlertName()
                                                            .toLowerCase()
                                                            .contains(keyword))

                                                    ||

                                                    String.valueOf(
                                                            alert.getAlertId())
                                                            .contains(keyword)
                                            ))
                            )
                            .collect(Collectors.toList());
        }

        /*
         * Sort.
         */
        Comparator<AdminAlertAssignmentResponseDto>
                comparator =
                        Comparator.comparing(
                                AdminAlertAssignmentResponseDto
                                        ::getAssignedOn,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()));

        if ("adminName".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AdminAlertAssignmentResponseDto
                                    ::getAdminName,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("adminEmail".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AdminAlertAssignmentResponseDto
                                    ::getAdminEmail,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("assignedOn".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AdminAlertAssignmentResponseDto
                                    ::getAssignedOn,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()));
        }

        if ("status".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AdminAlertAssignmentResponseDto
                                    ::getStatus,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("ASC".equalsIgnoreCase(sortDirection)) {

            results.sort(comparator);

        } else {

            results.sort(
                    comparator.reversed());
        }

        /*
         * Total elements AFTER search.
         */
        long totalElements =
                results.size();

        /*
         * Total pages.
         */
        int totalPages =
                size == 0
                        ? 0
                        : (int) Math.ceil(
                                (double) totalElements
                                        / size);

        /*
         * Pagination.
         */
        int fromIndex =
                page * size;

        List<AdminAlertAssignmentResponseDto>
                pagedResults;

        if (fromIndex >= totalElements) {

            pagedResults =
                    new java.util.ArrayList<>();

        } else {

            int toIndex =
                    Math.min(
                            fromIndex + size,
                            results.size());

            pagedResults =
                    results.subList(
                            fromIndex,
                            toIndex);
        }

        return AdminAlertAssignmentPageResponseDto
                .builder()
                .content(pagedResults)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .number(page)
                .size(size)
                .first(page == 0)
                .last(
                        totalPages == 0 ||
                        page >= totalPages - 1)
                .empty(pagedResults.isEmpty())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public AlertAssignmentOverviewResponseDto getCompleteAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection) {

        AdminAlertAssignmentPageResponseDto byAdmin =
                getAssignmentOverview(
                        page,
                        size,
                        search,
                        sortBy,
                        sortDirection);

        AlertRuleAssignmentPageResponseDto byAlertRule =
                getAlertRuleAssignments(
                        page,
                        size,
                        search,
                        sortBy,
                        sortDirection);

        DeviceAlertAssignmentPageResponseDto byDevice =
                getDeviceAssignmentOverview(
                        page,
                        size,
                        search,
                        sortBy,
                        sortDirection);

        return AlertAssignmentOverviewResponseDto
                .builder()
                .byAdmin(byAdmin)
                .byDevice(byDevice)
                .byAlertRule(byAlertRule)
                .build();
    }
    // =========================================================
    // EXISTING GENERIC ASSIGNMENT
    // =========================================================

    @Override
    public AlertAssignmentResponseDto assignAlert(
            Long alertId,
            AssignAlertRequestDto request) {

        if (alertId == null) {

            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        if (request == null) {

            throw new IllegalArgumentException(
                    "Assignment request cannot be null");
        }

        Alert alert =
                alertRepository.findById(alertId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found with ID: "
                                                + alertId));

        AssignmentType assignmentType =
                parseAssignmentType(
                        request.getAssignmentType());

        validateAssignmentRequest(
                assignmentType,
                request);

        if (assignmentType ==
                AssignmentType.ADMIN) {

            boolean alreadyAssigned =
                    alertAssignmentRepository
                            .findByAlertIdAndAdminIdAndActiveTrue(
                                    alertId,
                                    request.getAdminId())
                            .isPresent();

            if (alreadyAssigned) {

                throw new RuntimeException(
                        "Alert is already assigned to this admin");
            }
        }

        if (assignmentType ==
                AssignmentType.DEVICE) {

            boolean alreadyAssigned =
                    alertAssignmentRepository
                            .findByAlertIdAndDeviceIdAndActiveTrue(
                                    alertId,
                                    request.getDeviceId())
                            .isPresent();

            if (alreadyAssigned) {

                throw new RuntimeException(
                        "Alert is already assigned to this device");
            }
        }

        AlertAssignment assignment =
                AlertAssignment.builder()
                        .alertId(alert.getId())
                        .assignmentType(
                                assignmentType)
                        .adminId(
                                assignmentType ==
                                        AssignmentType.ADMIN
                                        ? request.getAdminId()
                                        : null)
                        .deviceId(
                                assignmentType ==
                                        AssignmentType.DEVICE
                                        ? request.getDeviceId()
                                        : null)
                        .assignedBy(
                                getCurrentUsername())
                        .reason(
                                request.getReason())
                        .active(true)
                        .build();

        AlertAssignment saved =
                alertAssignmentRepository.save(
                        assignment);

        return mapToResponse(saved);
    }

    // =========================================================
    // UNASSIGN
    // =========================================================

    @Override
    public String unassignAlert(
            Long assignmentId) {

        AlertAssignment assignment =
                getAssignment(assignmentId);

        if (!Boolean.TRUE.equals(
                assignment.getActive())) {

            return "Assignment is already inactive";
        }

        assignment.setActive(false);

        alertAssignmentRepository.save(
                assignment);

        return "Alert assignment removed successfully";
    }

    // =========================================================
    // REASSIGN
    // =========================================================

    @Override
    public AlertAssignmentResponseDto reassignAlert(
            Long assignmentId,
            AssignAlertRequestDto request) {

        AlertAssignment existing =
                getAssignment(assignmentId);

        if (!Boolean.TRUE.equals(
                existing.getActive())) {

            throw new RuntimeException(
                    "Cannot reassign an inactive assignment");
        }

        AssignmentType newType =
                parseAssignmentType(
                        request.getAssignmentType());

        validateAssignmentRequest(
                newType,
                request);

        existing.setActive(false);

        alertAssignmentRepository.save(
                existing);

        AlertAssignment newAssignment =
                AlertAssignment.builder()
                        .alertId(
                                existing.getAlertId())
                        .assignmentType(
                                newType)
                        .adminId(
                                newType ==
                                        AssignmentType.ADMIN
                                        ? request.getAdminId()
                                        : null)
                        .deviceId(
                                newType ==
                                        AssignmentType.DEVICE
                                        ? request.getDeviceId()
                                        : null)
                        .assignedBy(
                                getCurrentUsername())
                        .reason(
                                request.getReason())
                        .active(true)
                        .build();

        AlertAssignment saved =
                alertAssignmentRepository.save(
                        newAssignment);

        return mapToResponse(saved);
    }

    // =========================================================
    // ASSIGNMENT HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto>
    getAssignmentHistory(Long alertId) {

        if (alertId == null) {

            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        return alertAssignmentRepository
                .findByAlertId(alertId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // ALL ACTIVE ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto>
    getAllActiveAssignments() {

        return alertAssignmentRepository
                .findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // TOTAL ACTIVE ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getTotalActiveAssignments() {

        return alertAssignmentRepository
                .countByActiveTrue();
    }

    // =========================================================
    // TOTAL ADMIN ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getTotalAdminAssignments() {

        return alertAssignmentRepository
                .countByAssignmentTypeAndActiveTrue(
                        AssignmentType.ADMIN);
    }

    // =========================================================
    // TOTAL DEVICE ASSIGNMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getTotalDeviceAssignments() {

        return alertAssignmentRepository
                .countByAssignmentTypeAndActiveTrue(
                        AssignmentType.DEVICE);
    }

    // =========================================================
    // ASSIGNED ALERT COUNT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getAssignedAlertCount() {

        return alertAssignmentRepository
                .findByActiveTrue()
                .stream()
                .map(AlertAssignment::getAlertId)
                .distinct()
                .count();
    }

    // =========================================================
    // UNASSIGNED ALERT COUNT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getUnassignedAlertCount() {

        long activeAlerts =
                alertRepository.countByArchived(false);

        long assignedAlerts =
                getAssignedAlertCount();

        return Math.max(
                0,
                activeAlerts - assignedAlerts);
    }

    // =========================================================
    // GET ASSIGNMENTS BY DEVICE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto>
    getAssignmentsByDevice(String deviceId) {

        if (deviceId == null ||
                deviceId.isBlank()) {

            throw new IllegalArgumentException(
                    "Device ID cannot be empty");
        }

        return alertAssignmentRepository
                .findByDeviceIdAndActiveTrue(deviceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AlertRuleAssignmentResponseDto getAlertRuleAssignment(
            Long alertId) {

        Alert alert =
                alertRepository.findById(alertId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found with ID: "
                                                + alertId));

        List<AlertAssignment> assignments =
                alertAssignmentRepository
                        .findByAlertIdAndActiveTrue(alertId);

        List<AssignedAdminSummaryDto> assignedAdmins =
                new ArrayList<>();

        List<AssignedDeviceSummaryDto> assignedDevices =
                new ArrayList<>();

        LocalDateTime assignedOn = null;

        for (AlertAssignment assignment : assignments) {

            if (assignedOn == null ||
                    (assignment.getAssignedAt() != null &&
                     assignment.getAssignedAt()
                             .isBefore(assignedOn))) {

                assignedOn =
                        assignment.getAssignedAt();
            }

            // =====================================================
            // ADMIN ASSIGNMENT
            // =====================================================

            if (assignment.getAssignmentType()
                    == AssignmentType.ADMIN) {

                if (assignment.getAdminId() == null) {
                    continue;
                }

                User admin =
                        userRepository.findById(
                                assignment.getAdminId())
                                .orElse(null);

                if (admin == null) {
                    continue;
                }

                assignedAdmins.add(
                        AssignedAdminSummaryDto
                                .builder()
                                .adminId(
                                        admin.getId())
                                .adminName(
                                        buildUserName(admin))
                                .adminEmail(
                                        admin.getEmail())
                                .status(
                                        Boolean.TRUE.equals(
                                                assignment.getActive())
                                                ? "ACTIVE"
                                                : "INACTIVE")
                                .build());
            }

         // =====================================================
         // DEVICE ASSIGNMENT
         // =====================================================

         if (assignment.getAssignmentType()
                 == AssignmentType.DEVICE) {

             if (assignment.getDeviceId() == null) {
                 continue;
             }

             Device device = null;

             List<Device> devices =
                     deviceRepository.findByDeviceId(
                             assignment.getDeviceId());

             if (devices != null &&
                     !devices.isEmpty()) {

                 device = devices.get(0);
             }

             AssignedDeviceSummaryDto.AssignedDeviceSummaryDtoBuilder builder =
                     AssignedDeviceSummaryDto.builder()
                             .deviceId(
                                     assignment.getDeviceId())
                             .status(
                                     device != null &&
                                     device.getStatus() != null
                                             ? device.getStatus().name()
                                             : Boolean.TRUE.equals(
                                                     assignment.getActive())
                                                     ? "ACTIVE"
                                                     : "INACTIVE");

             if (device != null) {

                 builder.deviceName(
                         device.getDeviceName());

                 builder.deviceType(
                         device.getTechnologyType() != null
                                 ? device.getTechnologyType().name()
                                 : null);
             }

             assignedDevices.add(
                     builder.build());
         }
        }

        return AlertRuleAssignmentResponseDto
                .builder()
                .alertId(
                        alert.getId())
                .alertName(
                        alert.getName())
                .alertCode(
                        null)
                .source(
                        alert.getSource())
                .severity(
                        alert.getSeverity())
                .assignedOn(
                        assignedOn)
                .status(
                        assignments.isEmpty()
                                ? "UNASSIGNED"
                                : "ACTIVE")
                .assignedAdmins(
                        assignedAdmins)
                .assignedDevices(
                        assignedDevices)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AlertRuleAssignmentPageResponseDto getAlertRuleAssignments(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        List<Alert> alerts =
                alertRepository.findAll();

        List<AlertRuleAssignmentResponseDto> results =
                new ArrayList<>();

        for (Alert alert : alerts) {

            AlertRuleAssignmentResponseDto response =
                    getAlertRuleAssignment(
                            alert.getId());

            results.add(response);
        }

        // =====================================================
        // SEARCH
        // =====================================================

        if (search != null &&
                !search.isBlank()) {

            String keyword =
                    search.trim()
                            .toLowerCase();

            results =
                    results.stream()
                            .filter(item ->

                                    String.valueOf(
                                            item.getAlertId())
                                            .contains(keyword)

                                    ||

                                    (item.getAlertName() != null
                                            && item.getAlertName()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getAlertCode() != null
                                            && item.getAlertCode()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getSource() != null
                                            && item.getSource()
                                            .name()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getSeverity() != null
                                            && item.getSeverity()
                                            .name()
                                            .toLowerCase()
                                            .contains(keyword))
                            )
                            .collect(Collectors.toList());
        }

        // =====================================================
        // SORTING
        // =====================================================

        Comparator<AlertRuleAssignmentResponseDto>
                comparator =
                        Comparator.comparing(
                                AlertRuleAssignmentResponseDto
                                        ::getAssignedOn,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()));

        if ("alertName".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AlertRuleAssignmentResponseDto
                                    ::getAlertName,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("source".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            item -> item.getSource() != null
                                    ? item.getSource().name()
                                    : null,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("severity".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            item -> item.getSeverity() != null
                                    ? item.getSeverity().name()
                                    : null,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("assignedOn".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AlertRuleAssignmentResponseDto
                                    ::getAssignedOn,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()));
        }

        if ("status".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            AlertRuleAssignmentResponseDto
                                    ::getStatus,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("ASC".equalsIgnoreCase(sortDirection)) {

            results.sort(comparator);

        } else {

            results.sort(
                    comparator.reversed());
        }

        // =====================================================
        // PAGINATION
        // =====================================================

        long totalElements =
                results.size();

        int totalPages =
                (int) Math.ceil(
                        (double) totalElements
                                / size);

        int fromIndex =
                page * size;

        List<AlertRuleAssignmentResponseDto>
                pagedResults;

        if (fromIndex >= totalElements) {

            pagedResults =
                    new ArrayList<>();

        } else {

            int toIndex =
                    Math.min(
                            fromIndex + size,
                            results.size());

            pagedResults =
                    results.subList(
                            fromIndex,
                            toIndex);
        }

        return AlertRuleAssignmentPageResponseDto
                .builder()
                .content(pagedResults)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .number(page)
                .size(size)
                .first(page == 0)
                .last(
                        totalPages == 0 ||
                        page >= totalPages - 1)
                .empty(pagedResults.isEmpty())
                .build();
    }
    @Override
    public List<AlertAssignmentResponseDto> assignAlertsToDevices(
            AssignDeviceAlertsRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Assignment request cannot be null");
        }

        if (request.getDeviceId() == null) {
            throw new IllegalArgumentException(
                    "Device ID is required");
        }

        if (request.getAlertIds() == null ||
                request.getAlertIds().isEmpty()) {
            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        Device device = deviceRepository.findById(
                request.getDeviceId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Device not found with ID: "
                                + request.getDeviceId()));

        String deviceId = device.getDeviceId();

        List<AlertAssignmentResponseDto> results =
                new ArrayList<>();

        for (Long alertId : request.getAlertIds()) {

            Alert alert = alertRepository.findById(alertId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Alert not found with ID: "
                                            + alertId));

            boolean alreadyAssigned =
                    alertAssignmentRepository
                            .findByAlertIdAndDeviceIdAndActiveTrue(
                                    alertId,
                                    deviceId)
                            .isPresent();

            if (alreadyAssigned) {
                continue;
            }

            AlertAssignment assignment =
                    AlertAssignment.builder()
                            .alertId(alert.getId())
                            .assignmentType(
                                    AssignmentType.DEVICE)
                            .adminId(null)
                            .deviceId(deviceId)
                            .assignedBy(getCurrentUsername())
                            .reason(null)
                            .active(true)
                            .build();

            AlertAssignment saved =
                    alertAssignmentRepository.save(
                            assignment);

            results.add(mapToResponse(saved));
        }

        return results;
    }
    @Override
    @Transactional
    public List<AlertAssignmentResponseDto>
    bulkAssignAlertsToDevices(
            BulkAssignDeviceAlertsRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null");
        }

        if (request.getAlertIds() == null ||
                request.getAlertIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        if (request.getDeviceIds() == null ||
                request.getDeviceIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Device IDs cannot be empty");
        }

        List<AlertAssignmentResponseDto> results =
                new ArrayList<>();

        for (Long alertId :
                request.getAlertIds()) {

            if (alertId == null) {

                throw new IllegalArgumentException(
                        "Alert ID cannot be null");
            }

            Alert alert =
                    alertRepository.findById(
                            alertId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Alert not found with ID: "
                                                    + alertId));

            for (String deviceId :
                    request.getDeviceIds()) {

                if (deviceId == null ||
                        deviceId.isBlank()) {

                    throw new IllegalArgumentException(
                            "Device ID cannot be empty");
                }

                List<Device> devices =
                        deviceRepository.findByDeviceId(
                                deviceId);

                if (devices == null ||
                        devices.isEmpty()) {

                    throw new RuntimeException(
                            "Device not found with ID: "
                                    + deviceId);
                }

                Optional<AlertAssignment> existing =
                        alertAssignmentRepository
                                .findByAlertIdAndDeviceIdAndActiveTrue(
                                        alert.getId(),
                                        deviceId);

                if (existing.isPresent()) {
                    continue;
                }

                AlertAssignment assignment =
                        AlertAssignment.builder()
                                .alertId(
                                        alert.getId())
                                .assignmentType(
                                        AssignmentType.DEVICE)
                                .deviceId(
                                        deviceId)
                                .assignedBy(
                                        "SYSTEM")
                                .reason(
                                        request.getReason())
                                .active(true)
                                .assignedAt(
                                        LocalDateTime.now())
                                .updatedAt(
                                        LocalDateTime.now())
                                .build();

                AlertAssignment saved =
                        alertAssignmentRepository.save(
                                assignment);

                results.add(
                        mapToResponse(saved));
            }
        }

        return results;
    }
    @Override
    public String removeAlertFromDevice(
            String deviceId,
            Long alertId) {

        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Device ID cannot be empty");
        }

        if (alertId == null) {
            throw new IllegalArgumentException(
                    "Alert ID cannot be null");
        }

        AlertAssignment assignment =
                alertAssignmentRepository
                        .findByAlertIdAndDeviceIdAndActiveTrue(
                                alertId,
                                deviceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active device alert assignment not found"));

        assignment.setActive(false);

        alertAssignmentRepository.save(assignment);

        return "Alert removed from device successfully";
    }
    @Override
    @Transactional(readOnly = true)
    public AlertAssignmentResponseDto getAssignmentById(
            Long assignmentId) {

        AlertAssignment assignment =
                alertAssignmentRepository
                        .findById(assignmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert assignment not found"));

        return mapToResponse(assignment);
    }
    @Override
    @Transactional(readOnly = true)
    public List<AlertAssignmentResponseDto> getAssignmentsByUser(
            Long userId) {

        List<Device> devices =
                deviceRepository.findByAssignedUserId(userId);

        if (devices == null || devices.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> deviceIds =
                devices.stream()
                        .map(Device::getDeviceId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        if (deviceIds.isEmpty()) {
            return new ArrayList<>();
        }

        return alertAssignmentRepository
                .findByActiveTrue()
                .stream()
                .filter(assignment ->
                        assignment.getDeviceId() != null
                        && deviceIds.contains(
                                assignment.getDeviceId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public List<AlertAssignmentResponseDto> updateDeviceAlerts(
            AssignDeviceAlertsRequestDto request) {

        if (request == null || request.getDeviceId() == null) {
            throw new IllegalArgumentException(
                    "Device ID cannot be null");
        }

        if (request.getAlertIds() == null) {
            throw new IllegalArgumentException(
                    "Alert IDs cannot be null");
        }

        Device device =
                deviceRepository.findById(
                        request.getDeviceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Device not found"));

        String deviceId = device.getDeviceId();

        // Get currently active device assignments
        List<AlertAssignment> existingAssignments =
                alertAssignmentRepository
                        .findByDeviceIdAndActiveTrue(deviceId);

        Set<Long> requestedAlertIds =
                new HashSet<>(request.getAlertIds());

        // Disable assignments that are no longer selected
        for (AlertAssignment assignment :
                existingAssignments) {

            if (!requestedAlertIds.contains(
                    assignment.getAlertId())) {

                assignment.setActive(false);
            }
        }

        // Create missing assignments
        for (Long alertId :
                requestedAlertIds) {

            boolean alreadyAssigned =
                    existingAssignments.stream()
                            .anyMatch(assignment ->
                                    alertId.equals(
                                            assignment.getAlertId()));

            if (!alreadyAssigned) {

                Alert alert =
                        alertRepository.findById(alertId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Alert not found: "
                                                + alertId));

                AlertAssignment assignment =
                        AlertAssignment.builder()
                                .alertId(alert.getId())
                                .assignmentType(
                                        AlertAssignment.AssignmentType.DEVICE)
                                .deviceId(deviceId)
                                .assignedAt(LocalDateTime.now())
                                .active(true)
                                .reason("Device alert assignment updated")
                                .build();

                alertAssignmentRepository.save(
                        assignment);
            }
        }

        // Save disabled assignments
        alertAssignmentRepository.saveAll(
                existingAssignments);

        // Return current active assignments
        return alertAssignmentRepository
                .findByDeviceIdAndActiveTrue(deviceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    // =========================================================
    // HELPERS
    // =========================================================

    private AlertAssignment getAssignment(
            Long assignmentId) {

        if (assignmentId == null) {

            throw new IllegalArgumentException(
                    "Assignment ID cannot be null");
        }

        return alertAssignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assignment not found with ID: "
                                        + assignmentId));
    }

    private void validateAdminAssignmentRequest(
            AssignAdminAlertsRequestDto request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Assignment request cannot be null");
        }

        if (request.getAlertId() == null) {

            throw new IllegalArgumentException(
                    "Alert ID is required");
        }

        if (request.getAdminIds() == null ||
                request.getAdminIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Admin IDs cannot be empty");
        }
    }

    private void validateBulkAdminAssignmentRequest(
            BulkAssignAlertsRequestDto request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Bulk assignment request cannot be null");
        }

        if (request.getAlertIds() == null ||
                request.getAlertIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Alert IDs cannot be empty");
        }

        if (request.getAdminIds() == null ||
                request.getAdminIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Admin IDs cannot be empty");
        }
    }

    private AssignmentType parseAssignmentType(
            String value) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    "Assignment type is required");
        }

        try {

            return AssignmentType.valueOf(
                    value.trim()
                            .toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid assignment type. "
                            + "Allowed values: ADMIN, DEVICE");
        }
    }

    private void validateAssignmentRequest(
            AssignmentType assignmentType,
            AssignAlertRequestDto request) {

        if (assignmentType ==
                AssignmentType.ADMIN) {

            if (request.getAdminId() == null) {

                throw new IllegalArgumentException(
                        "Admin ID is required");
            }

            return;
        }

        if (request.getDeviceId() == null ||
                request.getDeviceId().isBlank()) {

            throw new IllegalArgumentException(
                    "Device ID is required");
        }
    }

    
    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return "SYSTEM";
        }

        return authentication.getName();
    }
    private void validateAdminIds(
            List<Long> adminIds) {

        if (adminIds == null ||
                adminIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Admin IDs cannot be empty");
        }

        for (Long adminId : adminIds) {

            if (adminId == null) {

                throw new IllegalArgumentException(
                        "Admin ID cannot be null");
            }

            User user =
                    userRepository.findById(adminId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Admin not found with ID: "
                                                    + adminId));

            if (user.getRole() != RoleType.ADMIN &&
                    user.getRole() != RoleType.SUPER_ADMIN) {

                throw new IllegalArgumentException(
                        "User with ID "
                                + adminId
                                + " is not an admin");
            }

            if (!Boolean.TRUE.equals(
                    user.getActive())) {

                throw new IllegalArgumentException(
                        "Admin with ID "
                                + adminId
                                + " is inactive");
            }
        }
    }
    private String buildUserName(User user) {

        String firstName =
                user.getFirstName();

        String lastName =
                user.getLastName();

        if (firstName != null &&
                !firstName.isBlank() &&
                lastName != null &&
                !lastName.isBlank()) {

            return firstName + " " + lastName;
        }

        if (firstName != null &&
                !firstName.isBlank()) {

            return firstName;
        }

        if (lastName != null &&
                !lastName.isBlank()) {

            return lastName;
        }

        return user.getUserName();
    }
    @Override
    public DeviceAlertAssignmentPageResponseDto getDeviceAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection) {

        List<AlertAssignment> assignments =
                alertAssignmentRepository.findByActiveTrue();

        Map<String, List<AlertAssignment>> groupedByDevice =
                assignments.stream()
                        .filter(a ->
                                a.getAssignmentType()
                                        == AssignmentType.DEVICE)
                        .filter(a ->
                                a.getDeviceId() != null)
                        .collect(Collectors.groupingBy(
                                AlertAssignment::getDeviceId));

        List<DeviceAlertAssignmentResponseDto> results =
                new ArrayList<>();

        for (Map.Entry<String, List<AlertAssignment>> entry :
                groupedByDevice.entrySet()) {

            String deviceId = entry.getKey();

            List<Device> devices =
                    deviceRepository.findByDeviceId(deviceId);

            if (devices == null ||
                    devices.isEmpty()) {
                continue;
            }

            Device device = devices.get(0);

            User admin = device.getAssignedAdmin();
            User user = device.getAssignedUser();

            List<AssignedAlertSummaryDto> alerts =
                    new ArrayList<>();

            for (AlertAssignment assignment :
                    entry.getValue()) {

                Alert alert =
                        alertRepository.findById(
                                assignment.getAlertId())
                                .orElse(null);

                if (alert == null) {
                    continue;
                }

                alerts.add(
                        AssignedAlertSummaryDto.builder()
                                .alertId(alert.getId())
                                .alertName(alert.getName())
                                .severity(alert.getSeverity())
                                .build());
            }

            results.add(
                    DeviceAlertAssignmentResponseDto.builder()
                            .deviceId(device.getId())
                            .deviceName(device.getDeviceName())
                            .deviceType(
                                    device.getTechnologyType() != null
                                            ? device.getTechnologyType().name()
                                            : null)
                            .imei(device.getImei())
                            .adminName(
                                    admin != null
                                            ? admin.getFirstName()
                                                    + " "
                                                    + admin.getLastName()
                                            : null)
                            .adminEmail(
                                    admin != null
                                            ? admin.getEmail()
                                            : null)
                            .userName(
                                    user != null
                                            ? user.getUserName()
                                            : null)
                            .userEmail(
                                    user != null
                                            ? user.getEmail()
                                            : null)
                            .assignedOn(
                                    entry.getValue()
                                            .stream()
                                            .map(AlertAssignment::getAssignedAt)
                                            .filter(Objects::nonNull)
                                            .min(LocalDateTime::compareTo)
                                            .orElse(null))
                            .status(
                                    Boolean.TRUE.equals(device.getActive())
                                            ? "ACTIVE"
                                            : "INACTIVE")
                            .alerts(alerts)
                            .build());
        }

        // =====================================================
        // SEARCH
        // =====================================================

        if (search != null &&
                !search.isBlank()) {

            String keyword =
                    search.trim().toLowerCase();

            results =
                    results.stream()
                            .filter(item ->

                                    (item.getDeviceName() != null
                                            && item.getDeviceName()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getDeviceType() != null
                                            && item.getDeviceType()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getImei() != null
                                            && item.getImei()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getAdminName() != null
                                            && item.getAdminName()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getAdminEmail() != null
                                            && item.getAdminEmail()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getUserName() != null
                                            && item.getUserName()
                                            .toLowerCase()
                                            .contains(keyword))

                                    ||

                                    (item.getUserEmail() != null
                                            && item.getUserEmail()
                                            .toLowerCase()
                                            .contains(keyword))
                            )
                            .collect(Collectors.toList());
        }

        // =====================================================
        // SORT
        // =====================================================

        Comparator<DeviceAlertAssignmentResponseDto> comparator =
                Comparator.comparing(
                        DeviceAlertAssignmentResponseDto::getAssignedOn,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()));

        if ("deviceName".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            DeviceAlertAssignmentResponseDto
                                    ::getDeviceName,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("deviceType".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            DeviceAlertAssignmentResponseDto
                                    ::getDeviceType,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("status".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            DeviceAlertAssignmentResponseDto
                                    ::getStatus,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("adminName".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            DeviceAlertAssignmentResponseDto
                                    ::getAdminName,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER));
        }

        if ("assignedOn".equalsIgnoreCase(sortBy)) {

            comparator =
                    Comparator.comparing(
                            DeviceAlertAssignmentResponseDto
                                    ::getAssignedOn,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()));
        }

        if ("ASC".equalsIgnoreCase(sortDirection)) {

            results.sort(comparator);

        } else {

            results.sort(comparator.reversed());
        }

        // =====================================================
        // PAGINATION
        // =====================================================

        long totalElements = results.size();

        int totalPages =
                size > 0
                        ? (int) Math.ceil(
                                (double) totalElements / size)
                        : 0;

        int fromIndex = page * size;

        List<DeviceAlertAssignmentResponseDto> content;

        if (fromIndex >= results.size()) {

            content = new ArrayList<>();

        } else {

            int toIndex =
                    Math.min(
                            fromIndex + size,
                            results.size());

            content =
                    results.subList(
                            fromIndex,
                            toIndex);
        }

        return DeviceAlertAssignmentPageResponseDto
                .builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .number(page)
                .size(size)
                .first(page == 0)
                .last(
                        totalPages == 0 ||
                        page >= totalPages - 1)
                .empty(content.isEmpty())
                .build();
    }
    private AlertAssignmentResponseDto mapToResponse(
            AlertAssignment assignment) {

        return AlertAssignmentResponseDto
                .builder()

                .id(
                        assignment.getId())

                .alertId(
                        assignment.getAlertId())

                .assignmentType(
                        assignment
                                .getAssignmentType()
                                .name())

                .adminId(
                        assignment.getAdminId())

                .deviceId(
                        assignment.getDeviceId())

                .assignedBy(
                        assignment.getAssignedBy())

                .reason(
                        assignment.getReason())

                .assignedAt(
                        assignment.getAssignedAt())

                .active(
                        assignment.getActive())

                .updatedAt(
                        assignment.getUpdatedAt())

                .build();
    }
}