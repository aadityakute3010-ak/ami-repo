package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import com.ami.entity.InstallationAttachment;
import com.ami.repository.InstallationAttachmentRepository;
import com.ami.dto.requests.UploadInstallationAttachmentRequestDto;
import com.ami.dto.responses.InstallationAttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ami.dto.requests.AddInstallationRemarkRequestDto;
import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.requests.CancelInstallationRequestDto;
import com.ami.dto.requests.CompleteInstallationRequestDto;
import com.ami.dto.requests.CreateInstallationRequestDto;
import com.ami.dto.requests.InstallationAssignmentFailureRequestDto;
import com.ami.dto.requests.InstallationChecklistRequestDto;
import com.ami.dto.requests.ReassignInstallationEngineerRequestDto;
import com.ami.dto.requests.RescheduleInstallationRequestDto;
import com.ami.dto.requests.UpdateInstallationRequestDto;
import com.ami.dto.responses.InstallationAnalyticsResponseDto;
import com.ami.dto.responses.InstallationAssignmentAttemptResponseDto;
import com.ami.dto.responses.InstallationChecklistResponseDto;
import com.ami.dto.responses.InstallationDashboardResponseDto;
import com.ami.dto.responses.InstallationEngineerResponseDto;
import com.ami.dto.responses.InstallationEngineerWorkloadResponseDto;
import com.ami.dto.responses.InstallationHistoryResponseDto;
import com.ami.dto.responses.InstallationPhotoResponseDto;
import com.ami.dto.responses.InstallationRemarkResponseDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.dto.responses.InstallationSourceSummaryResponseDto;
import com.ami.dto.responses.InstallationStatisticsResponseDto;
import com.ami.dto.responses.InstallationTimelineResponseDto;
import com.ami.dto.responses.PageResponseDto;
import com.ami.entity.Installation;
import com.ami.entity.InstallationAssignmentAttempt;
import com.ami.entity.User;
import com.ami.enums.AssignmentStatus;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;
import com.ami.enums.InstallationTimelineEvent;
import com.ami.enums.RoleType;
import com.ami.repository.InstallationAssignmentAttemptRepository;
import com.ami.repository.InstallationChecklistRepository;
import com.ami.repository.InstallationHistoryRepository;
import com.ami.repository.InstallationPhotoRepository;
import com.ami.repository.InstallationRemarkRepository;
import com.ami.repository.InstallationRepository;
import com.ami.repository.InstallationTimelineRepository;
import com.ami.repository.UserRepository;
import com.ami.service.InstallationNotificationService;
import com.ami.service.InstallationService;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.ami.entity.InstallationChecklist;
import com.ami.entity.InstallationPhoto;
import com.ami.entity.InstallationTimeline;
import com.ami.entity.InstallationHistory;
import com.ami.entity.InstallationRemark;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;



import com.ami.dto.responses.InstallationEngineerResponseDto;
import com.ami.dto.responses.InstallationTimelineResponseDto;
@Service
public class InstallationServiceImpl implements InstallationService {

    private final InstallationRepository installationRepository;

    private final UserRepository userRepository;
    
    private final InstallationChecklistRepository installationChecklistRepository;

    private final InstallationPhotoRepository installationPhotoRepository;

    private final InstallationTimelineRepository installationTimelineRepository;

    private final InstallationHistoryRepository installationHistoryRepository;

    private final InstallationRemarkRepository installationRemarkRepository;
    
    private final InstallationNotificationService
    installationNotificationService;
    
    private final InstallationAssignmentAttemptRepository
    installationAssignmentAttemptRepository;
    
    private final InstallationAttachmentRepository
    installationAttachmentRepository;
   
    public InstallationServiceImpl(
            InstallationRepository installationRepository,
            UserRepository userRepository,
            InstallationTimelineRepository installationTimelineRepository,
            InstallationHistoryRepository installationHistoryRepository,
            InstallationChecklistRepository installationChecklistRepository,
            InstallationPhotoRepository installationPhotoRepository,
            InstallationRemarkRepository installationRemarkRepository,
            InstallationAssignmentAttemptRepository installationAssignmentAttemptRepository,
            InstallationNotificationService installationNotificationService,
            InstallationAttachmentRepository
            installationAttachmentRepository) {

        this.installationRepository = installationRepository;
        this.userRepository = userRepository;
        this.installationTimelineRepository = installationTimelineRepository;
        this.installationHistoryRepository = installationHistoryRepository;
        this.installationChecklistRepository = installationChecklistRepository;
        this.installationPhotoRepository = installationPhotoRepository;
        this.installationRemarkRepository = installationRemarkRepository;
        this.installationAssignmentAttemptRepository =
                installationAssignmentAttemptRepository;
        this.installationNotificationService =
                installationNotificationService;
        this.installationAttachmentRepository =
                installationAttachmentRepository;
    }
    @Override
    public InstallationResponseDto createInstallation(
            CreateInstallationRequestDto request) {
    	
    	if (request.getScheduledDate() != null
    	        && request.getScheduledDate()
    	                .isBefore(LocalDateTime.now())) {

    	    throw new RuntimeException(
    	            "Scheduled date cannot be in the past.");
    	}

        Installation installation = Installation.builder()
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .meterNumber(request.getMeterNumber())
                .serialNumber(request.getSerialNumber())
                .source(request.getSource())
                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .state(request.getState())
                .city(request.getCity())
                .zone(request.getZone())
                .area(request.getArea())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .priority(request.getPriority())
                .scheduledDate(request.getScheduledDate())
                .status(InstallationStatus.PENDING)
                .build();

        installation = installationRepository.save(installation);

        InstallationChecklist checklist = InstallationChecklist.builder()
                .installation(installation)
                .meterMounted(false)
                .wiringCompleted(false)
                .communicationVerified(false)
                .meterActivated(false)
                .readingVerified(false)
                .customerVerified(false)
                .build();

        checklist = installationChecklistRepository.save(checklist);

        installation.setChecklist(checklist);

        if (request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            InstallationRemark remark = InstallationRemark.builder()
                    .installation(installation)
                    .remark(request.getRemarks())
                    .createdBy("SYSTEM")
                    .build();

            remark = installationRemarkRepository.save(remark);

            installation.getRemarks().add(remark);
        }

        addTimeline(
                installation,
                "INSTALLATION_CREATED",
                "Installation created successfully",
                "SYSTEM");

        addHistory(
                installation,
                "CREATE",
                "Installation created",
                "SUCCESS",
                "SYSTEM");

        installation = installationRepository.save(installation);

        return mapToResponse(installation);
    }
    @Override
    public PageResponseDto<InstallationResponseDto> getAllInstallations(
            String search,
            InstallationStatus status,
            InstallationPriority priority,
            InstallationSource source,
            String city,
            Long engineerId,
            String customerId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Integer page,
            Integer size,
            String sort) {

        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        String[] sortParts = sort.split(",");

        String sortBy = sortParts[0];

        Sort.Direction direction = Sort.Direction.DESC;

        if (sortParts.length > 1) {
            direction = Sort.Direction.fromString(sortParts[1]);
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy));

