package com.ami.service.impl;
import com.ami.service.AuditService;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.ami.service.NotificationService;
import java.util.stream.Collectors;
import java.io.OutputStreamWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.io.ByteArrayOutputStream;
import com.ami.dto.responses.PageResponseDto;
import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.FieldVisitRequestDto;
import com.ami.dto.requests.MaterialRequestDto;
import com.ami.dto.requests.ProgressUpdateRequestDto;
import com.ami.dto.requests.RejectIssueRequestDto;
import com.ami.dto.requests.ResolveIssueRequestDto;
import com.ami.dto.requests.UpdateIssueRequestDto;
import com.ami.dto.responses.AssignedEngineerResponseDto;
import com.ami.dto.responses.EngineerPerformanceResponseDto;
import com.ami.dto.responses.FieldVisitResponseDto;
import com.ami.dto.responses.IssueAnalyticsResponseDto;
import com.ami.dto.responses.IssueAttachmentResponseDto;
import com.ami.dto.responses.IssueCalendarResponseDto;
import com.ami.dto.responses.IssueCommentResponseDto;
import com.ami.dto.responses.IssueDashboardResponseDto;
import com.ami.dto.responses.IssueMaterialResponseDto;
import com.ami.dto.responses.IssueMySummaryResponseDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.dto.responses.IssueSlaResponseDto;
import com.ami.dto.responses.IssueSummaryResponseDto;
import com.ami.dto.responses.IssueTimelineResponseDto;
import com.ami.dto.responses.RejectionHistoryResponseDto;
import com.ami.dto.responses.ResolutionTrendResponseDto;
import com.ami.entity.Issue;
import com.ami.entity.IssueAttachment;
import com.ami.entity.IssueComment;
import com.ami.entity.User;
import com.ami.enums.AssignmentMethod;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.repository.IssueRepository;
import com.ami.repository.IssueTimelineRepository;
import com.ami.repository.UserRepository;
import com.ami.service.IssueService;
import java.nio.charset.StandardCharsets;
import com.ami.entity.IssueTimeline;
import com.ami.repository.AuditLogRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import com.ami.repository.IssueRejectionHistoryRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ami.entity.FieldVisit;
import com.ami.repository.FieldVisitRepository;
import com.ami.repository.IssueAttachmentRepository;
import com.ami.repository.IssueCommentRepository;
import com.ami.entity.IssueEscalation;
import com.ami.entity.IssueMaterial;
import com.ami.entity.IssueRejectionHistory;
import com.ami.repository.IssueEscalationRepository;
import com.ami.repository.IssueMaterialRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.ami.dto.requests.AcceptIssueRequestDto;
import com.ami.dto.requests.AddMaterialRequestDto;
import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.CommentRequestDto;
import com.ami.dto.responses.MaterialUsedResponseDto;
@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;

    private final UserRepository userRepository;

    private final IssueTimelineRepository issueTimelineRepository;

    private final IssueCommentRepository issueCommentRepository;

    private final IssueAttachmentRepository issueAttachmentRepository;

    private final IssueMaterialRepository issueMaterialRepository;

    private final FieldVisitRepository fieldVisitRepository;

    private final IssueRejectionHistoryRepository issueRejectionHistoryRepository;
    
    private final AuditService auditService;
    
    private final NotificationService notificationService;

    public IssueServiceImpl(
            IssueRepository issueRepository,
            UserRepository userRepository,
            IssueTimelineRepository issueTimelineRepository,
            IssueCommentRepository issueCommentRepository,
            IssueAttachmentRepository issueAttachmentRepository,
            IssueMaterialRepository issueMaterialRepository,
            FieldVisitRepository fieldVisitRepository,
            IssueRejectionHistoryRepository issueRejectionHistoryRepository,
            AuditService auditService,
            NotificationService notificationService) {

        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueTimelineRepository = issueTimelineRepository;
        this.issueCommentRepository = issueCommentRepository;
        this.issueAttachmentRepository = issueAttachmentRepository;
        this.issueMaterialRepository = issueMaterialRepository;
        this.fieldVisitRepository = fieldVisitRepository;
        this.issueRejectionHistoryRepository = issueRejectionHistoryRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }
    @Override
    public IssueResponseDto createIssue(
            CreateIssueRequestDto request) {

        Issue issue = Issue.builder()

                .title(request.getTitle())
                .description(request.getDescription())

                .category(request.getCategory())
                .priority(request.getPriority())
                .sourceType(request.getSourceType())

                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())

                .deviceId(request.getDeviceId())
                .meterId(request.getMeterId())
                .serialNumber(request.getSerialNumber())
                .firmwareVersion(request.getFirmwareVersion())
                .meterType(request.getMeterType())

                .state(request.getState())
                .city(request.getCity())
                .zone(request.getZone())
                .area(request.getArea())
                .address(request.getAddress())

                .latitude(request.getLatitude())
                .longitude(request.getLongitude())

                .status(IssueStatus.OPEN)

                .build();

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_CREATED",
                "Issue created successfully",
                "SYSTEM",
                "ADMIN");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "CREATE",
                "SYSTEM",
                "Issue created successfully");

        return mapToResponse(issueRepository.save(issue));
    }
    @Override
    public PageResponseDto<IssueResponseDto> getAllIssues(
            String search,
            IssueStatus status,
            IssuePriority priority,
            IssueCategory category,
            SourceType sourceType,
            String city,
            Long engineerId,
            Long customerId,
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

        Specification<Issue> specification =
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

        if (category != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("category"), category));
        }

        if (sourceType != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("sourceType"), sourceType));
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

        if (customerId != null) {
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

                                    cb.like(cb.lower(root.get("ticketNumber")), keyword),

                                    cb.like(cb.lower(root.get("title")), keyword),

                                    cb.like(cb.lower(root.get("description")), keyword),

                                    cb.like(cb.lower(root.get("customerName")), keyword),

                                    cb.like(cb.lower(root.get("customerPhone")), keyword),

                                    cb.like(cb.lower(root.get("customerEmail")), keyword),

                                    cb.like(cb.lower(root.get("meterId")), keyword),

                                    cb.like(cb.lower(root.get("serialNumber")), keyword),

                                    cb.like(cb.lower(root.get("deviceId")), keyword),

                                    cb.like(cb.lower(root.get("city")), keyword),

                                    cb.like(cb.lower(root.get("state")), keyword)
                            ));
        }
        Page<Issue> issuePage =
                issueRepository.findAll(
                        specification,
                        pageable);

        List<IssueResponseDto> issues =
                issuePage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponseDto.<IssueResponseDto>builder()
                .content(issues)
                .page(issuePage.getNumber())
                .size(issuePage.getSize())
                .totalElements(issuePage.getTotalElements())
                .totalPages(issuePage.getTotalPages())
                .last(issuePage.isLast())
                .build();
    }

    @Override
    public IssueResponseDto getIssueById(
            Long id) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        return mapToResponse(issue);
    }
    @Override
    public IssueResponseDto updateIssue(
            Long id,
            UpdateIssueRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (request.getTitle() != null)
            issue.setTitle(request.getTitle());

        if (request.getDescription() != null)
            issue.setDescription(request.getDescription());

        if (request.getCategory() != null)
            issue.setCategory(request.getCategory());

        if (request.getPriority() != null)
            issue.setPriority(request.getPriority());

        if (request.getSourceType() != null)
            issue.setSourceType(request.getSourceType());

        if (request.getCustomerId() != null)
            issue.setCustomerId(request.getCustomerId());

        if (request.getCustomerName() != null)
            issue.setCustomerName(request.getCustomerName());

        if (request.getCustomerPhone() != null)
            issue.setCustomerPhone(request.getCustomerPhone());

        if (request.getCustomerEmail() != null)
            issue.setCustomerEmail(request.getCustomerEmail());

        if (request.getDeviceId() != null)
            issue.setDeviceId(request.getDeviceId());

        if (request.getMeterId() != null)
            issue.setMeterId(request.getMeterId());

        if (request.getSerialNumber() != null)
            issue.setSerialNumber(request.getSerialNumber());

        if (request.getFirmwareVersion() != null)
            issue.setFirmwareVersion(request.getFirmwareVersion());

        if (request.getMeterType() != null)
            issue.setMeterType(request.getMeterType());

        if (request.getState() != null)
            issue.setState(request.getState());

        if (request.getCity() != null)
            issue.setCity(request.getCity());

        if (request.getZone() != null)
            issue.setZone(request.getZone());

        if (request.getArea() != null)
            issue.setArea(request.getArea());

        if (request.getAddress() != null)
            issue.setAddress(request.getAddress());

        if (request.getLatitude() != null)
            issue.setLatitude(request.getLatitude());

        if (request.getLongitude() != null)
            issue.setLongitude(request.getLongitude());

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_UPDATED",
                "Issue updated successfully",
                "SYSTEM",
                "ADMIN");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "UPDATE",
                "SYSTEM",
                "Issue updated successfully");

        return mapToResponse(issueRepository.save(issue));
    }
    @Override
    public void deleteIssue(
            Long id) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        addTimeline(
                issue,
                "ISSUE_DELETED",
                "Issue deleted successfully",
                "SYSTEM",
                "ADMIN");

        issueRepository.delete(issue);
    }
    @Override
    public IssueResponseDto assignEngineer(
            Long id,
            AssignEngineerRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        User engineer = userRepository
                .findById(request.getEngineerId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found with id : "
                                        + request.getEngineerId()));

        issue.setAssignedEngineer(engineer);

        issue.setAssignedAt(LocalDateTime.now());

        issue.setAssignedBy("SYSTEM");

        issue.setStatus(IssueStatus.AUTO_ASSIGNED);
        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_ASSIGNED",
                "Issue assigned to engineer",
                engineer.getFirstName() + " " + engineer.getLastName(),
                engineer.getRole().name());
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "ASSIGN",
                engineer.getFirstName(),
                "Issue assigned to engineer");
        
        notificationService.issueAssigned(
                issue.getId(),
                engineer.getFirstName() + " " + engineer.getLastName());

        return mapToResponse(
                issueRepository.save(issue));
    }
    @Override
    public IssueResponseDto rejectIssue(
            Long id,
            RejectIssueRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        issue.setStatus(IssueStatus.REJECTED);

        issue.setRejectionCount(
                issue.getRejectionCount() + 1);

        issue.setLastRejectReason(
                request.getReason());

        issue = issueRepository.save(issue);

        IssueRejectionHistory history =
                IssueRejectionHistory.builder()
                        .issue(issue)
                        .engineerId(
                                issue.getAssignedEngineer() != null
                                        ? issue.getAssignedEngineer().getId()
                                        : null)
                        .engineerName(
                                issue.getAssignedEngineer() != null
                                        ? issue.getAssignedEngineer().getFirstName()
                                        : "SYSTEM")
                        .reason(request.getReason())
                        .comment(request.getComment())
                        .build();

        issueRejectionHistoryRepository.save(history);
        
        String remarks = request.getReason();

        if (request.getComment() != null &&
                !request.getComment().isBlank()) {

            remarks += " | " + request.getComment();
        }

        addTimeline(
                issue,
                "ISSUE_REJECTED",
                request.getReason(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        notificationService.issueRejected(
                issue.getId(),
                request.getReason());

        if (request.getComment() != null &&
                !request.getComment().isBlank()) {

            IssueComment comment =
                    IssueComment.builder()
                            .issue(issue)
                            .comment(request.getComment())
                            .commentedBy(
                                    issue.getAssignedEngineer() != null
                                            ? issue.getAssignedEngineer().getFirstName()
                                            : "SYSTEM")
                            .role(
                                    issue.getAssignedEngineer() != null
                                            ? issue.getAssignedEngineer().getRole().name()
                                            : "SYSTEM")
                            .build();

            issueCommentRepository.save(comment);

            issue.getComments().add(comment);
        }

        return mapToResponse(
                issueRepository.save(issue));
    }
    @Override
    public IssueResponseDto resolveIssue(
            Long id,
            ResolveIssueRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (issue.getStatus() != IssueStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "Only In Progress issues can be resolved.");
        }

        issue.setStatus(IssueStatus.RESOLVED);

        issue.setResolvedAt(LocalDateTime.now());

        issue.setResolvedBy(
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM");

        issue.setRootCause(
                request.getRootCause());

        issue.setActionTaken(
                request.getActionTaken());

        issue.setResolutionNotes(
                request.getResolutionNotes());

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_RESOLVED",
                request.getResolutionNotes(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "RESOLVE",
                "ENGINEER",
                "Issue resolved");
        
        notificationService.issueResolved(
                issue.getId());

        if (request.getResolutionNotes() != null &&
                !request.getResolutionNotes().isBlank()) {

            IssueComment comment =
                    IssueComment.builder()
                            .issue(issue)
                            .comment(request.getResolutionNotes())
                            .commentedBy(
                                    issue.getAssignedEngineer() != null
                                            ? issue.getAssignedEngineer().getFirstName()
                                            : "SYSTEM")
                            .role(
                                    issue.getAssignedEngineer() != null
                                            ? issue.getAssignedEngineer().getRole().name()
                                            : "SYSTEM")
                            .build();

            issueCommentRepository.save(comment);

            issue.getComments().add(comment);
        }

        return mapToResponse(
                issueRepository.save(issue));
    }
    
    @Override
    public IssueResponseDto acceptIssue(
            Long id,
            AcceptIssueRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (issue.getStatus() != IssueStatus.AUTO_ASSIGNED) {
            throw new RuntimeException(
                    "Only assigned issues can be accepted.");
        }

        issue.setStatus(IssueStatus.ACCEPTED);

        issue.setAcceptedAt(LocalDateTime.now());
        
        issue.setAcceptedBy(
                issue.getAssignedEngineer().getFirstName()
                + " "
                + issue.getAssignedEngineer().getLastName());

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_ACCEPTED",
                "Accepted by " + issue.getAcceptedBy(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "REJECT",
                "ENGINEER",
                "Issue rejected");
        
        notificationService.issueAccepted(
                issue.getId());

        if (request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            IssueComment comment = IssueComment.builder()
                    .issue(issue)
                    .comment(request.getRemarks())
                    .commentedBy(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getFirstName()
                                    : "SYSTEM")
                    .role(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getRole().name()
                                    : "SYSTEM")
                    .build();

            issueCommentRepository.save(comment);

            issue.getComments().add(comment);
        }

        return mapToResponse(
                issueRepository.save(issue));
    }
    @Override
    public IssueResponseDto closeIssue(
            Long id) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (issue.getStatus() != IssueStatus.RESOLVED) {
            throw new RuntimeException(
                    "Only Resolved issues can be closed.");
        }

        issue.setStatus(IssueStatus.CLOSED);

        issue.setClosedAt(LocalDateTime.now());

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_CLOSED",
                "Issue closed successfully",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "CLOSE",
                "ADMIN",
                "Issue closed");
        
        notificationService.issueClosed(
                issue.getId());

        return mapToResponse(
                issueRepository.save(issue));
    }
    @Override
    public IssueCommentResponseDto addComment(
            Long issueId,
            CommentRequestDto request) {

        Issue issue = issueRepository
                .findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        IssueComment comment = IssueComment.builder()
                .issue(issue)
                .comment(request.getComment())
                .commentedBy(
                        issue.getAssignedEngineer() != null
                                ? issue.getAssignedEngineer().getFirstName()
                                : "SYSTEM")
                .role(
                        issue.getAssignedEngineer() != null
                                ? issue.getAssignedEngineer().getRole().name()
                                : "SYSTEM")
                .build();

        comment = issueCommentRepository.save(comment);

        issue.getComments().add(comment);

        addTimeline(
                issue,
                "COMMENT_ADDED",
                request.getComment(),
                comment.getCommentedBy(),
                comment.getRole());
        
        addAuditLog(
                "ISSUE",
                issueId,
                "COMMENT",
                "USER",
                "Comment added");

        return mapComment(comment);
    }
    @Override
    public void deleteComment(
            Long issueId,
            Long commentId) {

        IssueComment comment = issueCommentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Comment not found with id : " + commentId));

        if (!comment.getIssue().getId().equals(issueId)) {
            throw new RuntimeException(
                    "Comment does not belong to this issue.");
        }

        issueCommentRepository.delete(comment);
    }
   
    @Override
    public void deleteAttachment(
            Long issueId,
            Long attachmentId) {

        IssueAttachment attachment =
                issueAttachmentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attachment not found with id : "
                                                + attachmentId));

        if (!attachment.getIssue().getId().equals(issueId)) {
            throw new RuntimeException(
                    "Attachment does not belong to this issue.");
        }

        issueAttachmentRepository.delete(attachment);
    }
   
    @Override
    public IssueResponseDto startWork(
            Long id,
            ProgressUpdateRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (issue.getStatus() != IssueStatus.ACCEPTED) {
            throw new RuntimeException(
                    "Only accepted issues can be started.");
        }

        issue.setStatus(IssueStatus.IN_PROGRESS);

        issue.setStartedAt(LocalDateTime.now());

        issue = issueRepository.save(issue);

        String remarks =
                request != null &&
                request.getRemarks() != null &&
                !request.getRemarks().isBlank()
                        ? request.getRemarks()
                        : "Engineer started working on issue";

        addTimeline(
                issue,
                "WORK_STARTED",
                remarks,
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "START_WORK",
                "ENGINEER",
                "Work started");
        
        notificationService.workStarted(
                issue.getId());
        
        if (request != null &&
                request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            IssueComment comment = IssueComment.builder()
                    .issue(issue)
                    .comment(request.getRemarks())
                    .commentedBy(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getFirstName()
                                    : "SYSTEM")
                    .role(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getRole().name()
                                    : "SYSTEM")
                    .build();

            issueCommentRepository.save(comment);

            issue.getComments().add(comment);
        }

        return mapToResponse(
                issueRepository.save(issue));
    }
    @Override
    public IssueResponseDto updateProgress(
            Long id,
            ProgressUpdateRequestDto request) {

        Issue issue = issueRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + id));

        if (issue.getStatus() != IssueStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "Only In Progress issues can be updated.");
        }

        issue.setProgressPercentage(
                request.getProgressPercentage());

        issue.setCurrentWork(
                request.getCurrentWork());

        issue.setEstimatedCompletion(
                request.getEstimatedCompletion());

        issue.setUpdatedBy(
                request.getUpdatedBy());

        addTimeline(
                issue,
                "PROGRESS_UPDATED",
                request.getRemarks(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");

        addAuditLog(
                "ISSUE",
                issue.getId(),
                "PROGRESS_UPDATE",
                "ENGINEER",
                "Progress updated to "
                        + request.getProgressPercentage()
                        + "%");
        
        notificationService.progressUpdated(
                issue.getId(),
                request.getProgressPercentage());

        if (request.getRemarks() != null &&
                !request.getRemarks().isBlank()) {

            IssueComment comment = IssueComment.builder()
                    .issue(issue)
                    .comment(request.getRemarks())
                    .commentedBy(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getFirstName()
                                    : "SYSTEM")
                    .role(
                            issue.getAssignedEngineer() != null
                                    ? issue.getAssignedEngineer().getRole().name()
                                    : "SYSTEM")
                    .build();

            issueCommentRepository.save(comment);

            issue.getComments().add(comment);
        }

        issue = issueRepository.save(issue);

        return mapToResponse(issue);
    }
    @Override
    public IssueResponseDto escalateIssue(
            Long issueId,
            String reason) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setStatus(IssueStatus.ESCALATED);
        issue.setEscalated(true);
        issue.setEscalatedBy("SYSTEM");
        issue.setEscalatedAt(LocalDateTime.now());
        issue.setEscalationReason(reason);

        issueRepository.save(issue);

        addTimeline(
                issue,
                "ISSUE_ESCALATED",
                reason,
                "SYSTEM",
                "ADMIN");
        
        addAuditLog(
                "ISSUE",
                issue.getId(),
                "ESCALATE",
                "ADMIN",
                "Issue escalated");

        return mapToResponse(issue);
    }
    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<IssueResponseDto> getAssignedIssues(

            Long engineerId,

            String search,

            IssueStatus status,

            IssuePriority priority,

            Integer page,

            Integer size,

            String sort) {

        Pageable pageable = PageRequest.of(

                page,

                size,

                Sort.by(sort));

        Page<Issue> issuePage = issueRepository.findAssignedIssues(

                engineerId,

                status,

                priority,

                search,

                pageable);

        List<IssueResponseDto> content = issuePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponseDto.<IssueResponseDto>builder()

                .content(content)

                .page(issuePage.getNumber())

                .size(issuePage.getSize())

                .totalElements(issuePage.getTotalElements())

                .totalPages(issuePage.getTotalPages())

                .last(issuePage.isLast())

                .build();
    }
    @Override
    public IssueResponseDto markSlaBreach(
            Long issueId,
            String reason) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException("Issue not found"));

        issue.setSlaBreached(true);
        issue.setSlaBreachReason(reason);

        issue = issueRepository.save(issue);

        addTimeline(
                issue,
                "SLA_BREACHED",
                reason,
                "SYSTEM",
                "SYSTEM");
        
        notificationService.slaBreached(
                issue.getId());

        return mapToResponse(issue);
    }
   
    @Override
    public IssueAttachmentResponseDto uploadAttachment(
            Long issueId,
            MultipartFile file) {

        Issue issue = issueRepository
                .findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

       
        String fileUrl = "/uploads/" + file.getOriginalFilename();

        IssueAttachment attachment = IssueAttachment.builder()
                .issue(issue)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(
                        issue.getAssignedEngineer() != null
                                ? issue.getAssignedEngineer().getFirstName()
                                : "SYSTEM")
                .build();

        attachment = issueAttachmentRepository.save(attachment);

        issue.getAttachments().add(attachment);

        addTimeline(
                issue,
                "ATTACHMENT_UPLOADED",
                file.getOriginalFilename(),
                attachment.getUploadedBy(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issueId,
                "ATTACHMENT",
                "USER",
                "Attachment uploaded");

        return mapAttachment(attachment);
    }
    @Override
    public List<FieldVisitResponseDto> getFieldVisits(
            Long issueId) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        return fieldVisitRepository
                .findByIssueId(issueId)
                .stream()
                .map(this::mapFieldVisit)
                .toList();
    }
    @Override
    public FieldVisitResponseDto createFieldVisit(
            Long issueId,
            FieldVisitRequestDto request) {

        Issue issue = issueRepository
                .findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        FieldVisit visit = FieldVisit.builder()
                .issue(issue)
                .engineerId(
                        issue.getAssignedEngineer() != null
                                ? issue.getAssignedEngineer().getId()
                                : null)
                .engineerName(
                        issue.getAssignedEngineer() != null
                                ? issue.getAssignedEngineer().getFirstName()
                                : "SYSTEM")
                .visitDate(request.getVisitDate())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .observation(request.getObservation())
                .photoUrl(request.getPhotoUrl())
                .status(request.getStatus())
                .build();

        visit = fieldVisitRepository.save(visit);

        issue.getFieldVisits().add(visit);

        addTimeline(
                issue,
                "FIELD_VISIT_CREATED",
                "Field visit created",
                visit.getEngineerName(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issueId,
                "FIELD_VISIT",
                "ENGINEER",
                "Field visit created");
        
        notificationService.fieldVisitCreated(
                issue.getId());

        return mapFieldVisit(visit);
    }
    @Override
    public FieldVisitResponseDto updateFieldVisit(
            Long visitId,
            FieldVisitRequestDto request) {

        FieldVisit visit = fieldVisitRepository
                .findById(visitId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Field Visit not found with id : " + visitId));

        visit.setVisitDate(request.getVisitDate());
        visit.setCheckIn(request.getCheckIn());
        visit.setCheckOut(request.getCheckOut());
        visit.setLatitude(request.getLatitude());
        visit.setLongitude(request.getLongitude());
        visit.setObservation(request.getObservation());
        visit.setPhotoUrl(request.getPhotoUrl());
        visit.setStatus(request.getStatus());

        visit = fieldVisitRepository.save(visit);

        addTimeline(
                visit.getIssue(),
                "FIELD_VISIT_UPDATED",
                "Field visit updated",
                visit.getEngineerName(),
                visit.getIssue().getAssignedEngineer() != null
                        ? visit.getIssue().getAssignedEngineer().getRole().name()
                        : "SYSTEM");

        return mapFieldVisit(visit);
    }
    @Override
    public IssueMaterialResponseDto addMaterial(
            Long issueId,
            MaterialRequestDto request) {

        Issue issue = issueRepository
                .findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        IssueMaterial material = IssueMaterial.builder()
                .issue(issue)
                .materialName(request.getMaterialName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .cost(request.getCost())
                .remarks(request.getRemarks())
                .build();

        material = issueMaterialRepository.save(material);

        issue.getMaterials().add(material);

        addTimeline(
                issue,
                "MATERIAL_ADDED",
                request.getMaterialName(),
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getFirstName()
                        : "SYSTEM",
                issue.getAssignedEngineer() != null
                        ? issue.getAssignedEngineer().getRole().name()
                        : "SYSTEM");
        
        addAuditLog(
                "ISSUE",
                issueId,
                "MATERIAL",
                "ENGINEER",
                "Material added");
        
        notificationService.materialAdded(
                issue.getId(),
                request.getMaterialName());

        return mapMaterial(material);
    }
    @Override
    public List<IssueMaterialResponseDto> getMaterials(
            Long issueId) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        return issueMaterialRepository
                .findByIssueId(issueId)
                .stream()
                .map(this::mapMaterial)
                .toList();
    }
    @Override
    public void deleteMaterial(
            Long issueId,
            Long materialId) {

        IssueMaterial material =
                issueMaterialRepository.findById(materialId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Material not found with id : "
                                                + materialId));

        if (!material.getIssue().getId().equals(issueId)) {
            throw new RuntimeException(
                    "Material does not belong to this issue.");
        }

        issueMaterialRepository.delete(material);
    }
    @Override
    public IssueDashboardResponseDto getDashboard() {

        return IssueDashboardResponseDto.builder()

                .total(issueRepository.count())

                .open(
                        issueRepository.countByStatus(
                                IssueStatus.OPEN))

                .assigned(
                        issueRepository.countByStatus(
                                IssueStatus.AUTO_ASSIGNED))

                .accepted(
                        issueRepository.countByStatus(
                                IssueStatus.ACCEPTED))

                .inProgress(
                        issueRepository.countByStatus(
                                IssueStatus.IN_PROGRESS))

                .resolved(
                        issueRepository.countByStatus(
                                IssueStatus.RESOLVED))

                .closed(
                        issueRepository.countByStatus(
                                IssueStatus.CLOSED))

                .rejected(
                        issueRepository.countByStatus(
                                IssueStatus.REJECTED))

                .escalated(
                        issueRepository.countByEscalatedTrue())

                .critical(
                        issueRepository.countByPriority(
                                IssuePriority.CRITICAL))

                .build();
    }
    
    @Override
    public IssueMySummaryResponseDto
    getMySummary(
            Long engineerId) {

        return IssueMySummaryResponseDto
                .builder()
                .assigned(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.AUTO_ASSIGNED))
                .accepted(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.ACCEPTED))
                .inProgress(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.IN_PROGRESS))
                .resolved(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.RESOLVED))
                .rejected(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.REJECTED))
                .escalated(
                        issueRepository
                                .countByAssignedEngineerIdAndStatus(
                                        engineerId,
                                        IssueStatus.ESCALATED))
                .build();
    }
   
    
    
    @Override
    public List<EngineerPerformanceResponseDto>
    getEngineerPerformance() {

        return userRepository
                .findByRole(RoleType.SERVICE_ENGINEER)
                .stream()
                .map(engineer ->
                        EngineerPerformanceResponseDto
                                .builder()
                                .engineerId(
                                        engineer.getId())
                                .engineerName(
                                        engineer.getFirstName()
                                                + " "
                                                + engineer.getLastName())
                                .assignedIssues(
                                        issueRepository.countByAssignedEngineerId(
                                                engineer.getId()))
                                .resolvedIssues(
                                        issueRepository.countByAssignedEngineerIdAndStatus(
                                                engineer.getId(),
                                                IssueStatus.RESOLVED))
                                .rejectedIssues(
                                        issueRepository.countByAssignedEngineerIdAndStatus(
                                                engineer.getId(),
                                                IssueStatus.REJECTED))
                                .escalatedIssues(
                                        issueRepository.countByAssignedEngineerIdAndStatus(
                                                engineer.getId(),
                                                IssueStatus.ESCALATED))
                                .inProgressIssues(
                                        issueRepository.countByAssignedEngineerIdAndStatus(
                                                engineer.getId(),
                                                IssueStatus.IN_PROGRESS))
                                .build())
                .toList();
    }
   
    @Override
    public List<IssueCommentResponseDto> getComments(
            Long issueId) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        return issueCommentRepository
                .findByIssueIdOrderByCreatedAtDesc(issueId)
                .stream()
                .map(this::mapComment)
                .toList();
    }
    @Override
    public List<IssueAttachmentResponseDto> getAttachments(
            Long issueId) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id : " + issueId));

        return issueAttachmentRepository
                .findByIssueId(issueId)
                .stream()
                .map(this::mapAttachment)
                .toList();
    }
    @Override
    public IssueAnalyticsResponseDto getAnalytics() {

        Long total = issueRepository.count();

        Long open = issueRepository.countByStatus(
                IssueStatus.OPEN);

        Long inProgress = issueRepository.countByStatus(
                IssueStatus.IN_PROGRESS);

        Long resolved = issueRepository.countByStatus(
                IssueStatus.RESOLVED);

        Long closed = issueRepository.countByStatus(
                IssueStatus.CLOSED);

        Long rejected = issueRepository.countByStatus(
                IssueStatus.REJECTED);

        Long escalated = issueRepository.countByEscalatedTrue();

        Long overdue = issueRepository.countBySlaBreachedTrue();

        Double resolutionRate = total == 0
                ? 0.0
                : ((resolved + closed) * 100.0)
                / total.doubleValue();

        return IssueAnalyticsResponseDto.builder()

                // ==========================
                // Overall
                // ==========================

                .totalIssues(total)

                .openIssues(open)

                .inProgressIssues(inProgress)

                .resolvedIssues(resolved)

                .closedIssues(closed)

                .rejectedIssues(rejected)

                .escalatedIssues(escalated)

                .overdueIssues(overdue)

                .resolutionRate(resolutionRate)

                .monthlyTrend(List.of())

                // ==========================
                // Priority
                // ==========================

                .lowPriority(
                        issueRepository.countByPriority(
                                IssuePriority.LOW))

                .mediumPriority(
                        issueRepository.countByPriority(
                                IssuePriority.MEDIUM))

                .highPriority(
                        issueRepository.countByPriority(
                                IssuePriority.HIGH))

                .criticalPriority(
                        issueRepository.countByPriority(
                                IssuePriority.CRITICAL))

                // ==========================
                // Category
                // ==========================

                .meterIssues(
                        issueRepository.countByCategory(
                                IssueCategory.METER))

                .networkIssues(
                        issueRepository.countByCategory(
                                IssueCategory.NETWORK))

                .batteryIssues(
                        issueRepository.countByCategory(
                                IssueCategory.BATTERY))

                .tamperIssues(
                        issueRepository.countByCategory(
                                IssueCategory.TAMPER))

                .valveIssues(
                        issueRepository.countByCategory(
                                IssueCategory.VALVE))

                .communicationIssues(
                        issueRepository.countByCategory(
                                IssueCategory.COMMUNICATION))

                .billingIssues(
                        issueRepository.countByCategory(
                                IssueCategory.BILLING))

                .leakageIssues(
                        issueRepository.countByCategory(
                                IssueCategory.LEAKAGE))

                .systemIssues(
                        issueRepository.countByCategory(
                                IssueCategory.SYSTEM))

                .otherIssues(
                        issueRepository.countByCategory(
                                IssueCategory.OTHER))

                // ==========================
                // SLA
                // ==========================

                .slaBreached(
                        issueRepository.countBySlaBreachedTrue())

                .slaWithin(
                        issueRepository.countBySlaBreachedFalse())

                // ==========================
                // Engineers
                // ==========================

                .totalEngineers(
                        userRepository.countByRole(
                                RoleType.SERVICE_ENGINEER))

                .availableEngineers(
                        (long) userRepository.findByRoleAndAvailabilityStatus(
                                RoleType.SERVICE_ENGINEER,
                                EngineerAvailabilityStatus.AVAILABLE).size())

                .busyEngineers(
                        (long) userRepository.findByRoleAndAvailabilityStatus(
                                RoleType.SERVICE_ENGINEER,
                                EngineerAvailabilityStatus.BUSY).size())
                .activeEngineers(
                        userRepository.countByRole(
                                RoleType.SERVICE_ENGINEER))

                .build();
    }
    @Override
    public byte[] exportCsv() {

        try {

            List<Issue> issues = issueRepository.findAll();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            CSVPrinter printer =
                    new CSVPrinter(
                            new OutputStreamWriter(
                                    out,
                                    StandardCharsets.UTF_8),
                            CSVFormat.DEFAULT.withHeader(
                                    "Ticket No",
                                    "Title",
                                    "Category",
                                    "Priority",
                                    "Status",
                                    "Customer",
                                    "City"));

            for (Issue issue : issues) {

                printer.printRecord(

                        issue.getTicketNumber(),

                        issue.getTitle(),

                        issue.getCategory(),

                        issue.getPriority(),

                        issue.getStatus(),

                        issue.getCustomerName(),

                        issue.getCity());
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

            List<Issue> issues = issueRepository.findAll();

            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet("Issues");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Ticket");

            header.createCell(1).setCellValue("Title");

            header.createCell(2).setCellValue("Category");

            header.createCell(3).setCellValue("Priority");

            header.createCell(4).setCellValue("Status");

            header.createCell(5).setCellValue("Customer");

            header.createCell(6).setCellValue("City");

            int rowNum = 1;

            for (Issue issue : issues) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(issue.getTicketNumber());

                row.createCell(1)
                        .setCellValue(issue.getTitle());

                row.createCell(2)
                        .setCellValue(issue.getCategory().name());

                row.createCell(3)
                        .setCellValue(issue.getPriority().name());

                row.createCell(4)
                        .setCellValue(issue.getStatus().name());

                row.createCell(5)
                        .setCellValue(issue.getCustomerName());

                row.createCell(6)
                        .setCellValue(issue.getCity());
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

            List<Issue> issues = issueRepository.findAll();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Issue Report"));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);

            table.addCell("Ticket");

            table.addCell("Title");

            table.addCell("Category");

            table.addCell("Priority");

            table.addCell("Status");

            table.addCell("Customer");

            for (Issue issue : issues) {

                table.addCell(issue.getTicketNumber());

                table.addCell(issue.getTitle());

                table.addCell(issue.getCategory().name());

                table.addCell(issue.getPriority().name());

                table.addCell(issue.getStatus().name());

                table.addCell(issue.getCustomerName());
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
    private AssignedEngineerResponseDto mapAssignedEngineer(
            User engineer,
            LocalDateTime assignedAt) {

        if (engineer == null) {
            return null;
        }

        return AssignedEngineerResponseDto.builder()

                .engineerId(engineer.getId())

                .engineerName(
                        engineer.getFirstName() + " " + engineer.getLastName())

                .engineerEmail(engineer.getEmail())

                .engineerPhone(engineer.getPhoneNo())

                .assignedAt(assignedAt)

                .build();
    }
    private IssueTimelineResponseDto mapTimeline(
            IssueTimeline timeline) {

        return IssueTimelineResponseDto.builder()

                .id(timeline.getId())

                .action(timeline.getAction())

                .remarks(timeline.getRemarks())

                .performedBy(timeline.getPerformedBy())

                .performedByRole(timeline.getPerformedByRole())

                .createdAt(timeline.getCreatedAt())

                .build();
    }
    private IssueCommentResponseDto mapComment(
            IssueComment comment) {

        return IssueCommentResponseDto.builder()

                .id(comment.getId())

                .comment(comment.getComment())

                .commentedBy(comment.getCommentedBy())

                .role(comment.getRole())

                .createdAt(comment.getCreatedAt())

                .build();
    }
    private IssueAttachmentResponseDto mapAttachment(
            IssueAttachment attachment) {

        return IssueAttachmentResponseDto.builder()

                .id(attachment.getId())

                .fileName(attachment.getFileName())

                .fileUrl(attachment.getFileUrl())

                .fileType(attachment.getFileType())

                .fileSize(attachment.getFileSize())

                .uploadedBy(attachment.getUploadedBy())

                .uploadedAt(attachment.getUploadedAt())

                .build();
    }
    private IssueMaterialResponseDto mapMaterial(
            IssueMaterial material) {

        return IssueMaterialResponseDto.builder()

                .id(material.getId())

                .materialName(material.getMaterialName())

                .quantity(material.getQuantity())

                .unit(material.getUnit())

                .cost(material.getCost())

                .remarks(material.getRemarks())

                .build();
    }
    private FieldVisitResponseDto mapFieldVisit(
            FieldVisit visit) {

        return FieldVisitResponseDto.builder()

                .id(visit.getId())

                .engineerId(visit.getEngineerId())

                .engineerName(visit.getEngineerName())

                .visitDate(visit.getVisitDate())

                .checkIn(visit.getCheckIn())

                .checkOut(visit.getCheckOut())

                .latitude(visit.getLatitude())

                .longitude(visit.getLongitude())

                .observation(visit.getObservation())

                .photoUrl(visit.getPhotoUrl())

                .status(visit.getStatus())

                .build();
    }
    private void addTimeline(
            Issue issue,
            String action,
            String remarks,
            String performedBy,
            String performedByRole) {

        IssueTimeline timeline = IssueTimeline.builder()
                .issue(issue)
                .action(action)
                .remarks(remarks)
                .performedBy(performedBy)
                .performedByRole(performedByRole)
                .build();

        issueTimelineRepository.save(timeline);

        issue.getTimeline().add(timeline);
    }
    @Override
    public List<IssueTimelineResponseDto> getTimeline(Long issueId) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found with id : " + issueId));

        return issueTimelineRepository
                .findByIssueIdOrderByCreatedAtDesc(issueId)
                .stream()
                .map(this::mapTimeline)
                .toList();
    }
    @Override
    public IssueSlaResponseDto getSlaDetails(
            Long issueId) {

        Issue issue = issueRepository
                .findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        long elapsedHours = 0;

        if (issue.getCreatedAt() != null) {
            elapsedHours = java.time.Duration
                    .between(
                            issue.getCreatedAt(),
                            LocalDateTime.now())
                    .toHours();
        }

        long remainingHours = 0;

        if (issue.getResolutionDueAt() != null) {

            remainingHours = java.time.Duration
                    .between(
                            LocalDateTime.now(),
                            issue.getResolutionDueAt())
                    .toHours();

            if (remainingHours < 0) {
                remainingHours = 0;
            }
        }

        return IssueSlaResponseDto.builder()
                .issueId(issue.getId())
                .elapsedHours(elapsedHours)
                .remainingHours(remainingHours)
                .breached(issue.getSlaBreached())
                .build();
    }
    
    private void addAuditLog(
            String module,
            Long entityId,
            String action,
            String performedBy,
            String description) {

        CreateAuditLogRequestDto request =
                CreateAuditLogRequestDto.builder()
                        .module(module)
                        .entityId(entityId)
                        .action(action)
                        .performedBy(performedBy)
                        .description(description)
                        .build();

        auditService.createAuditLog(request);
    }
    private IssueResponseDto mapToResponse(
            Issue issue) {

        return IssueResponseDto.builder()

                .id(issue.getId())
                .ticketNumber(issue.getTicketNumber())

                .title(issue.getTitle())
                .description(issue.getDescription())

                .category(issue.getCategory())
                .priority(issue.getPriority())
                .status(issue.getStatus())

                .sourceType(issue.getSourceType())
                .assignmentMethod(issue.getAssignmentMethod())

                // Customer
                .customerId(issue.getCustomerId())
                .customerName(issue.getCustomerName())
                .customerPhone(issue.getCustomerPhone())
                .customerEmail(issue.getCustomerEmail())

                // Device
                .deviceId(issue.getDeviceId())
                .meterId(issue.getMeterId())
                .serialNumber(issue.getSerialNumber())
                .firmwareVersion(issue.getFirmwareVersion())
                .meterType(issue.getMeterType())

                // Location
                .state(issue.getState())
                .city(issue.getCity())
                .zone(issue.getZone())
                .area(issue.getArea())
                .address(issue.getAddress())

                .latitude(issue.getLatitude())
                .longitude(issue.getLongitude())

                // Engineer
                .assignedEngineer(
                        mapAssignedEngineer(
                                issue.getAssignedEngineer(),
                                issue.getAssignedAt()))

                .assignedAt(issue.getAssignedAt())
                .assignedBy(issue.getAssignedBy())

                // Workflow
                .acceptedAt(issue.getAcceptedAt())
                .startedAt(issue.getStartedAt())
                .progressPercentage(issue.getProgressPercentage())
                .currentWork(issue.getCurrentWork())
                .estimatedCompletion(issue.getEstimatedCompletion())
                .updatedBy(issue.getUpdatedBy())
                .completedAt(issue.getCompletedAt())
                .closedAt(issue.getClosedAt())

                // SLA
                .responseDueAt(issue.getResponseDueAt())
                .resolutionDueAt(issue.getResolutionDueAt())
                .slaBreached(issue.getSlaBreached())
                .slaStatus(issue.getSlaStatus())

                // Resolution
                .rootCause(issue.getRootCause())
                .actionTaken(issue.getActionTaken())
                .resolutionNotes(issue.getResolutionNotes())
                .resolvedBy(issue.getResolvedBy())
                .resolvedAt(issue.getResolvedAt())

                // Escalation
                .escalated(issue.getEscalated())
                .escalatedBy(issue.getEscalatedBy())
                .escalatedAt(issue.getEscalatedAt())
                .escalationReason(issue.getEscalationReason())

                // Rejection
                .rejectionCount(issue.getRejectionCount())
                .lastRejectReason(issue.getLastRejectReason())

                // Child Objects
                .timeline(
                        issue.getTimeline()
                                .stream()
                                .map(this::mapTimeline)
                                .toList())

                .comments(
                        issue.getComments()
                                .stream()
                                .map(this::mapComment)
                                .toList())

                .attachments(
                        issue.getAttachments()
                                .stream()
                                .map(this::mapAttachment)
                                .toList())

                .materials(
                        issue.getMaterials()
                                .stream()
                                .map(this::mapMaterial)
                                .toList())

                .fieldVisits(
                        issue.getFieldVisits()
                                .stream()
                                .map(this::mapFieldVisit)
                                .toList())

                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())

                .build();
    }
}