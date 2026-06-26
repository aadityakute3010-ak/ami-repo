package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.UpdateIssueRequestDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.dto.responses.IssueSummaryResponseDto;
import com.ami.dto.responses.RejectionHistoryResponseDto;
import com.ami.entity.Issue;
import com.ami.entity.User;
import com.ami.enums.AssignmentMethod;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.repository.IssueRepository;
import com.ami.repository.UserRepository;
import com.ami.service.IssueService;
import com.ami.entity.IssueProgress;
import com.ami.repository.AuditLogRepository;
import com.ami.repository.IssueProgressRepository;
import java.time.LocalDateTime;
import com.ami.entity.FieldVisit;
import com.ami.repository.FieldVisitRepository;
import com.ami.entity.IssueEscalation;
import com.ami.repository.IssueEscalationRepository;
@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueProgressRepository issueProgressRepository;
    private final FieldVisitRepository fieldVisitRepository;
    private final IssueEscalationRepository issueEscalationRepository;
    public IssueServiceImpl(
            IssueRepository issueRepository,
            UserRepository userRepository,
            IssueProgressRepository issueProgressRepository,
            FieldVisitRepository fieldVisitRepository,
            IssueEscalationRepository issueEscalationRepository){

        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueProgressRepository =
                issueProgressRepository;
        this.fieldVisitRepository =
                fieldVisitRepository;
        
        this.issueEscalationRepository =
                issueEscalationRepository;
    }
    @Override
    public IssueResponseDto createIssue(CreateIssueRequestDto request) {

        User engineer = userRepository
                .findByRole(RoleType.SERVICE_ENGINEER)
                .stream()
                .findFirst()
                .orElse(null);

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
                .meterId(request.getMeterId())
                .meterType(request.getMeterType())
                .serialNumber(request.getSerialNumber())
                .state(request.getState())
                .city(request.getCity())
                .address(request.getAddress())
                .assignedEngineer(engineer)
                .assignmentMethod(AssignmentMethod.AUTO)
                .status(IssueStatus.AUTO_ASSIGNED)
                .rejectionCount(0)
                .ticketNumber("ISS-" + System.currentTimeMillis())
                .timeline(
                        "[" + LocalDateTime.now() + "] Issue Created\n" +
                        "[" + LocalDateTime.now() + "] Auto Assigned To : " +
                        (engineer != null
                                ? engineer.getUserName()
                                : "No Engineer Available")
                )
                .build();

        issue = issueRepository.save(issue);

        return mapToResponse(issue);
    }
    @Override
    public List<IssueResponseDto> getAllIssues() {

        return issueRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IssueResponseDto getIssueById(Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        return mapToResponse(issue);
    }

    @Override
    public IssueResponseDto updateIssue(
            Long issueId,
            UpdateIssueRequestDto request) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setCategory(request.getCategory());
        issue.setPriority(request.getPriority());
        issue.setState(request.getState());
        issue.setCity(request.getCity());
        issue.setAddress(request.getAddress());

        issue = issueRepository.save(issue);

        return mapToResponse(issue);
    }

    @Override
    public String deleteIssue(Long issueId) {

        issueRepository.deleteById(issueId);

        return "Issue deleted successfully";
    }

    @Override
    public String assignEngineer(Long issueId,
                                 Long engineerId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException("Engineer not found"));

        issue.setAssignedEngineer(engineer);
        issue.setAssignmentMethod(AssignmentMethod.MANUAL);
        issue.setStatus(IssueStatus.AUTO_ASSIGNED);
        
        String timeline = issue.getTimeline();

        if (timeline == null) {
            timeline = "";
        }

        timeline += "\n[" +
                LocalDateTime.now() +
                "] Engineer Assigned : " +
                engineer.getUserName();

        issue.setTimeline(timeline);

        issueRepository.save(issue);

        return "Engineer assigned successfully";
    }

    @Override
    public String rejectIssue(Long issueId,
                              String reason) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setRejectionCount(
                issue.getRejectionCount() + 1);

        issue.setRejectionReason(reason);
        
        String rejectionHistory =
                issue.getRejectionHistory();

        if(rejectionHistory == null) {

            rejectionHistory = "";
        }

        rejectionHistory +=
                "\n[" +
                LocalDateTime.now() +
                "] " +
                reason;

        issue.setRejectionHistory(
                rejectionHistory);

        String timeline = issue.getTimeline();

        if (timeline == null) {
            timeline = "";
        }

        timeline += "\n[" +
                LocalDateTime.now() +
                "] Issue Rejected : " +
                reason;

        if(issue.getRejectionCount() >= 4) {

            issue.setStatus(IssueStatus.ESCALATED);

            timeline += "\n[" +
                    LocalDateTime.now() +
                    "] Issue Escalated";

        } else {

            issue.setStatus(IssueStatus.REASSIGNED);
        }

        issue.setTimeline(timeline);

        issueRepository.save(issue);

        return "Issue rejected";
    }

    @Override
    public String resolveIssue(
            Long issueId,
            String rootCause,
            String actionTaken,
            String resolutionNotes) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        issue.setStatus(
                IssueStatus.RESOLVED);
        issue.setResolvedAt(
                LocalDateTime.now());

        issue.setResolutionNotes(
                "Root Cause : "
                + rootCause
                + "\nAction Taken : "
                + actionTaken
                + "\nResolution Notes : "
                + resolutionNotes);
        String timeline =
                issue.getTimeline();

        if (timeline == null) {

            timeline = "";
        }

        timeline +=
                "\n[" +
                LocalDateTime.now() +
                "] Issue Resolved";

        issue.setTimeline(
                timeline);

        issueRepository.save(issue);

        return "Issue resolved successfully";
    }
       @Override
    	public IssueSummaryResponseDto getSummary() {

    	    return IssueSummaryResponseDto.builder()
    	            .total(issueRepository.count())
    	            .open(issueRepository.countByStatus(IssueStatus.OPEN))
    	            .accepted(issueRepository.countByStatus(IssueStatus.ACCEPTED))
    	            .inProgress(issueRepository.countByStatus(IssueStatus.IN_PROGRESS))
    	            .resolved(issueRepository.countByStatus(IssueStatus.RESOLVED))
    	            .closed(issueRepository.countByStatus(IssueStatus.CLOSED))
    	            .escalated(issueRepository.countByStatus(IssueStatus.ESCALATED))
    	            .build();
    	
    }

    
    @Override
    public String updateIssueStatus(
            Long issueId,
            IssueStatus status) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setStatus(status);

        String timeline = issue.getTimeline();

        if (timeline == null) {
            timeline = "";
        }

        timeline += "\n[" +
                LocalDateTime.now() +
                "] Status Changed To : " +
                status;

        issue.setTimeline(timeline);

        issueRepository.save(issue);

        return "Issue status updated successfully";
    }
    @Override
    public String acceptIssue(Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setStatus(IssueStatus.ACCEPTED);
        issue.setAcceptedAt(LocalDateTime.now());

        String timeline = issue.getTimeline();

        if (timeline == null) {
            timeline = "";
        }

        timeline += "\n[" +
                LocalDateTime.now() +
                "] Issue Accepted";

        issue.setTimeline(timeline);

        issueRepository.save(issue);

        return "Issue accepted successfully";
    }
    
    @Override
    public List<IssueResponseDto> getIssuesByStatus(
            IssueStatus status) {

        return issueRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public List<IssueResponseDto> getIssuesByPriority(
            IssuePriority priority) {

        return issueRepository.findByPriority(priority)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<IssueResponseDto> getIssuesByCategory(
            IssueCategory category) {

        return issueRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<IssueResponseDto> getIssuesBySource(
            SourceType sourceType) {

        return issueRepository.findBySourceType(sourceType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public String addComment(
            Long issueId,
            String comment) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        String existingComments =
                issue.getComments();

        if(existingComments == null) {

            existingComments = "";
        }

        existingComments +=
                "\n[" +
                LocalDateTime.now() +
                "] " +
                comment;

        issue.setComments(
                existingComments);

        issueRepository.save(issue);

        return "Comment added successfully";
    }
    @Override
    public RejectionHistoryResponseDto
    getRejectionHistory(Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        RejectionHistoryResponseDto dto =
                new RejectionHistoryResponseDto();

        dto.setIssueId(issue.getId());
        dto.setRejectionHistory(
                issue.getRejectionHistory());

        return dto;
    }
    
    @Override
    public String addAttachment(
            Long issueId,
            String fileName) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        String attachments =
                issue.getAttachments();

        if (attachments == null) {

            attachments = "";
        }

        attachments +=
                "\n[" +
                LocalDateTime.now() +
                "] " +
                fileName;

        issue.setAttachments(
                attachments);

        String timeline =
                issue.getTimeline();

        if (timeline == null) {

            timeline = "";
        }

        timeline +=
                "\n[" +
                LocalDateTime.now() +
                "] Attachment Added : " +
                fileName;

        issue.setTimeline(
                timeline);

        issueRepository.save(issue);

        return "Attachment added successfully";
    }
    
    private IssueResponseDto mapToResponse(Issue issue) {

    	return IssueResponseDto.builder()
    	        .id(issue.getId())
    	        .ticketNumber(issue.getTicketNumber())
    	        .title(issue.getTitle())
    	        .description(issue.getDescription())
    	        .priority(issue.getPriority())
    	        .status(issue.getStatus())
    	        .customerName(issue.getCustomerName())
    	        .assignedEngineer(
    	                issue.getAssignedEngineer() != null
    	                        ? issue.getAssignedEngineer().getUserName()
    	                        : null)
    	        .rejectionCount(issue.getRejectionCount())
    	        .comments(issue.getComments())
    	        .timeline(issue.getTimeline())
    	        .attachments(issue.getAttachments())
    	        .rejectionHistory(issue.getRejectionHistory())
    	        .createdAt(issue.getCreatedAt())
    	        .build();
    }
    @Override
    public String startWork(
            Long issueId,
            String notes) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        issue.setStatus(
                IssueStatus.IN_PROGRESS);
        String timeline =
                issue.getTimeline();

        if (timeline == null) {

            timeline = "";
        }

        timeline +=
                "\n[" +
                LocalDateTime.now() +
                "] Work Started : " +
                notes;

        issue.setTimeline(
                timeline);

        issueRepository.save(issue);

        return "Work started successfully";
    }
    @Override
    public String updateProgress(
            Long issueId,
            String progress,
            String remarks) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        IssueProgress issueProgress =
                IssueProgress.builder()
                        .issueId(issueId)
                        .progress(progress)
                        .remarks(remarks)
                        .createdAt(
                                LocalDateTime.now())
                        .build();

        issueProgressRepository.save(
                issueProgress);

        return "Progress updated successfully";
    }
    @Override
    public String addFieldVisit(
            Long issueId,
            String latitude,
            String longitude,
            String visitNotes) {

        issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        FieldVisit visit =
                FieldVisit.builder()
                        .issueId(issueId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .visitNotes(visitNotes)
                        .visitedAt(
                                LocalDateTime.now())
                        .build();

        fieldVisitRepository.save(
                visit);

        return "Field visit recorded successfully";
    }
    @Override
    public String escalateIssue(
            Long issueId,
            String reason) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        issue.setStatus(
                IssueStatus.ESCALATED);

        issueRepository.save(issue);

        IssueEscalation escalation =
                IssueEscalation.builder()
                        .issueId(issueId)
                        .reason(reason)
                        .escalatedAt(
                                LocalDateTime.now())
                        .build();

        issueEscalationRepository.save(
                escalation);

        return "Issue escalated successfully";
    }
}