        Specification<Installation> specification =
                (root, query, cb) -> cb.conjunction();

        if (status != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("status"), status));
        }

        if (priority != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("priority"), priority));
        }

        if (source != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("source"), source));
        }

        if (city != null && !city.isBlank()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(root.get("city")),
                                    "%" + city.toLowerCase() + "%"));
        }

        if (engineerId != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("assignedEngineer").get("id"),
                                    engineerId));
        }

        if (customerId != null && !customerId.isBlank()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("customerId"),
                                    customerId));
        }

        if (fromDate != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("createdAt"),
                                    fromDate));
        }

        if (toDate != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("createdAt"),
                                    toDate));
        }

        if (search != null && !search.isBlank()) {

            String keyword = "%" + search.toLowerCase() + "%";

            specification = specification.and(
                    (root, query, cb) ->
                            cb.or(

                                    cb.like(cb.lower(root.get("installationNumber")), keyword),

                                    cb.like(cb.lower(root.get("deviceId")), keyword),

                                    cb.like(cb.lower(root.get("deviceName")), keyword),

                                    cb.like(cb.lower(root.get("meterNumber")), keyword),

                                    cb.like(cb.lower(root.get("serialNumber")), keyword),

                                    cb.like(cb.lower(root.get("customerName")), keyword),

                                    cb.like(cb.lower(root.get("customerPhone")), keyword),

                                    cb.like(cb.lower(root.get("customerEmail")), keyword),

                                    cb.like(cb.lower(root.get("city")), keyword),

                                    cb.like(cb.lower(root.get("state")), keyword),

                                    cb.like(cb.lower(root.get("zone")), keyword),

                                    cb.like(cb.lower(root.get("area")), keyword),

                                    cb.like(cb.lower(root.get("address")), keyword)

                            ));
        }

        Page<Installation> installationPage =
                installationRepository.findAll(
                        specification,
                        pageable);

        List<InstallationResponseDto> response =
                installationPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponseDto.<InstallationResponseDto>builder()
                .content(response)
                .page(installationPage.getNumber())
                .size(installationPage.getSize())
                .totalElements(installationPage.getTotalElements())
                .totalPages(installationPage.getTotalPages())
                .last(installationPage.isLast())
                .build();
    }
    @Override
    public InstallationResponseDto getInstallationById(
            Long id) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));

        return mapToResponse(installation);
    }
    @Override
    public InstallationResponseDto updateInstallation(
            Long id,
            UpdateInstallationRequestDto request) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));
        
        if (installation.getStatus() == InstallationStatus.COMPLETED) {
            throw new RuntimeException(
                    "Completed installation cannot be modified.");
        }

        if (installation.getStatus() == InstallationStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cancelled installation cannot be modified.");
        }
        
        if (request.getScheduledDate() != null
                && request.getScheduledDate()
                        .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Scheduled date cannot be in the past.");
        }

        if (request.getCustomerId() != null)
            installation.setCustomerId(request.getCustomerId());

        if (request.getCustomerName() != null)
            installation.setCustomerName(request.getCustomerName());

        if (request.getCustomerPhone() != null)
            installation.setCustomerPhone(request.getCustomerPhone());

        if (request.getCustomerEmail() != null)
            installation.setCustomerEmail(request.getCustomerEmail());

        if (request.getAddress() != null)
            installation.setAddress(request.getAddress());

        if (request.getCity() != null)
            installation.setCity(request.getCity());

        if (request.getState() != null)
            installation.setState(request.getState());

        if (request.getZone() != null)
            installation.setZone(request.getZone());

        if (request.getArea() != null)
            installation.setArea(request.getArea());

        if (request.getDeviceId() != null)
            installation.setDeviceId(request.getDeviceId());

        if (request.getDeviceName() != null)
            installation.setDeviceName(request.getDeviceName());

        if (request.getMeterNumber() != null)
            installation.setMeterNumber(request.getMeterNumber());

        if (request.getSerialNumber() != null)
            installation.setSerialNumber(request.getSerialNumber());

        if (request.getSource() != null)
            installation.setSource(request.getSource());

        if (request.getPriority() != null)
            installation.setPriority(request.getPriority());

        if (request.getScheduledDate() != null)
            installation.setScheduledDate(request.getScheduledDate());

        if (request.getLatitude() != null)
            installation.setLatitude(request.getLatitude());

        if (request.getLongitude() != null)
            installation.setLongitude(request.getLongitude());

        installation = installationRepository.save(installation);

        addTimeline(
                installation,
                "INSTALLATION_UPDATED",
                "Installation details updated",
                "SYSTEM");

        addHistory(
                installation,
                "UPDATE",
                "Installation updated",
                "SUCCESS",
                "SYSTEM");

        installation = installationRepository.save(installation);

        return mapToResponse(installation);
    }
    @Override
    public void deleteInstallation(
            Long id) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));

        addHistory(
                installation,
                "DELETE",
                "Installation deleted",
                "SUCCESS",
                "SYSTEM");

        installationRepository.delete(installation);
    }
    @Override
    public InstallationResponseDto assignEngineer(
            Long id,
            AssignInstallationEngineerRequestDto request) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));
        
        if (installation.getStatus() == InstallationStatus.COMPLETED
                || installation.getStatus() == InstallationStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cannot assign engineer for completed or cancelled installation.");
        }
        
        if (installation.getAssignedEngineer() != null
                && installation.getStatus() == InstallationStatus.ASSIGNED) {

            throw new RuntimeException(
                    "Installation is already assigned.");
        }

        User engineer = userRepository
                .findById(request.getEngineerId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found with id : "
                                        + request.getEngineerId()));

        if (engineer.getRole() != RoleType.SERVICE_ENGINEER) {
            throw new RuntimeException(
                    "Selected user is not a Service Engineer");
        }
        
        if (engineer.getAttendanceStatus()
                != EngineerAttendanceStatus.PRESENT) {

            throw new RuntimeException(
                    "Engineer is not present.");
        }

        if (engineer.getAvailabilityStatus()
                != EngineerAvailabilityStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Engineer is currently unavailable.");
        }

        installation.setAssignedEngineer(engineer);
        installation.setAssignedBy("SYSTEM");

        installation.setAssignedAt(LocalDateTime.now());

        installation.setAssignmentStatus(
                AssignmentStatus.SUCCESS);

        installation.setLastAssignmentAttempt(
                LocalDateTime.now());

      
        installation = installationRepository.save(installation);

        addTimeline(
                installation,
                "ENGINEER_ASSIGNED",
                "Engineer assigned",
                engineer.getFirstName() + " " + engineer.getLastName());

        addHistory(
                installation,
                "ASSIGN_ENGINEER",
                "Engineer assigned successfully",
                "SUCCESS",
                "SYSTEM");

        installation = installationRepository.save(installation);
        
        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Engineer Assigned",
                "Installation assigned to "
                        + engineer.getFirstName()
                        + " "
                        + engineer.getLastName(),
                "ENGINEER_ASSIGNED",
                "SYSTEM");

        return mapToResponse(installation);
    }
    @Override
    public InstallationResponseDto startInstallation(
            Long id) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));
        
        if (installation.getStatus() == InstallationStatus.COMPLETED
                || installation.getStatus() == InstallationStatus.CANCELLED) {

            throw new RuntimeException(
                    "Installation cannot be started.");
        }

        if (installation.getAssignedEngineer() == null) {
            throw new RuntimeException(
                    "Installation is not assigned to any engineer.");
        }

        if (installation.getStatus() != InstallationStatus.ASSIGNED) {
            throw new RuntimeException(
                    "Only assigned installations can be started.");
        }

        installation.setStatus(InstallationStatus.IN_PROGRESS);
        installation.setStartedAt(LocalDateTime.now());

        installation = installationRepository.save(installation);

        addTimeline(
                installation,
                "INSTALLATION_STARTED",
                "Installation work started",
                installation.getAssignedEngineer().getFirstName()
                        + " "
                        + installation.getAssignedEngineer().getLastName());

        addHistory(
                installation,
                "START",
                "Installation started",
                "SUCCESS",
                installation.getAssignedEngineer().getFirstName());

        installation = installationRepository.save(installation);

        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Installation Started",
                "Engineer started installation.",
                "INSTALLATION_STARTED",
                installation.getAssignedEngineer() != null
                        ? installation.getAssignedEngineer().getFirstName()
                        : "SYSTEM");
        
        return mapToResponse(installation);
    }
    @Override
    public InstallationResponseDto completeInstallation(
            Long id,
            CompleteInstallationRequestDto request) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));

        if (installation.getStatus() != InstallationStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "Only In Progress installations can be completed.");
        }

        InstallationChecklist checklist = installation.getChecklist();

        if (checklist == null) {
            checklist = InstallationChecklist.builder()
                    .installation(installation)
                    .build();
        }

        InstallationChecklistRequestDto dto = request.getChecklist();

        checklist.setMeterMounted(dto.getMeterMounted());
        checklist.setWiringCompleted(dto.getWiringCompleted());
        checklist.setCommunicationVerified(dto.getCommunicationVerified());
        checklist.setMeterActivated(dto.getMeterActivated());
        checklist.setReadingVerified(dto.getReadingVerified());
        checklist.setCustomerVerified(dto.getCustomerVerified());
        
        if (Boolean.TRUE.equals(checklist.getMandatory())) {

            if (!Boolean.TRUE.equals(checklist.getMeterMounted())
                    || !Boolean.TRUE.equals(checklist.getWiringCompleted())
                    || !Boolean.TRUE.equals(checklist.getCommunicationVerified())
                    || !Boolean.TRUE.equals(checklist.getMeterActivated())
                    || !Boolean.TRUE.equals(checklist.getReadingVerified())
                    || !Boolean.TRUE.equals(checklist.getCustomerVerified())) {

                throw new RuntimeException(
                        "Complete all mandatory checklist items before completing the installation.");
            }
        }

        checklist = installationChecklistRepository.save(checklist);

        installation.setChecklist(checklist);
        if (installation.getPhotos() == null
                || installation.getPhotos().isEmpty()) {

            throw new RuntimeException(
                    "Upload at least one installation photo before completion.");
        }
        installation.setCompletedAt(LocalDateTime.now());
        installation.setStatus(InstallationStatus.COMPLETED);

        installation = installationRepository.save(installation);

        if (request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            InstallationRemark remark = InstallationRemark.builder()
                    .installation(installation)
                    .remark(request.getRemarks())
                    .createdBy(
                            installation.getAssignedEngineer() != null
                                    ? installation.getAssignedEngineer().getFirstName()
                                    : "SYSTEM")
                    .build();

            remark = installationRemarkRepository.save(remark);

            installation.getRemarks().add(remark);
        }

        addTimeline(
                installation,
                "INSTALLATION_COMPLETED",
                "Installation completed successfully",
                installation.getAssignedEngineer() != null
                        ? installation.getAssignedEngineer().getFirstName()
                        : "SYSTEM");

        addHistory(
                installation,
                "COMPLETE",
                "Installation completed",
                "SUCCESS",
                installation.getAssignedEngineer() != null
                        ? installation.getAssignedEngineer().getFirstName()
                        : "SYSTEM");

        installation = installationRepository.save(installation);
        
        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Installation Completed",
                "Installation completed successfully.",
                "INSTALLATION_COMPLETED",
                installation.getAssignedEngineer() != null
                        ? installation.getAssignedEngineer().getFirstName()
                        : "SYSTEM");

        return mapToResponse(installation);
    }
    @Override
    public InstallationResponseDto cancelInstallation(
            Long id,
            CancelInstallationRequestDto request) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));
        
        if (request.getReason() == null
                || request.getReason().isBlank()) {

            throw new RuntimeException(
                    "Cancellation reason is required.");
        }

        if (installation.getStatus() == InstallationStatus.COMPLETED) {

            throw new RuntimeException(
                    "Completed installation cannot be cancelled.");
        }

        if (installation.getStatus() == InstallationStatus.CANCELLED) {

            throw new RuntimeException(
                    "Installation is already cancelled.");
        }

        installation.setStatus(InstallationStatus.CANCELLED);
        
        installation.setAssignmentStatus(
                AssignmentStatus.FAILED);

        installation = installationRepository.save(installation);

        if (request.getReason() != null &&
                !request.getReason().isBlank()) {

            InstallationRemark remark = InstallationRemark.builder()
                    .installation(installation)
                    .remark(request.getReason())
                    .createdBy("SYSTEM")
                    .build();

            remark = installationRemarkRepository.save(remark);

            installation.getRemarks().add(remark);
        }

        addTimeline(
                installation,
                "CANCELLED",
                request.getReason(),
                "SYSTEM");

        addHistory(
                installation,
                "CANCEL",
                request.getReason(),
                "SUCCESS",
                "SYSTEM");

        installation = installationRepository.save(installation);
        
        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Installation Cancelled",
                "Installation has been cancelled.",
                "INSTALLATION_CANCELLED",
                "SYSTEM");

        return mapToResponse(installation);
    }
    @Override
    public InstallationResponseDto rescheduleInstallation(
            Long id,
            RescheduleInstallationRequestDto request) {

        Installation installation = installationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : " + id));

        if (installation.getStatus() == InstallationStatus.COMPLETED) {
            throw new RuntimeException(
                    "Completed installation cannot be rescheduled.");
        }

        installation.setScheduledDate(request.getScheduledDate());

        installation = installationRepository.save(installation);

        if (request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            InstallationRemark remark = InstallationRemark.builder()
                    .installation(installation)
                    .remark(request.getRemarks())
                    .createdBy("SYSTEM")
                    .build();

            remark = installationRemarkRepository.save(remark);

            installation.getRemarks().add(remark);
        }

        addTimeline(
                installation,
                "INSTALLATION_RESCHEDULED",
                "Installation rescheduled to " + request.getScheduledDate(),
                "SYSTEM");

        addHistory(
                installation,
                "RESCHEDULE",
                "Installation rescheduled",
                "SUCCESS",
                "SYSTEM");

        installation = installationRepository.save(installation);

        return mapToResponse(installation);
    }
    @Override
    public InstallationDashboardResponseDto getDashboard() {

        return InstallationDashboardResponseDto.builder()

                .total(installationRepository.count())

                .pending(installationRepository.countByStatus(
                        InstallationStatus.PENDING))

                .assigned(installationRepository.countByStatus(
                        InstallationStatus.ASSIGNED))

                .inProgress(installationRepository.countByStatus(
                        InstallationStatus.IN_PROGRESS))

                .completed(installationRepository.countByStatus(
                        InstallationStatus.COMPLETED))

                .cancelled(installationRepository.countByStatus(
                        InstallationStatus.CANCELLED))

                .todayScheduled(
                        installationRepository.countByScheduledDateBetween(
                                LocalDateTime.now().toLocalDate().atStartOfDay(),
                                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()))

                .activeEngineers(
                        installationRepository.countByAssignedEngineerIsNotNull())

                .build();
    }
    @Override
    public InstallationStatisticsResponseDto getStatistics() {

        LocalDateTime todayStart =
                LocalDateTime.now().toLocalDate().atStartOfDay();

        LocalDateTime tomorrowStart =
                todayStart.plusDays(1);

        LocalDateTime weekStart =
                todayStart.minusDays(todayStart.getDayOfWeek().getValue() - 1);

        LocalDateTime monthStart =
                todayStart.withDayOfMonth(1);

        return InstallationStatisticsResponseDto.builder()

                .total(installationRepository.count())

                .pending(installationRepository.countByStatus(
                        InstallationStatus.PENDING))

                .assigned(installationRepository.countByStatus(
                        InstallationStatus.ASSIGNED))

                .inProgress(installationRepository.countByStatus(
                        InstallationStatus.IN_PROGRESS))

                .completed(installationRepository.countByStatus(
                        InstallationStatus.COMPLETED))

                .cancelled(installationRepository.countByStatus(
                        InstallationStatus.CANCELLED))

                .overdue(
                        installationRepository
                                .countByStatusNotAndScheduledDateBefore(
                                        InstallationStatus.COMPLETED,
                                        LocalDateTime.now()))

                .today(
                        installationRepository.countByScheduledDateBetween(
                                todayStart,
                                tomorrowStart))

                .thisWeek(
                        installationRepository.countByScheduledDateBetween(
                                weekStart,
                                tomorrowStart))

                .thisMonth(
                        installationRepository.countByScheduledDateBetween(
                                monthStart,
                                tomorrowStart))

                .build();
    }
    @Override
    public InstallationAnalyticsResponseDto getAnalytics() {

        List<Installation> completedInstallations =
                installationRepository.findByStatus(
                        InstallationStatus.COMPLETED);

        long totalInstallations = installationRepository.count();

        long completedCount = completedInstallations.size();

        long cancelledCount =
                installationRepository.countByStatus(
                        InstallationStatus.CANCELLED);

        long pendingCount =
                installationRepository.countByStatus(
                        InstallationStatus.PENDING);

        double completionRate = totalInstallations == 0
                ? 0
                : (completedCount * 100.0) / totalInstallations;

        long assigned =
                installationRepository.countByAssignedEngineerIsNotNull();

        double engineerUtilization = totalInstallations == 0
                ? 0
                : (assigned * 100.0) / totalInstallations;

        double averageCompletionTime = 0;

        if (!completedInstallations.isEmpty()) {

            long totalHours = 0;

            for (Installation installation : completedInstallations) {

                if (installation.getStartedAt() != null &&
                        installation.getCompletedAt() != null) {

                    totalHours += java.time.Duration
                            .between(
                                    installation.getStartedAt(),
                                    installation.getCompletedAt())
                            .toHours();
                }
            }

            averageCompletionTime =
                    (double) totalHours
                            / completedInstallations.size();
        }

        return InstallationAnalyticsResponseDto.builder()

                .totalInstallations(totalInstallations)

                .completedInstallations(completedCount)

                .cancelledInstallations(cancelledCount)

                .pendingInstallations(pendingCount)

                .completionRate(completionRate)

                .engineerUtilization(engineerUtilization)

                .averageCompletionTime(averageCompletionTime)

                .monthlyTrend(List.of())

                .build();
    }
    @Override
    public List<InstallationSourceSummaryResponseDto> getSourceSummary() {

        return List.of(

                InstallationSourceSummaryResponseDto.builder()
                        .source(InstallationSource.ENERGY)
                        .total(installationRepository.countBySource(InstallationSource.ENERGY))
                        .completed(installationRepository.countBySourceAndStatus(
                                InstallationSource.ENERGY,
                                InstallationStatus.COMPLETED))
                        .pending(installationRepository.countBySourceAndStatus(
                                InstallationSource.ENERGY,
                                InstallationStatus.PENDING))
                        .inProgress(installationRepository.countBySourceAndStatus(
                                InstallationSource.ENERGY,
                                InstallationStatus.IN_PROGRESS))
                        .cancelled(installationRepository.countBySourceAndStatus(
                                InstallationSource.ENERGY,
                                InstallationStatus.CANCELLED))
                        .build(),

                InstallationSourceSummaryResponseDto.builder()
                        .source(InstallationSource.WATER)
                        .total(installationRepository.countBySource(InstallationSource.WATER))
                        .completed(installationRepository.countBySourceAndStatus(
                                InstallationSource.WATER,
                                InstallationStatus.COMPLETED))
                        .pending(installationRepository.countBySourceAndStatus(
                                InstallationSource.WATER,
                                InstallationStatus.PENDING))
                        .inProgress(installationRepository.countBySourceAndStatus(
                                InstallationSource.WATER,
                                InstallationStatus.IN_PROGRESS))
                        .cancelled(installationRepository.countBySourceAndStatus(
                                InstallationSource.WATER,
                                InstallationStatus.CANCELLED))
                        .build(),

                InstallationSourceSummaryResponseDto.builder()
                        .source(InstallationSource.GAS)
                        .total(installationRepository.countBySource(InstallationSource.GAS))
                        .completed(installationRepository.countBySourceAndStatus(
                                InstallationSource.GAS,
                                InstallationStatus.COMPLETED))
                        .pending(installationRepository.countBySourceAndStatus(
                                InstallationSource.GAS,
                                InstallationStatus.PENDING))
                        .inProgress(installationRepository.countBySourceAndStatus(
                                InstallationSource.GAS,
                                InstallationStatus.IN_PROGRESS))
                        .cancelled(installationRepository.countBySourceAndStatus(
                                InstallationSource.GAS,
                                InstallationStatus.CANCELLED))
                        .build(),

                InstallationSourceSummaryResponseDto.builder()
                        .source(InstallationSource.SOLAR)
                        .total(installationRepository.countBySource(InstallationSource.SOLAR))
                        .completed(installationRepository.countBySourceAndStatus(
                                InstallationSource.SOLAR,
                                InstallationStatus.COMPLETED))
                        .pending(installationRepository.countBySourceAndStatus(
                                InstallationSource.SOLAR,
                                InstallationStatus.PENDING))
                        .inProgress(installationRepository.countBySourceAndStatus(
                                InstallationSource.SOLAR,
                                InstallationStatus.IN_PROGRESS))
                        .cancelled(installationRepository.countBySourceAndStatus(
                                InstallationSource.SOLAR,
                                InstallationStatus.CANCELLED))
                        .build()

        );
    }
    @Override
    public List<InstallationEngineerWorkloadResponseDto> getEngineerWorkload() {

        List<User> engineers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == RoleType.SERVICE_ENGINEER)
                .toList();

        return engineers.stream()
                .map(engineer ->

                        InstallationEngineerWorkloadResponseDto.builder()

                                .engineerId(engineer.getId())

                                .engineerName(
                                        engineer.getFirstName()
                                                + " "
                                                + engineer.getLastName())

                                .assignedInstallations(
                                        installationRepository
                                                .countByAssignedEngineerIdAndStatus(
                                                        engineer.getId(),
                                                        InstallationStatus.ASSIGNED))

                                .inProgressInstallations(
                                        installationRepository
                                                .countByAssignedEngineerIdAndStatus(
                                                        engineer.getId(),
                                                        InstallationStatus.IN_PROGRESS))

                                .completedInstallations(
                                        installationRepository
                                                .countByAssignedEngineerIdAndStatus(
                                                        engineer.getId(),
                                                        InstallationStatus.COMPLETED))

                                .cancelledInstallations(
                                        installationRepository
                                                .countByAssignedEngineerIdAndStatus(
                                                        engineer.getId(),
                                                        InstallationStatus.CANCELLED))

                                .build())

                .toList();
    }
    @Override
    public List<InstallationEngineerWorkloadResponseDto> getAvailableEngineers() {

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == RoleType.SERVICE_ENGINEER)
                .filter(user -> user.getAvailabilityStatus() != null
                        && user.getAvailabilityStatus().name().equals("AVAILABLE"))
                .map(user -> InstallationEngineerWorkloadResponseDto.builder()

                        .engineerId(user.getId())

                        .engineerName(
                                user.getFirstName()
                                        + " "
                                        + user.getLastName())

                        .assignedInstallations(
                                installationRepository.countByAssignedEngineerId(
                                        user.getId()))

                        .inProgressInstallations(
                                installationRepository
                                        .countByAssignedEngineerIdAndStatus(
                                                user.getId(),
                                                InstallationStatus.IN_PROGRESS))

                        .completedInstallations(
                                installationRepository
                                        .countByAssignedEngineerIdAndStatus(
                                                user.getId(),
                                                InstallationStatus.COMPLETED))

                        .cancelledInstallations(
                                installationRepository
                                        .countByAssignedEngineerIdAndStatus(
                                                user.getId(),
                                                InstallationStatus.CANCELLED))

                        .build())

                .toList();
    }
  
    @Override
    public List<InstallationHistoryResponseDto> getHistory(
            Long installationId) {

        installationRepository.findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        return installationHistoryRepository
                .findByInstallationIdOrderByCreatedAtDesc(installationId)
                .stream()
                .map(this::mapHistory)
                .toList();
    }
    @Override
    public List<InstallationEngineerWorkloadResponseDto> getAllEngineers() {
        return getEngineerWorkload();
    }
    @Override
    public List<InstallationTimelineResponseDto> getTimeline(
            Long installationId) {

        installationRepository.findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        return installationTimelineRepository
                .findByInstallationIdOrderByEventTimeAsc(installationId)
                .stream()
                .map(this::mapTimeline)
                .toList();
    }
    @Override
    public List<InstallationRemarkResponseDto> getRemarks(
            Long installationId) {

        installationRepository.findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        return installationRemarkRepository
                .findByInstallationIdOrderByCreatedAtDesc(installationId)
                .stream()
                .map(this::mapRemark)
                .toList();
    }
    @Override
    public InstallationRemarkResponseDto addRemark(
            Long installationId,
            AddInstallationRemarkRequestDto request) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        InstallationRemark remark = InstallationRemark.builder()
                .installation(installation)
                .remark(request.getRemark())
                .createdBy("SYSTEM")
                .build();

        remark = installationRemarkRepository.save(remark);

        installation.getRemarks().add(remark);

        addTimeline(
                installation,
                "REMARK_ADDED",
                "Remark added",
                "SYSTEM");

        addHistory(
                installation,
                "ADD_REMARK",
                "Remark added successfully",
                "SUCCESS",
                "SYSTEM");

        return mapRemark(remark);
    }
    @Override
    public void deleteRemark(
            Long installationId,
            Long remarkId) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        InstallationRemark remark =
                installationRemarkRepository.findById(remarkId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Remark not found"));

        if (!remark.getInstallation().getId().equals(installationId)) {
            throw new RuntimeException(
                    "Remark does not belong to this installation.");
        }

        installationRemarkRepository.delete(remark);

        addTimeline(
                installation,
                "REMARK_DELETED",
                "Remark deleted",
                "SYSTEM");

        addHistory(
                installation,
                "DELETE_REMARK",
                "Remark deleted",
                "SUCCESS",
                "SYSTEM");
    }
    @Override
    public InstallationChecklistResponseDto getChecklist(
            Long installationId) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found"));

        return mapChecklist(installation.getChecklist());
    }
    @Override
    public InstallationChecklistResponseDto updateChecklist(
            Long installationId,
            InstallationChecklistRequestDto request) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found"));

        InstallationChecklist checklist =
                installation.getChecklist();

        if (checklist == null) {
            checklist = InstallationChecklist.builder()
                    .installation(installation)
                    .build();
        }

        checklist.setMeterMounted(request.getMeterMounted());
        checklist.setWiringCompleted(request.getWiringCompleted());
        checklist.setCommunicationVerified(request.getCommunicationVerified());
        checklist.setMeterActivated(request.getMeterActivated());
        checklist.setReadingVerified(request.getReadingVerified());
        checklist.setCustomerVerified(request.getCustomerVerified());

        checklist = installationChecklistRepository.save(checklist);

        installation.setChecklist(checklist);

        addTimeline(
                installation,
                "CHECKLIST_UPDATED",
                "Checklist updated",
                "SYSTEM");

        addHistory(
                installation,
                "UPDATE_CHECKLIST",
                "Checklist updated",
                "SUCCESS",
                "SYSTEM");

        return mapChecklist(checklist);
    }
    @Override
    public List<InstallationPhotoResponseDto> getPhotos(
            Long installationId) {

        installationRepository.findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        return installationPhotoRepository
                .findByInstallationId(installationId)
                .stream()
                .map(this::mapPhoto)
                .toList();
    }
    @Override
    public InstallationPhotoResponseDto uploadPhoto(
            Long installationId,
            MultipartFile file) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));

        try {

            String uploadDir = "uploads";

            Files.createDirectories(Paths.get(uploadDir));

            String fileName =
                    UUID.randomUUID() + "_"
                            + file.getOriginalFilename();

            Path filePath =
                    Paths.get(uploadDir, fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            InstallationPhoto photo = InstallationPhoto.builder()
                    .installation(installation)
                    .fileName(fileName)
                    .fileUrl("/uploads/" + fileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            photo = installationPhotoRepository.save(photo);

            installation.getPhotos().add(photo);

            addTimeline(
                    installation,
                    "PHOTO_UPLOADED",
                    "Installation photo uploaded",
                    "SYSTEM");

            addHistory(
                    installation,
                    "UPLOAD_PHOTO",
                    "Photo uploaded",
                    "SUCCESS",
                    "SYSTEM");

            return mapPhoto(photo);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to upload photo",
                    ex);
        }
    }
    @Override
    public void deletePhoto(
            Long installationId,
            Long photoId) {

        Installation installation =
                installationRepository.findById(installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found"));

        InstallationPhoto photo =
                installationPhotoRepository.findById(photoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Photo not found"));

        try {

            Path filePath =
                    Paths.get(
                            photo.getFileUrl()
                                    .replace("/uploads/", "uploads/"));

            Files.deleteIfExists(filePath);

        } catch (Exception ignored) {
        }

        installationPhotoRepository.delete(photo);

        addTimeline(
                installation,
                "PHOTO_DELETED",
                "Installation photo deleted",
                "SYSTEM");

        addHistory(
                installation,
                "DELETE_PHOTO",
                "Photo deleted",
                "SUCCESS",
                "SYSTEM");
    }
    @Override
    public InstallationResponseDto reassignEngineer(
            Long installationId,
            ReassignInstallationEngineerRequestDto request) {

        Installation installation = installationRepository
                .findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found with id : "
                                        + installationId));
        
        if (installation.getStatus() == InstallationStatus.COMPLETED
                || installation.getStatus() == InstallationStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cannot reassign engineer for completed or cancelled installation.");
        }

        User engineer = userRepository
                .findById(request.getEngineerId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found with id : "
                                        + request.getEngineerId()));

        if (engineer.getRole() != RoleType.SERVICE_ENGINEER) {
            throw new RuntimeException(
                    "Selected user is not a Service Engineer");
        }
        
        if (engineer.getAttendanceStatus()
                != EngineerAttendanceStatus.PRESENT) {

            throw new RuntimeException(
                    "Engineer is not present.");
        }

        if (engineer.getAvailabilityStatus()
                != EngineerAvailabilityStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Engineer is currently unavailable.");
        }

        installation.setAssignedEngineer(engineer);

        installation = installationRepository.save(installation);
        
        installation.setAssignedBy("SYSTEM");

        installation.setAssignedAt(LocalDateTime.now());

        installation.setAssignmentRetryCount(
                installation.getAssignmentRetryCount() + 1);

        installation.setLastAssignmentAttempt(
                LocalDateTime.now());

        installation.setAssignmentStatus(
                AssignmentStatus.RETRYING);

        installation.setStatus(
                InstallationStatus.REASSIGNED);

        InstallationAssignmentAttempt attempt =
                InstallationAssignmentAttempt.builder()
                        .installation(installation)
                        .engineer(engineer)
                        .successful(true)
                        .installationStatus(
                                installation.getStatus())
                        .assignedBy("SYSTEM")
                        .failureReason(request.getRemarks())
                        .build();

        installationAssignmentAttemptRepository.save(attempt);

        addTimeline(
                installation,
                "ENGINEER_REASSIGNED",
                request.getRemarks(),
                engineer.getFirstName()
                        + " "
                        + engineer.getLastName());

        addHistory(
                installation,
                "REASSIGN_ENGINEER",
                "Engineer reassigned successfully",
                "SUCCESS",
                "SYSTEM");
        
        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Engineer Reassigned",
                request.getRemarks(),
                "ENGINEER_REASSIGNED",
                "SYSTEM");

        return mapToResponse(
                installationRepository.save(
                        installation));
    }
    @Override
    public InstallationResponseDto markAssignmentFailed(
            Long installationId,
            InstallationAssignmentFailureRequestDto request) {

        Installation installation =
                installationRepository.findById(
                        installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found"));

        InstallationAssignmentAttempt attempt =
                InstallationAssignmentAttempt.builder()
                        .installation(installation)
                        .engineer(
                                installation.getAssignedEngineer())
                        .successful(false)
                        .installationStatus(
                                installation.getStatus())
                        .assignedBy("SYSTEM")
                        .failureReason(
                                request.getFailureReason())
                        .build();

        installationAssignmentAttemptRepository
                .save(attempt);
        
        installation.setAssignmentRetryCount(
                installation.getAssignmentRetryCount() + 1);

        installation.setLastAssignmentAttempt(
                LocalDateTime.now());

        installation.setAssignmentStatus(
                AssignmentStatus.FAILED);

        installation.setStatus(
                InstallationStatus.ASSIGNMENT_FAILED);

        installationRepository.save(
                installation);

        addTimeline(
                installation,
                "ASSIGNMENT_FAILED",
                request.getFailureReason(),
                installation.getAssignedEngineer() != null
                        ? installation.getAssignedEngineer()
                                .getFirstName()
                        : "SYSTEM");

        addHistory(
                installation,
                "ASSIGNMENT_FAILED",
                request.getFailureReason(),
                "FAILED",
                "SYSTEM");
        
        installationNotificationService
        .sendInstallationNotification(
                installation.getId(),
                installation.getInstallationNumber(),
                "Assignment Failed",
                request.getFailureReason(),
                "ASSIGNMENT_FAILED",
                "SYSTEM");

        return mapToResponse(installation);
    }
    @Override
    public List<InstallationAssignmentAttemptResponseDto>
    getAssignmentAttempts(
            Long installationId) {

        return installationAssignmentAttemptRepository
                .findByInstallationIdOrderByAttemptedAtDesc(
                        installationId)
                .stream()
                .map(this::mapAssignmentAttempt)
                .toList();
    }
    @Override
    public List<InstallationResponseDto> getEngineerAssignments(
            Long engineerId) {

        return installationRepository
                .findByAssignedEngineerId(engineerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public InstallationAttachmentResponseDto uploadAttachment(
            Long installationId,
            MultipartFile file,
            UploadInstallationAttachmentRequestDto request) {

        Installation installation =
                installationRepository.findById(installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found."));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Attachment file is required.");
        }

        InstallationAttachment attachment =
                InstallationAttachment.builder()
                        .installation(installation)
                        .fileName(file.getOriginalFilename())
                        .originalFileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .attachmentType(request.getAttachmentType())
                        .uploadedBy("SYSTEM")
                        .filePath(file.getOriginalFilename())
                        .fileUrl(file.getOriginalFilename())
                        .build();

        attachment =
                installationAttachmentRepository.save(
                        attachment);

        installation.getAttachments().add(attachment);

        addTimeline(
                installation,
                "OTHER",
                "Attachment uploaded : "
                        + file.getOriginalFilename(),
                "SYSTEM");

        addHistory(
                installation,
                "UPLOAD_ATTACHMENT",
                "Attachment uploaded",
                "SUCCESS",
                "SYSTEM");

        installationNotificationService
                .sendInstallationNotification(
                        installation.getId(),
                        installation.getInstallationNumber(),
                        "Attachment Uploaded",
                        file.getOriginalFilename(),
                        "ATTACHMENT_UPLOADED",
                        "SYSTEM");

        return mapAttachment(attachment);
    }
    @Override
    public void deleteAttachment(
            Long installationId,
            Long attachmentId) {

        Installation installation =
                installationRepository.findById(
                        installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found."));

        InstallationAttachment attachment =
                installationAttachmentRepository
                        .findById(attachmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attachment not found."));

        if (!attachment.getInstallation()
                .getId()
                .equals(installationId)) {

            throw new RuntimeException(
                    "Attachment does not belong to this installation.");
        }

        installationAttachmentRepository
                .delete(attachment);

        installation.getAttachments()
                .remove(attachment);

        addTimeline(
                installation,
                "OTHER",
                "Attachment deleted",
                "SYSTEM");

        addHistory(
                installation,
                "DELETE_ATTACHMENT",
                "Attachment deleted",
                "SUCCESS",
                "SYSTEM");

        installationNotificationService
                .sendInstallationNotification(
                        installation.getId(),
                        installation.getInstallationNumber(),
                        "Attachment Deleted",
                        attachment.getFileName(),
                        "ATTACHMENT_DELETED",
                        "SYSTEM");
    }
    @Override
    public List<InstallationAttachmentResponseDto> getAttachments(
            Long installationId) {

        installationRepository.findById(installationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Installation not found."));

        return installationAttachmentRepository
                .findByInstallationIdOrderByUploadedAtDesc(
                        installationId)
                .stream()
                .map(this::mapAttachment)
                .toList();
    }
    @Override
    public byte[] exportCsv() {

        try {

            List<Installation> installations =
                    installationRepository.findAll();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            CSVPrinter printer =
                    new CSVPrinter(
                            new OutputStreamWriter(
                                    out,
                                    StandardCharsets.UTF_8),
                            CSVFormat.DEFAULT.withHeader(
                                    "Installation No",
                                    "Device",
                                    "Customer",
                                    "Source",
                                    "Priority",
                                    "Status",
                                    "City",
                                    "Scheduled Date"));

            for (Installation installation : installations) {

                printer.printRecord(

                        installation.getInstallationNumber(),

                        installation.getDeviceName(),

                        installation.getCustomerName(),

                        installation.getSource(),

                        installation.getPriority(),

                        installation.getStatus(),

                        installation.getCity(),

                        installation.getScheduledDate());
            }

            printer.flush();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to export CSV",
                    ex);
        }
    }
    @Override
    public byte[] exportExcel() {

        try {

            List<Installation> installations =
                    installationRepository.findAll();

            XSSFWorkbook workbook =
                    new XSSFWorkbook();

            XSSFSheet sheet =
                    workbook.createSheet(
                            "Installations");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("Installation No");

            header.createCell(1)
                    .setCellValue("Device");

            header.createCell(2)
                    .setCellValue("Customer");

            header.createCell(3)
                    .setCellValue("Source");

            header.createCell(4)
                    .setCellValue("Priority");

            header.createCell(5)
                    .setCellValue("Status");

            header.createCell(6)
                    .setCellValue("City");

            header.createCell(7)
                    .setCellValue("Scheduled Date");

            int rowNum = 1;

            for (Installation installation : installations) {

                Row row =
                        sheet.createRow(
                                rowNum++);

                row.createCell(0)
                        .setCellValue(
                                installation.getInstallationNumber());

                row.createCell(1)
                        .setCellValue(
                                installation.getDeviceName());

                row.createCell(2)
                        .setCellValue(
                                installation.getCustomerName());

                row.createCell(3)
                        .setCellValue(
                                installation.getSource().name());

                row.createCell(4)
                        .setCellValue(
                                installation.getPriority().name());

                row.createCell(5)
                        .setCellValue(
                                installation.getStatus().name());

                row.createCell(6)
                        .setCellValue(
                                installation.getCity());

                row.createCell(7)
                        .setCellValue(
                                installation.getScheduledDate() != null
                                        ? installation.getScheduledDate().toString()
                                        : "");
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);

            workbook.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to export Excel",
                    ex);
        }
    }
    @Override
    public byte[] exportPdf() {

        try {

            List<Installation> installations =
                    installationRepository.findAll();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    out);

            document.open();

            document.add(
                    new Paragraph(
                            "AMI Installation Report"));

            document.add(
                    new Paragraph(" "));

            PdfPTable table =
                    new PdfPTable(6);

            table.addCell("Installation");

            table.addCell("Device");

            table.addCell("Customer");

            table.addCell("Source");

            table.addCell("Priority");

            table.addCell("Status");

            for (Installation installation : installations) {

                table.addCell(
                        installation.getInstallationNumber());

                table.addCell(
                        installation.getDeviceName());

                table.addCell(
                        installation.getCustomerName());

                table.addCell(
                        installation.getSource().name());

                table.addCell(
                        installation.getPriority().name());

                table.addCell(
                        installation.getStatus().name());
            }

            document.add(table);

            document.close();

            return out.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to export PDF",
                    ex);
        }
    }
    private InstallationAssignmentAttemptResponseDto
    mapAssignmentAttempt(
            InstallationAssignmentAttempt attempt) {

        return InstallationAssignmentAttemptResponseDto
                .builder()
                .id(attempt.getId())
                .installationId(
                        attempt.getInstallation().getId())
                .installationNumber(
                        attempt.getInstallation()
                                .getInstallationNumber())
                .engineerId(
                        attempt.getEngineer() != null
                                ? attempt.getEngineer().getId()
                                : null)
                .engineerName(
                        attempt.getEngineer() != null
                                ? attempt.getEngineer()
                                        .getFirstName()
                                        + " "
                                        + attempt.getEngineer()
                                                .getLastName()
                                : null)
                .successful(
                        attempt.getSuccessful())
                .failureReason(
                        attempt.getFailureReason())
                .assignedBy(
                        attempt.getAssignedBy())
                .installationStatus(
                        attempt.getInstallationStatus())
                .attemptedAt(
                        attempt.getAttemptedAt())
                .build();
    }
    private InstallationEngineerResponseDto mapEngineer(User engineer) {

        if (engineer == null) {
            return null;
        }

        return InstallationEngineerResponseDto.builder()
                .engineerId(engineer.getId())
                .engineerName(
                        engineer.getFirstName() + " " + engineer.getLastName())
                .engineerEmail(engineer.getEmail())
                .engineerPhone(engineer.getPhoneNo())
                .build();
    }
    private InstallationChecklistResponseDto mapChecklist(
            InstallationChecklist checklist) {

        if (checklist == null) {
            return null;
        }

        return InstallationChecklistResponseDto.builder()

                .id(checklist.getId())

                .meterMounted(checklist.getMeterMounted())

                .wiringCompleted(checklist.getWiringCompleted())

                .communicationVerified(checklist.getCommunicationVerified())

                .meterActivated(checklist.getMeterActivated())

                .readingVerified(checklist.getReadingVerified())

                .customerVerified(checklist.getCustomerVerified())

                .mandatory(checklist.getMandatory())

                .checkedBy(checklist.getCheckedBy())

                .checkedAt(checklist.getCheckedAt())

                .remarks(checklist.getRemarks())

                .build();
    }
    private InstallationPhotoResponseDto mapPhoto(
            InstallationPhoto photo) {

        return InstallationPhotoResponseDto.builder()

                .id(photo.getId())

                .fileName(photo.getFileName())

                .fileUrl(photo.getFileUrl())

                .contentType(photo.getContentType())

                .fileSize(photo.getFileSize())

                .uploadedBy(photo.getUploadedBy())

                .caption(photo.getCaption())

                .photoType(photo.getPhotoType())

                .mandatory(photo.getMandatory())

                .primaryPhoto(photo.getPrimaryPhoto())

                .uploadedAt(photo.getUploadedAt())

                .build();
    }
    private InstallationTimelineResponseDto mapTimeline(
            InstallationTimeline timeline) {

        return InstallationTimelineResponseDto.builder()

                .id(timeline.getId())

                .event(timeline.getEvent())

                .description(timeline.getDescription())

                .performedBy(timeline.getPerformedBy())

                .performedByRole(timeline.getPerformedByRole())

                .remarks(timeline.getRemarks())

                .eventTime(timeline.getEventTime())

                .build();
    }
    private InstallationHistoryResponseDto mapHistory(
            InstallationHistory history) {

        return InstallationHistoryResponseDto.builder()

                .id(history.getId())

                .action(history.getAction())

                .description(history.getDescription())

                .previousStatus(history.getPreviousStatus())

                .newStatus(history.getNewStatus())

                .status(history.getStatus())

                .performedBy(history.getPerformedBy())

                .performedByRole(history.getPerformedByRole())

                .remarks(history.getRemarks())

                .createdAt(history.getCreatedAt())

                .build();
    }
    private InstallationRemarkResponseDto mapRemark(
            InstallationRemark remark) {

        return InstallationRemarkResponseDto.builder()

                .id(remark.getId())

                .remark(remark.getRemark())

                .remarkType(remark.getRemarkType())

                .visibility(remark.getVisibility())

                .createdBy(remark.getCreatedBy())

                .createdAt(remark.getCreatedAt())

                .updatedBy(remark.getUpdatedBy())

                .updatedAt(remark.getUpdatedAt())

                .build();
    }
    private void addTimeline(
            Installation installation,
            String event,
            String description,
            String performedBy) {

        InstallationTimeline timeline =
                InstallationTimeline.builder()
                        .installation(installation)
                        .event(
                                InstallationTimelineEvent.valueOf(event))
                        .description(description)
                        .performedBy(performedBy)
                        .performedByRole("SYSTEM")
                        .build();

        installationTimelineRepository.save(timeline);

        installation.getTimeline().add(timeline);
    }
    private void addHistory(
            Installation installation,
            String action,
            String description,
            String status,
            String performedBy) {

        InstallationHistory history =
                InstallationHistory.builder()

                        .installation(installation)

                        .action(action)

                        .description(description)

                        .status(status)

                        .previousStatus(installation.getStatus())

                        .newStatus(installation.getStatus())

                        .performedBy(performedBy)

                        .performedByRole("SYSTEM")

                        .remarks(description)

                        .build();

        installationHistoryRepository.save(history);

        installation.getHistory().add(history);
    }
    private InstallationAttachmentResponseDto mapAttachment(
            InstallationAttachment attachment) {

        return InstallationAttachmentResponseDto.builder()

                .id(attachment.getId())

                .fileName(attachment.getFileName())

                .originalFileName(
                        attachment.getOriginalFileName())

                .fileUrl(attachment.getFileUrl())

                .contentType(
                        attachment.getContentType())

                .fileSize(
                        attachment.getFileSize())

                .attachmentType(
                        attachment.getAttachmentType())

                .uploadedBy(
                        attachment.getUploadedBy())

                .uploadedAt(
                        attachment.getUploadedAt())

                .build();
    }
    private InstallationResponseDto mapToResponse(
            Installation installation) {

        return InstallationResponseDto.builder()

                .id(installation.getId())
                .installationNumber(installation.getInstallationNumber())

                .deviceId(installation.getDeviceId())
                .deviceName(installation.getDeviceName())
                .meterNumber(installation.getMeterNumber())
                .serialNumber(installation.getSerialNumber())

                .source(installation.getSource())

                .customerId(installation.getCustomerId())
                .customerName(installation.getCustomerName())
                .customerPhone(installation.getCustomerPhone())
                .customerEmail(installation.getCustomerEmail())

                .state(installation.getState())
                .city(installation.getCity())
                .zone(installation.getZone())
                .area(installation.getArea())
                .address(installation.getAddress())

                .latitude(installation.getLatitude())
                .longitude(installation.getLongitude())

                .priority(installation.getPriority())
                .status(installation.getStatus())

                .assignmentStatus(
                        installation.getAssignmentStatus())

                .scheduledDate(installation.getScheduledDate())
                .startedAt(installation.getStartedAt())
                .completedAt(installation.getCompletedAt())

                .createdAt(installation.getCreatedAt())
                .updatedAt(installation.getUpdatedAt())

                .assignedEngineer(
                        mapEngineer(installation.getAssignedEngineer()))

                .assignedBy(
                        installation.getAssignedBy())

                .assignedAt(
                        installation.getAssignedAt())

                .assignmentRetryCount(
                        installation.getAssignmentRetryCount())

                .lastAssignmentAttempt(
                        installation.getLastAssignmentAttempt())

                .completionPercentage(
                        installation.getCompletionPercentage())

                .checklist(
                        mapChecklist(installation.getChecklist()))

                .photos(
                        installation.getPhotos()
                                .stream()
                                .map(this::mapPhoto)
                                .toList())

                .timeline(
                        installation.getTimeline()
                                .stream()
                                .map(this::mapTimeline)
                                .toList())

                .history(
                        installation.getHistory()
                                .stream()
                                .map(this::mapHistory)
                                .toList())

                .remarks(
                        installation.getRemarks()
                                .stream()
                                .map(this::mapRemark)
                                .toList())

                .build();
    }
    
}