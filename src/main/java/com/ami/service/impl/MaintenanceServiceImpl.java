package com.ami.service.impl;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.time.Duration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.AssignmentFailureRequestDto;
import com.ami.dto.requests.CancelMaintenanceRequestDto;
import com.ami.dto.requests.CompleteMaintenanceRequestDto;
import com.ami.dto.requests.CreateMaintenanceRequestDto;
import com.ami.dto.responses.MaintenanceAttachmentsResponseDto;
import com.ami.dto.responses.MaintenanceChecklistResponseDto;
import com.ami.dto.responses.MaintenanceDashboardResponseDto;
import com.ami.dto.responses.MaintenanceHistoryResponseDto;
import com.ami.dto.responses.MaintenancePhotoResponseDto;
import com.ami.dto.responses.MaintenanceRemarksResponseDto;
import com.ami.dto.responses.MaintenanceResponseDto;
import com.ami.dto.responses.MaintenanceTimelineResponseDto;
import com.ami.entity.Maintenance;
import com.ami.entity.User;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;
import com.ami.repository.MaintenanceRepository;
import com.ami.service.EngineerService;
import com.ami.service.MaintenanceService;
import com.ami.service.NotificationManagementService;
import com.ami.service.NotificationManagementService;
import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.dto.requests.ReassignEngineerRequestDto;
import com.ami.dto.requests.RescheduleMaintenanceRequestDto;
import com.ami.dto.requests.StartMaintenanceRequestDto;
import com.ami.dto.requests.UpdateMaintenanceAttachmentsRequestDto;
import com.ami.dto.requests.UpdateMaintenanceChecklistRequestDto;
import com.ami.dto.requests.UpdateMaintenancePhotoRequestDto;
import com.ami.dto.requests.UpdateMaintenanceRemarksRequestDto;
import com.ami.enums.NotificationType;
import com.ami.enums.RoleType;
import lombok.RequiredArgsConstructor;
import com.ami.dto.responses.MaintenanceHistoryResponseDto;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl
        implements MaintenanceService {
	
	private final MaintenanceRepository maintenanceRepository;

	private final NotificationManagementService
	        notificationManagementService;

	private final EngineerService engineerService;
	
	@Override
	public MaintenanceResponseDto createMaintenance(
	        CreateMaintenanceRequestDto request) {

		Maintenance maintenance =
		        Maintenance.builder()

		                .deviceId(
		                        request.getDeviceId())

		                .maintenanceType(
		                        request.getMaintenanceType())

		                .source(
		                        request.getSource())

		                .priority(
		                        request.getPriority())

		                .status(
		                        MaintenanceStatus.CREATED)

		                .title(
		                        request.getTitle() != null
		                                ? request.getTitle()
		                                : "Maintenance - "
		                                        + request.getDeviceId())

		                .description(
		                        request.getDescription())

		                .assignedEngineer(
		                        request.getAssignedEngineer())

		                .preferredDate(
		                        request.getPreferredDate())

		                .scheduledAt(
		                        request.getScheduledAt())

		                .maintenanceCost(
		                        request.getMaintenanceCost())

		                .totalCost(
		                        request.getTotalCost() != null
		                                ? request.getTotalCost()
		                                : request.getMaintenanceCost())

		                .replacementParts(
		                        request.getReplacementParts())

		                .remarks(
		                        request.getRemarks())

		                .estimatedDuration(
		                        request.getEstimatedDuration())

		                .rescheduleCount(0)

		                .autoReassignmentCount(0)

		                .assignmentFailureCount(0)

		                .manualAssignmentRequired(false)

		                .build();
	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    notificationManagementService.createNotification(

	            CreateNotificationRequestDto.builder()

	                    .type(NotificationType.MAINTENANCE)

	                    .title("Maintenance Created")

	                    .message(
	                            "Maintenance created for device "
	                                    + maintenance.getDeviceId())

	                    .recipient(
	                            maintenance.getAssignedEngineer())

	                    .build());

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public MaintenanceResponseDto getMaintenanceById(
	        Long id) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(id)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public Page<MaintenanceResponseDto> getAllMaintenance(

	        int page,

	        int size,

	        String search,

	        MaintenanceType maintenanceType,

	        MaintenanceStatus status,

	        String sortBy,

	        String direction) {

	    Sort sort = direction.equalsIgnoreCase("DESC")
	            ? Sort.by(sortBy).descending()
	            : Sort.by(sortBy).ascending();

	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    sort);

	    Specification<Maintenance> specification =
	            (root, query, cb) -> cb.conjunction();

	    if (search != null && !search.isBlank()) {

	        specification = specification.and((root, query, cb) ->

	                cb.or(

	                        cb.like(

	                                cb.lower(root.get("deviceId")),

	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(

	                                cb.lower(root.get("title")),

	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(

	                                cb.lower(root.get("assignedEngineer")),

	                                "%" + search.toLowerCase() + "%")));
	    }

	    if (maintenanceType != null) {

	        specification = specification.and((root, query, cb) ->

	                cb.equal(

	                        root.get("maintenanceType"),

	                        maintenanceType));
	    }

	    if (status != null) {

	        specification = specification.and((root, query, cb) ->

	                cb.equal(

	                        root.get("status"),

	                        status));
	    }

	    return maintenanceRepository

	            .findAll(

	                    specification,

	                    pageable)

	            .map(this::mapToResponse);
	}
	@Override
	public MaintenanceResponseDto updateMaintenance(

	        Long id,

	        CreateMaintenanceRequestDto request) {

	    Maintenance maintenance =

	            maintenanceRepository

	                    .findById(id)

	                    .orElseThrow(() ->

	                            new RuntimeException(
	                                    "Maintenance not found"));

	    maintenance.setDeviceId(
	            request.getDeviceId());

	    maintenance.setMaintenanceType(
	            request.getMaintenanceType());

	    maintenance.setSource(
	            request.getSource());

	    maintenance.setPriority(
	            request.getPriority());

	    if (request.getTitle() != null) {

	        maintenance.setTitle(
	                request.getTitle());
	    }

	    maintenance.setDescription(
	            request.getDescription());

	    if (request.getAssignedEngineer() != null) {

	        maintenance.setAssignedEngineer(
	                request.getAssignedEngineer());
	    }

	    if (request.getPreferredDate() != null) {

	        maintenance.setPreferredDate(
	                request.getPreferredDate());
	    }

	    if (request.getScheduledAt() != null) {

	        maintenance.setScheduledAt(
	                request.getScheduledAt());
	    }

	    if (request.getMaintenanceCost() != null) {

	        maintenance.setMaintenanceCost(
	                request.getMaintenanceCost());

	        maintenance.setTotalCost(
	                request.getMaintenanceCost());
	    }

	    if (request.getTotalCost() != null) {

	        maintenance.setTotalCost(
	                request.getTotalCost());
	    }

	    maintenance.setReplacementParts(
	            request.getReplacementParts());

	    maintenance.setRemarks(
	            request.getRemarks());

	    if (request.getEstimatedDuration() != null) {

	        maintenance.setEstimatedDuration(
	                request.getEstimatedDuration());
	    }

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    notificationManagementService.createNotification(

	            CreateNotificationRequestDto.builder()

	                    .type(NotificationType.MAINTENANCE)

	                    .title("Maintenance Updated")

	                    .message(
	                            "Maintenance updated for device "
	                                    + maintenance.getDeviceId())

	                    .recipient(
	                            maintenance.getAssignedEngineer())

	                    .build());

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public String deleteMaintenance(
	        Long id) {

	    Maintenance maintenance =

	            maintenanceRepository

	                    .findById(id)

	                    .orElseThrow(() ->

	                            new RuntimeException(
	                                    "Maintenance not found"));

	    notificationManagementService.createNotification(

	            CreateNotificationRequestDto.builder()

	                    .type(NotificationType.MAINTENANCE)

	                    .title("Maintenance Deleted")

	                    .message(
	                            "Maintenance deleted for device "
	                                    + maintenance.getDeviceId())

	                    .recipient("ADMIN")

	                    .build());

	    maintenanceRepository.delete(
	            maintenance);

	    return "Maintenance deleted successfully";
	}
	@Override
	public MaintenanceDashboardResponseDto getDashboard() {

	    List<Maintenance> list =
	            maintenanceRepository.findAll();

	    return MaintenanceDashboardResponseDto.builder()

	            .totalMaintenance(
	                    (long) list.size())

	            .scheduledMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getStatus() ==
	                                    MaintenanceStatus.CREATED)
	                            .count())

	            .inProgressMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getStatus() ==
	                                    MaintenanceStatus.IN_PROGRESS)
	                            .count())

	            .completedMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getStatus() ==
	                                    MaintenanceStatus.COMPLETED)
	                            .count())

	            .cancelledMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getStatus() ==
	                                    MaintenanceStatus.CANCELLED)
	                            .count())

	            .preventiveMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getMaintenanceType() ==
	                                    MaintenanceType.PREVENTIVE)
	                            .count())

	            .correctiveMaintenance(
	                    list.stream()
	                            .filter(m ->
	                                    m.getMaintenanceType() ==
	                                    MaintenanceType.CORRECTIVE)
	                            .count())

	            .totalMaintenanceCost(
	                    list.stream()
	                            .mapToDouble(m ->
	                                    m.getMaintenanceCost() == null
	                                            ? 0
	                                            : m.getMaintenanceCost())
	                            .sum())

	            .build();
	}
	@Override
	public List<MaintenanceTimelineResponseDto> getTimeline() {

	    return maintenanceRepository
	            .findAll(
	                    org.springframework.data.domain.Sort
	                            .by(
	                                    org.springframework.data.domain.Sort.Direction.DESC,
	                                    "createdAt"))
	            .stream()
	            .map(this::mapToTimeline)
	            .toList();
	}
	@Override
	public List<MaintenanceTimelineResponseDto> getTimeline(
	        Long maintenanceId) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return List.of(
	            mapToTimeline(maintenance));
	}
	private MaintenanceTimelineResponseDto mapToTimeline(
	        Maintenance maintenance) {

	    return MaintenanceTimelineResponseDto
	            .builder()

	            .id(
	                    maintenance.getId())

	            .deviceId(
	                    maintenance.getDeviceId())

	            .title(
	                    maintenance.getTitle())

	            .assignedEngineer(
	                    maintenance.getAssignedEngineer())

	            .status(
	                    maintenance.getStatus() != null
	                            ? maintenance.getStatus().name()
	                            : null)

	            .createdAt(
	                    maintenance.getCreatedAt())

	            .assignedAt(
	                    maintenance.getAssignedAt())

	            .scheduledAt(
	                    maintenance.getScheduledAt())

	            .startedAt(
	                    maintenance.getStartedAt())

	            .completedAt(
	                    maintenance.getCompletedAt())

	            .cancelledAt(
	                    maintenance.getCancelledAt())

	            .rescheduledAt(
	                    maintenance.getRescheduledAt())

	            .rescheduleCount(
	                    maintenance.getRescheduleCount())

	            .assignmentFailureCount(
	                    maintenance.getAssignmentFailureCount())

	            .manualAssignmentRequired(
	                    maintenance.getManualAssignmentRequired())

	            .build();
	}
	@Override
	public List<MaintenanceHistoryResponseDto> getHistory(
	        String deviceId) {

	    List<Maintenance> maintenanceList =
	            maintenanceRepository
	                    .findByDeviceId(deviceId);

	    return maintenanceList.stream()
	            .sorted(
	                    Comparator.comparing(
	                            Maintenance::getCreatedAt,
	                            Comparator.nullsLast(
	                                    Comparator.reverseOrder())))
	            .map(this::mapToHistory)
	            .toList();
	}
	
	
	private MaintenanceHistoryResponseDto mapToHistory(
	        Maintenance maintenance) {

	    return MaintenanceHistoryResponseDto
	            .builder()

	            .maintenanceId(
	                    maintenance.getId())

	            .deviceId(
	                    maintenance.getDeviceId())

	            .title(
	                    maintenance.getTitle())

	            .maintenanceType(
	                    maintenance.getMaintenanceType() != null
	                            ? maintenance
	                                    .getMaintenanceType()
	                                    .name()
	                            : null)

	            .source(
	                    maintenance.getSource() != null
	                            ? maintenance
	                                    .getSource()
	                                    .name()
	                            : null)

	            .priority(
	                    maintenance.getPriority() != null
	                            ? maintenance
	                                    .getPriority()
	                                    .name()
	                            : null)

	            .status(
	                    maintenance.getStatus() != null
	                            ? maintenance
	                                    .getStatus()
	                                    .name()
	                            : null)

	            .assignedEngineer(
	                    maintenance.getAssignedEngineer())

	            .assignedEngineerId(
	                    maintenance.getAssignedEngineerId())

	            .createdAt(
	                    maintenance.getCreatedAt())

	            .assignedAt(
	                    maintenance.getAssignedAt())

	            .preferredDate(
	                    maintenance.getPreferredDate())

	            .scheduledAt(
	                    maintenance.getScheduledAt())

	            .rescheduledAt(
	                    maintenance.getRescheduledAt())

	            .startedAt(
	                    maintenance.getStartedAt())

	            .completedAt(
	                    maintenance.getCompletedAt())

	            .cancelledAt(
	                    maintenance.getCancelledAt())

	            .rescheduleCount(
	                    maintenance.getRescheduleCount())

	            .assignmentFailureCount(
	                    maintenance.getAssignmentFailureCount())

	            .manualAssignmentRequired(
	                    maintenance
	                            .getManualAssignmentRequired())

	            .estimatedDuration(
	                    maintenance.getEstimatedDuration())

	            .actualDuration(
	                    maintenance.getActualDuration())

	            .maintenanceCost(
	                    maintenance.getMaintenanceCost())

	            .totalCost(
	                    maintenance.getTotalCost())

	            .replacementParts(
	                    maintenance.getReplacementParts())

	            .remarks(
	                    maintenance.getRemarks())

	            .build();
	}
	@Override
	public List<MaintenanceResponseDto>
	getUpcomingMaintenance() {

	    return maintenanceRepository
	            .findByStatus(
	            		MaintenanceStatus.CREATED)
	            .stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	@Override
	public List<MaintenanceResponseDto>
	getCompletedMaintenance() {

	    return maintenanceRepository
	            .findByStatus(
	                    MaintenanceStatus.COMPLETED)
	            .stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	@Override
	public MaintenanceResponseDto assignEngineer(
	        Long maintenanceId,
	        AssignEngineerRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE CURRENT STATUS
	    // ============================================

	    if (maintenance.getStatus() != MaintenanceStatus.CREATED) {

	        throw new RuntimeException(
	                "Engineer can only be assigned to maintenance "
	                        + "with CREATED status");
	    }

	    // ============================================
	    // 3. FIND ENGINEER
	    // ============================================

	    User engineer =
	            engineerService.getEngineerById(
	                    request.getEngineerId());

	    // ============================================
	    // 4. VALIDATE ENGINEER ROLE
	    // ============================================

	    if (engineer.getRole() != RoleType.SERVICE_ENGINEER) {

	        throw new RuntimeException(
	                "Selected user is not a service engineer");
	    }

	    // ============================================
	    // 5. VALIDATE ENGINEER ACCOUNT
	    // ============================================

	    if (Boolean.FALSE.equals(engineer.getActive())) {

	        throw new RuntimeException(
	                "Selected engineer is inactive");
	    }

	    // ============================================
	    // 6. VALIDATE ENGINEER AVAILABILITY
	    // ============================================

	    if (engineer.getAvailabilityStatus()
	            != EngineerAvailabilityStatus.AVAILABLE) {

	        throw new RuntimeException(
	                "Selected engineer is not available");
	    }

	    // ============================================
	    // 7. ASSIGN ENGINEER
	    // ============================================

	    maintenance.setAssignedEngineerId(
	            engineer.getId());

	    String engineerName =
	            ((engineer.getFirstName() != null
	                    ? engineer.getFirstName()
	                    : "")
	            + " "
	            + (engineer.getLastName() != null
	                    ? engineer.getLastName()
	                    : ""))
	            .trim();

	    maintenance.setAssignedEngineer(
	            engineerName);

	    maintenance.setAssignedAt(
	            LocalDateTime.now());
	    
	    maintenance.setManualAssignmentRequired(
	            false);maintenance.setManualAssignmentRequired(
	                    false);

	    // ============================================
	    // 8. UPDATE STATUS
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.AUTO_ASSIGNED);

	    // ============================================
	    // 9. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 10. NOTIFICATION
	    // ============================================

	    notificationManagementService.createNotification(

	            CreateNotificationRequestDto.builder()

	                    .type(
	                            NotificationType.MAINTENANCE)

	                    .title(
	                            "Maintenance Assigned")

	                    .message(
	                            "Maintenance #"
	                                    + maintenance.getId()
	                                    + " has been assigned to "
	                                    + engineerName)

	                    .recipient(
	                            engineer.getEmail())

	                    .build());

	    // ============================================
	    // 11. RETURN UPDATED MAINTENANCE
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public MaintenanceResponseDto reassignEngineer(
	        Long maintenanceId,
	        ReassignEngineerRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE CURRENT ASSIGNMENT
	    // ============================================

	    if (maintenance.getAssignedEngineerId() == null) {

	        throw new RuntimeException(
	                "Maintenance has no engineer assigned. "
	                        + "Use assign engineer instead.");
	    }

	    // ============================================
	    // 3. FIND NEW ENGINEER
	    // ============================================

	    User newEngineer =
	            engineerService.getEngineerById(
	                    request.getEngineerId());

	    // ============================================
	    // 4. PREVENT SAME ENGINEER
	    // ============================================

	    if (maintenance.getAssignedEngineerId()
	            .equals(newEngineer.getId())) {

	        throw new RuntimeException(
	                "The selected engineer is already assigned "
	                        + "to this maintenance");
	    }

	    // ============================================
	    // 5. VALIDATE ENGINEER ROLE
	    // ============================================

	    if (newEngineer.getRole()
	            != RoleType.SERVICE_ENGINEER) {

	        throw new RuntimeException(
	                "Selected user is not a service engineer");
	    }

	    // ============================================
	    // 6. VALIDATE ENGINEER ACTIVE STATUS
	    // ============================================

	    if (Boolean.FALSE.equals(
	            newEngineer.getActive())) {

	        throw new RuntimeException(
	                "Selected engineer is inactive");
	    }

	    // ============================================
	    // 7. VALIDATE ENGINEER AVAILABILITY
	    // ============================================

	    if (newEngineer.getAvailabilityStatus()
	            != EngineerAvailabilityStatus.AVAILABLE) {

	        throw new RuntimeException(
	                "Selected engineer is not available");
	    }

	    // ============================================
	    // 8. STORE OLD ENGINEER
	    // ============================================

	    Long previousEngineerId =
	            maintenance.getAssignedEngineerId();

	    String previousEngineer =
	            maintenance.getAssignedEngineer();

	    // ============================================
	    // 9. ASSIGN NEW ENGINEER
	    // ============================================

	    maintenance.setAssignedEngineerId(
	            newEngineer.getId());

	    String newEngineerName =
	            ((newEngineer.getFirstName() != null
	                    ? newEngineer.getFirstName()
	                    : "")
	            + " "
	            + (newEngineer.getLastName() != null
	                    ? newEngineer.getLastName()
	                    : ""))
	            .trim();

	    maintenance.setAssignedEngineer(
	            newEngineerName);

	    maintenance.setAssignedAt(
	            LocalDateTime.now());

	    // ============================================
	    // 10. INCREMENT REASSIGNMENT COUNT
	    // ============================================

	    Integer currentCount =
	            maintenance.getAutoReassignmentCount();

	    if (currentCount == null) {
	        currentCount = 0;
	    }

	    maintenance.setAutoReassignmentCount(
	            currentCount + 1);
	    
	    maintenance.setManualAssignmentRequired(
	            false);

	    // ============================================
	    // 11. UPDATE STATUS
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.AUTO_ASSIGNED);

	    // ============================================
	    // 12. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 13. NOTIFY NEW ENGINEER
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Reassigned")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " has been reassigned "
	                                            + "to you. Reason: "
	                                            + request.getReason())

	                            .recipient(
	                                    newEngineer.getEmail())

	                            .build());

	    // ============================================
	    // 14. NOTIFY ADMIN
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Reassigned")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " reassigned from "
	                                            + previousEngineer
	                                            + " to "
	                                            + newEngineerName
	                                            + ". Reason: "
	                                            + request.getReason())

	                            .recipient("ADMIN")

	                            .build());

	    // ============================================
	    // 15. RETURN UPDATED MAINTENANCE
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public MaintenanceResponseDto recordAssignmentFailure(
	        Long maintenanceId,
	        AssignmentFailureRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE STATUS
	    // ============================================

	    if (maintenance.getStatus() == MaintenanceStatus.COMPLETED) {

	        throw new RuntimeException(
	                "Cannot record assignment failure for "
	                        + "completed maintenance");
	    }

	    if (maintenance.getStatus() == MaintenanceStatus.CANCELLED) {

	        throw new RuntimeException(
	                "Cannot record assignment failure for "
	                        + "cancelled maintenance");
	    }

	    // ============================================
	    // 3. VALIDATE REASON
	    // ============================================

	    if (request.getReason() == null
	            || request.getReason().isBlank()) {

	        throw new RuntimeException(
	                "Assignment failure reason is required");
	    }

	    // ============================================
	    // 4. INCREMENT FAILURE COUNT
	    // ============================================

	    Integer currentFailureCount =
	            maintenance.getAssignmentFailureCount();

	    if (currentFailureCount == null) {
	        currentFailureCount = 0;
	    }

	    maintenance.setAssignmentFailureCount(
	            currentFailureCount + 1);

	    // ============================================
	    // 5. STORE FAILURE DETAILS
	    // ============================================

	    maintenance.setLastAssignmentFailedAt(
	            LocalDateTime.now());

	    maintenance.setLastAssignmentFailureReason(
	            request.getReason());

	    // ============================================
	    // 6. REQUIRE MANUAL ASSIGNMENT
	    // ============================================

	    maintenance.setManualAssignmentRequired(
	            true);

	    // ============================================
	    // 7. KEEP MAINTENANCE AVAILABLE
	    //    FOR MANUAL ASSIGNMENT
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.CREATED);

	    // ============================================
	    // 8. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 9. NOTIFY ADMIN
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType.MAINTENANCE)

	                            .title(
	                                    "Maintenance Assignment Failed")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " could not be assigned. "
	                                            + "Reason: "
	                                            + request.getReason())

	                            .recipient("ADMIN")

	                            .build());

	    // ============================================
	    // 10. RETURN UPDATED MAINTENANCE
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	
	@Override
	public MaintenanceResponseDto startMaintenance(
	        Long maintenanceId,
	        StartMaintenanceRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE ENGINEER ASSIGNMENT
	    // ============================================

	    if (maintenance.getAssignedEngineerId() == null) {

	        throw new RuntimeException(
	                "Maintenance cannot be started "
	                        + "without an assigned engineer");
	    }

	    // ============================================
	    // 3. VALIDATE CURRENT STATUS
	    // ============================================

	    MaintenanceStatus currentStatus =
	            maintenance.getStatus();

	    if (currentStatus != MaintenanceStatus.AUTO_ASSIGNED
	            && currentStatus != MaintenanceStatus.ACCEPTED
	            && currentStatus != MaintenanceStatus.ON_THE_WAY) {

	        throw new RuntimeException(
	                "Maintenance cannot be started from status: "
	                        + currentStatus);
	    }

	    // ============================================
	    // 4. SET START TIME
	    // ============================================

	    LocalDateTime startTime =
	            LocalDateTime.now();

	    maintenance.setStartedAt(
	            startTime);

	    // ============================================
	    // 5. UPDATE STATUS
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.IN_PROGRESS);

	    // ============================================
	    // 6. SAVE REMARKS IF PROVIDED
	    // ============================================

	    if (request != null
	            && request.getRemarks() != null
	            && !request.getRemarks().isBlank()) {

	        maintenance.setRemarks(
	                request.getRemarks());
	    }

	    // ============================================
	    // 7. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 8. NOTIFY
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType.MAINTENANCE)

	                            .title(
	                                    "Maintenance Started")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " has started")

	                            .recipient(
	                                    maintenance
	                                            .getAssignedEngineer())

	                            .build());

	    // ============================================
	    // 9. RETURN
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public MaintenanceResponseDto completeMaintenance(
	        Long maintenanceId,
	        CompleteMaintenanceRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE ENGINEER ASSIGNMENT
	    // ============================================

	    if (maintenance.getAssignedEngineerId() == null) {

	        throw new RuntimeException(
	                "Maintenance cannot be completed "
	                        + "without an assigned engineer");
	    }

	    // ============================================
	    // 3. VALIDATE STARTED STATUS
	    // ============================================

	    if (maintenance.getStartedAt() == null) {

	        throw new RuntimeException(
	                "Maintenance cannot be completed "
	                        + "before it is started");
	    }

	    // ============================================
	    // 4. VALIDATE CURRENT STATUS
	    // ============================================

	    if (maintenance.getStatus()
	            != MaintenanceStatus.IN_PROGRESS) {

	        throw new RuntimeException(
	                "Only IN_PROGRESS maintenance "
	                        + "can be completed");
	    }

	    // ============================================
	    // 5. SET COMPLETION TIME
	    // ============================================

	    LocalDateTime completedAt =
	            LocalDateTime.now();

	    maintenance.setCompletedAt(
	            completedAt);

	    // ============================================
	    // 6. CALCULATE ACTUAL DURATION
	    // ============================================

	    long durationMinutes =
	            Duration.between(
	                    maintenance.getStartedAt(),
	                    completedAt)
	                    .toMinutes();

	    maintenance.setActualDuration(
	            (int) durationMinutes);

	    // ============================================
	    // 7. UPDATE STATUS
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.COMPLETED);

	    // ============================================
	    // 8. UPDATE REMARKS
	    // ============================================

	    if (request != null
	            && request.getRemarks() != null
	            && !request.getRemarks().isBlank()) {

	        maintenance.setRemarks(
	                request.getRemarks());
	    }

	    // ============================================
	    // 9. UPDATE COST
	    // ============================================

	    if (request != null
	            && request.getMaintenanceCost() != null) {

	        maintenance.setMaintenanceCost(
	                request.getMaintenanceCost());
	    }

	    if (request != null
	            && request.getTotalCost() != null) {

	        maintenance.setTotalCost(
	                request.getTotalCost());

	    } else if (request != null
	            && request.getMaintenanceCost() != null) {

	        maintenance.setTotalCost(
	                request.getMaintenanceCost());
	    }

	    // ============================================
	    // 10. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 11. NOTIFY ENGINEER
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Completed")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " has been completed")

	                            .recipient(
	                                    maintenance
	                                            .getAssignedEngineer())

	                            .build());

	    // ============================================
	    // 12. NOTIFY ADMIN
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Completed")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " has been completed "
	                                            + "for device "
	                                            + maintenance.getDeviceId())

	                            .recipient("ADMIN")

	                            .build());

	    // ============================================
	    // 13. RETURN
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	
	@Override
	public MaintenanceResponseDto cancelMaintenance(
	        Long maintenanceId,
	        CancelMaintenanceRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE REQUEST
	    // ============================================

	    if (request == null
	            || request.getReason() == null
	            || request.getReason().isBlank()) {

	        throw new RuntimeException(
	                "Cancellation reason is required");
	    }

	    // ============================================
	    // 3. PREVENT INVALID CANCELLATION
	    // ============================================

	    if (maintenance.getStatus()
	            == MaintenanceStatus.COMPLETED) {

	        throw new RuntimeException(
	                "Completed maintenance cannot be cancelled");
	    }

	    if (maintenance.getStatus()
	            == MaintenanceStatus.CANCELLED) {

	        throw new RuntimeException(
	                "Maintenance is already cancelled");
	    }

	    // ============================================
	    // 4. SET CANCELLATION TIME
	    // ============================================

	    maintenance.setCancelledAt(
	            LocalDateTime.now());

	    // ============================================
	    // 5. UPDATE STATUS
	    // ============================================

	    maintenance.setStatus(
	            MaintenanceStatus.CANCELLED);

	    // ============================================
	    // 6. STORE CANCELLATION REASON
	    // ============================================

	    maintenance.setRemarks(
	            request.getReason());

	    // ============================================
	    // 7. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 8. NOTIFY ASSIGNED ENGINEER
	    // ============================================

	    if (maintenance.getAssignedEngineer() != null
	            && !maintenance.getAssignedEngineer().isBlank()) {

	        notificationManagementService
	                .createNotification(

	                        CreateNotificationRequestDto
	                                .builder()

	                                .type(
	                                        NotificationType
	                                                .MAINTENANCE)

	                                .title(
	                                        "Maintenance Cancelled")

	                                .message(
	                                        "Maintenance #"
	                                                + maintenance.getId()
	                                                + " has been cancelled. "
	                                                + "Reason: "
	                                                + request.getReason())

	                                .recipient(
	                                        maintenance
	                                                .getAssignedEngineer())

	                                .build());
	    }

	    // ============================================
	    // 9. NOTIFY ADMIN
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Cancelled")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " for device "
	                                            + maintenance.getDeviceId()
	                                            + " was cancelled. "
	                                            + "Reason: "
	                                            + request.getReason())

	                            .recipient("ADMIN")

	                            .build());

	    // ============================================
	    // 10. RETURN
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	@Override
	public MaintenanceResponseDto rescheduleMaintenance(
	        Long maintenanceId,
	        RescheduleMaintenanceRequestDto request) {

	    // ============================================
	    // 1. FIND MAINTENANCE
	    // ============================================

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    // ============================================
	    // 2. VALIDATE REQUEST
	    // ============================================

	    if (request == null
	            || request.getScheduledAt() == null) {

	        throw new RuntimeException(
	                "New scheduled date is required");
	    }

	    if (request.getReason() == null
	            || request.getReason().isBlank()) {

	        throw new RuntimeException(
	                "Reschedule reason is required");
	    }

	    // ============================================
	    // 3. VALIDATE STATUS
	    // ============================================

	    if (maintenance.getStatus()
	            == MaintenanceStatus.COMPLETED) {

	        throw new RuntimeException(
	                "Completed maintenance cannot be rescheduled");
	    }

	    if (maintenance.getStatus()
	            == MaintenanceStatus.CANCELLED) {

	        throw new RuntimeException(
	                "Cancelled maintenance cannot be rescheduled");
	    }

	    // ============================================
	    // 4. VALIDATE NEW DATE
	    // ============================================

	    if (!request.getScheduledAt()
	            .isAfter(LocalDateTime.now())) {

	        throw new RuntimeException(
	                "New scheduled date must be in the future");
	    }

	    // ============================================
	    // 5. STORE NEW SCHEDULED DATE
	    // ============================================

	    maintenance.setScheduledAt(
	            request.getScheduledAt());

	    // ============================================
	    // 6. SET RESCHEDULED TIME
	    // ============================================

	    maintenance.setRescheduledAt(
	            LocalDateTime.now());

	    // ============================================
	    // 7. INCREMENT RESCHEDULE COUNT
	    // ============================================

	    Integer currentCount =
	            maintenance.getRescheduleCount();

	    if (currentCount == null) {
	        currentCount = 0;
	    }

	    maintenance.setRescheduleCount(
	            currentCount + 1);

	    // ============================================
	    // 8. SAVE REASON
	    // ============================================

	    maintenance.setRemarks(
	            request.getReason());

	    // ============================================
	    // 9. SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    // ============================================
	    // 10. NOTIFY ASSIGNED ENGINEER
	    // ============================================

	    if (maintenance.getAssignedEngineer() != null
	            && !maintenance.getAssignedEngineer().isBlank()) {

	        notificationManagementService
	                .createNotification(

	                        CreateNotificationRequestDto
	                                .builder()

	                                .type(
	                                        NotificationType
	                                                .MAINTENANCE)

	                                .title(
	                                        "Maintenance Rescheduled")

	                                .message(
	                                        "Maintenance #"
	                                                + maintenance.getId()
	                                                + " has been rescheduled "
	                                                + "to "
	                                                + request.getScheduledAt()
	                                                + ". Reason: "
	                                                + request.getReason())

	                                .recipient(
	                                        maintenance
	                                                .getAssignedEngineer())

	                                .build());
	    }

	    // ============================================
	    // 11. NOTIFY ADMIN
	    // ============================================

	    notificationManagementService
	            .createNotification(

	                    CreateNotificationRequestDto
	                            .builder()

	                            .type(
	                                    NotificationType
	                                            .MAINTENANCE)

	                            .title(
	                                    "Maintenance Rescheduled")

	                            .message(
	                                    "Maintenance #"
	                                            + maintenance.getId()
	                                            + " for device "
	                                            + maintenance.getDeviceId()
	                                            + " was rescheduled to "
	                                            + request.getScheduledAt()
	                                            + ". Reason: "
	                                            + request.getReason())

	                            .recipient("ADMIN")

	                            .build());

	    // ============================================
	    // 12. RETURN
	    // ============================================

	    return mapToResponse(
	            maintenance);
	}
	
	@Override
	public MaintenanceChecklistResponseDto getMaintenanceChecklist(
	        Long maintenanceId) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return buildChecklistResponse(
	            maintenance);
	}
	@Override
	public MaintenanceChecklistResponseDto updateMaintenanceChecklist(
	        Long maintenanceId,
	        UpdateMaintenanceChecklistRequestDto request) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    if (request == null) {

	        throw new RuntimeException(
	                "Checklist request is required");
	    }

	    // ============================================
	    // UPDATE ONLY VALUES PROVIDED IN REQUEST
	    // ============================================

	    if (request.getInspectionCompleted() != null) {

	        maintenance.setInspectionCompleted(
	                request.getInspectionCompleted());
	    }

	    if (request.getCleaningCompleted() != null) {

	        maintenance.setCleaningCompleted(
	                request.getCleaningCompleted());
	    }

	    if (request.getCalibrationCompleted() != null) {

	        maintenance.setCalibrationCompleted(
	                request.getCalibrationCompleted());
	    }

	    if (request.getFirmwareUpdated() != null) {

	        maintenance.setFirmwareUpdated(
	                request.getFirmwareUpdated());
	    }

	    if (request.getPartsVerified() != null) {

	        maintenance.setPartsVerified(
	                request.getPartsVerified());
	    }

	    if (request.getTestingCompleted() != null) {

	        maintenance.setTestingCompleted(
	                request.getTestingCompleted());
	    }

	    if (request.getCustomerVerified() != null) {

	        maintenance.setCustomerVerified(
	                request.getCustomerVerified());
	    }

	    // ============================================
	    // SAVE
	    // ============================================

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    return buildChecklistResponse(
	            maintenance);
	}
	private MaintenanceChecklistResponseDto
	buildChecklistResponse(
	        Maintenance maintenance) {

	    boolean allCompleted =

	            Boolean.TRUE.equals(
	                    maintenance.getInspectionCompleted())

	            && Boolean.TRUE.equals(
	                    maintenance.getCleaningCompleted())

	            && Boolean.TRUE.equals(
	                    maintenance.getCalibrationCompleted())

	            && Boolean.TRUE.equals(
	                    maintenance.getFirmwareUpdated())

	            && Boolean.TRUE.equals(
	                    maintenance.getPartsVerified())

	            && Boolean.TRUE.equals(
	                    maintenance.getTestingCompleted())

	            && Boolean.TRUE.equals(
	                    maintenance.getCustomerVerified());

	    return MaintenanceChecklistResponseDto
	            .builder()

	            .maintenanceId(
	                    maintenance.getId())

	            .inspectionCompleted(
	                    Boolean.TRUE.equals(
	                            maintenance.getInspectionCompleted()))

	            .cleaningCompleted(
	                    Boolean.TRUE.equals(
	                            maintenance.getCleaningCompleted()))

	            .calibrationCompleted(
	                    Boolean.TRUE.equals(
	                            maintenance.getCalibrationCompleted()))

	            .firmwareUpdated(
	                    Boolean.TRUE.equals(
	                            maintenance.getFirmwareUpdated()))

	            .partsVerified(
	                    Boolean.TRUE.equals(
	                            maintenance.getPartsVerified()))

	            .testingCompleted(
	                    Boolean.TRUE.equals(
	                            maintenance.getTestingCompleted()))

	            .customerVerified(
	                    Boolean.TRUE.equals(
	                            maintenance.getCustomerVerified()))

	            .allCompleted(
	                    allCompleted)

	            .build();
	}
	@Override
	public MaintenanceRemarksResponseDto getMaintenanceRemarks(
	        Long maintenanceId) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return MaintenanceRemarksResponseDto
	            .builder()
	            .maintenanceId(
	                    maintenance.getId())
	            .remarks(
	                    maintenance.getRemarks())
	            .build();
	}
	@Override
	public MaintenanceRemarksResponseDto updateMaintenanceRemarks(
	        Long maintenanceId,
	        UpdateMaintenanceRemarksRequestDto request) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    if (request == null
	            || request.getRemarks() == null
	            || request.getRemarks().isBlank()) {

	        throw new RuntimeException(
	                "Remarks cannot be empty");
	    }

	    maintenance.setRemarks(
	            request.getRemarks());

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    return MaintenanceRemarksResponseDto
	            .builder()
	            .maintenanceId(
	                    maintenance.getId())
	            .remarks(
	                    maintenance.getRemarks())
	            .build();
	}
	@Override
	public MaintenancePhotoResponseDto getMaintenancePhotos(
	        Long maintenanceId) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return buildMaintenancePhotoResponse(
	            maintenance);
	}
	@Override
	public MaintenancePhotoResponseDto updateMaintenancePhotos(
	        Long maintenanceId,
	        UpdateMaintenancePhotoRequestDto request) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    if (request == null) {
	        throw new RuntimeException(
	                "Photo request cannot be null");
	    }

	    if (request.getBeforePhotoUrl() != null
	            && !request.getBeforePhotoUrl().isBlank()) {

	        maintenance.setBeforePhotoUrl(
	                request.getBeforePhotoUrl());
	    }

	    if (request.getAfterPhotoUrl() != null
	            && !request.getAfterPhotoUrl().isBlank()) {

	        maintenance.setAfterPhotoUrl(
	                request.getAfterPhotoUrl());
	    }

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    return buildMaintenancePhotoResponse(
	            maintenance);
	}
	private MaintenancePhotoResponseDto
	buildMaintenancePhotoResponse(
	        Maintenance maintenance) {

	    return MaintenancePhotoResponseDto
	            .builder()

	            .maintenanceId(
	                    maintenance.getId())

	            .beforePhotoUrl(
	                    maintenance.getBeforePhotoUrl())

	            .afterPhotoUrl(
	                    maintenance.getAfterPhotoUrl())

	            .beforePhotoUploaded(
	                    maintenance.getBeforePhotoUrl() != null
	                            && !maintenance
	                                    .getBeforePhotoUrl()
	                                    .isBlank())

	            .afterPhotoUploaded(
	                    maintenance.getAfterPhotoUrl() != null
	                            && !maintenance
	                                    .getAfterPhotoUrl()
	                                    .isBlank())

	            .build();
	}
	@Override
	public MaintenanceAttachmentsResponseDto getMaintenanceAttachments(
	        Long maintenanceId) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    return buildMaintenanceAttachmentsResponse(
	            maintenance);
	}
	@Override
	public MaintenanceAttachmentsResponseDto updateMaintenanceAttachments(
	        Long maintenanceId,
	        UpdateMaintenanceAttachmentsRequestDto request) {

	    Maintenance maintenance =
	            maintenanceRepository
	                    .findById(maintenanceId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Maintenance not found"));

	    if (request == null) {

	        throw new RuntimeException(
	                "Attachment request cannot be null");
	    }

	    List<String> attachmentUrls =
	            request.getAttachmentUrls();

	    if (attachmentUrls == null) {

	        throw new RuntimeException(
	                "Attachment URLs are required");
	    }

	    // Remove empty values
	    List<String> validUrls =
	            attachmentUrls.stream()
	                    .filter(url ->
	                            url != null
	                            && !url.isBlank())
	                    .map(String::trim)
	                    .toList();

	    // Remove duplicate URLs
	    validUrls =
	            validUrls.stream()
	                    .distinct()
	                    .toList();

	    if (validUrls.size() > 10) {

	        throw new RuntimeException(
	                "Maximum 10 attachments are allowed");
	    }

	    // Store as one String in Maintenance
	    String storedUrls =
	            String.join(
	                    "|||",
	                    validUrls);

	    maintenance.setAttachmentUrls(
	            storedUrls);

	    maintenance =
	            maintenanceRepository.save(
	                    maintenance);

	    return buildMaintenanceAttachmentsResponse(
	            maintenance);
	}
	private MaintenanceAttachmentsResponseDto
	buildMaintenanceAttachmentsResponse(
	        Maintenance maintenance) {

	    List<String> attachmentUrls =
	            parseAttachmentUrls(
	                    maintenance.getAttachmentUrls());

	    return MaintenanceAttachmentsResponseDto
	            .builder()

	            .maintenanceId(
	                    maintenance.getId())

	            .attachmentUrls(
	                    attachmentUrls)

	            .attachmentCount(
	                    attachmentUrls.size())

	            .build();
	}
	private List<String> parseAttachmentUrls(
	        String attachmentUrls) {

	    if (attachmentUrls == null
	            || attachmentUrls.isBlank()) {

	        return Collections.emptyList();
	    }

	    return Arrays.stream(
	                    attachmentUrls.split(
	                            "\\|\\|\\|"))
	            .map(String::trim)
	            .filter(url ->
	                    !url.isBlank())
	            .distinct()
	            .toList();
	}
	private MaintenanceResponseDto mapToResponse(
	        Maintenance maintenance) {

	    return MaintenanceResponseDto.builder()

	            .id(
	                    maintenance.getId())

	            .deviceId(
	                    maintenance.getDeviceId())

	            .maintenanceType(
	                    maintenance.getMaintenanceType())

	            .source(
	                    maintenance.getSource())

	            .priority(
	                    maintenance.getPriority())

	            .status(
	                    maintenance.getStatus())

	            .title(
	                    maintenance.getTitle())

	            .description(
	                    maintenance.getDescription())

	            .assignedEngineerId(
	                    maintenance.getAssignedEngineerId())

	            .assignedEngineer(
	                    maintenance.getAssignedEngineer())

	            .assignedAt(
	                    maintenance.getAssignedAt())

	            .preferredDate(
	                    maintenance.getPreferredDate())

	            .scheduledAt(
	                    maintenance.getScheduledAt())

	            .startedAt(
	                    maintenance.getStartedAt())

	            .completedAt(
	                    maintenance.getCompletedAt())

	            .cancelledAt(
	                    maintenance.getCancelledAt())

	            .rescheduledAt(
	                    maintenance.getRescheduledAt())

	            .rescheduleCount(
	                    maintenance.getRescheduleCount())

	            .autoReassignmentCount(
	                    maintenance.getAutoReassignmentCount())

	            .assignmentFailureCount(
	                    maintenance.getAssignmentFailureCount())

	            .manualAssignmentRequired(
	                    maintenance.getManualAssignmentRequired())

	            .lastAssignmentFailedAt(
	                    maintenance.getLastAssignmentFailedAt())

	            .lastAssignmentFailureReason(
	                    maintenance.getLastAssignmentFailureReason())

	            .estimatedDuration(
	                    maintenance.getEstimatedDuration())

	            .actualDuration(
	                    maintenance.getActualDuration())

	            .maintenanceCost(
	                    maintenance.getMaintenanceCost())

	            .totalCost(
	                    maintenance.getTotalCost() != null
	                            ? maintenance.getTotalCost()
	                            : maintenance.getMaintenanceCost())
	            
	            .attachmentUrls(
	                    parseAttachmentUrls(
	                            maintenance.getAttachmentUrls()))

	            .attachmentCount(
	                    parseAttachmentUrls(
	                            maintenance.getAttachmentUrls())
	                            .size())

	            .replacementParts(
	                    maintenance.getReplacementParts())

	            .remarks(
	                    maintenance.getRemarks())

	            .build();
	}
}