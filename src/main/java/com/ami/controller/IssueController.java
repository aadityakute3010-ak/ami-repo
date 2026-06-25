package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.AddCommentRequestDto;
import com.ami.dto.requests.AttachmentRequestDto;
import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.UpdateIssueRequestDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.dto.responses.IssueSummaryResponseDto;
import com.ami.dto.responses.RejectionHistoryResponseDto;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;
import com.ami.service.IssueService;
import com.ami.dto.requests.StartWorkRequestDto;
import com.ami.dto.requests.ProgressUpdateRequestDto;
import com.ami.dto.requests.FieldVisitRequestDto;
import com.ami.dto.requests.EscalateIssueRequestDto;
import com.ami.dto.requests.ResolveIssueRequestDto;
@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @PostMapping
    public IssueResponseDto createIssue(
            @RequestBody CreateIssueRequestDto request) {

        return issueService.createIssue(request);
    }

    @GetMapping
    public List<IssueResponseDto> getAllIssues() {
        return issueService.getAllIssues();
    }

    @GetMapping("/{issueId}")
    public IssueResponseDto getIssue(
            @PathVariable Long issueId) {

        return issueService.getIssueById(issueId);
    }
    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping("/{issueId}")
    public IssueResponseDto updateIssue(
            @PathVariable Long issueId,
            @RequestBody UpdateIssueRequestDto request) {

        return issueService.updateIssue(issueId, request);
    }
    @PreAuthorize("hasRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{issueId}")
    public String deleteIssue(
            @PathVariable Long issueId) {

        return issueService.deleteIssue(issueId);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/{issueId}/assign")
    public String assignEngineer(
            @PathVariable Long issueId,
            @RequestParam Long engineerId) {

        return issueService.assignEngineer(issueId,
                                           engineerId);
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/reject")
    public String rejectIssue(
            @PathVariable Long issueId,
            @RequestParam String reason) {

        return issueService.rejectIssue(issueId,
                                        reason);
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/resolve")
    public String resolveIssue(
            @PathVariable Long issueId,
            @RequestBody ResolveIssueRequestDto request) {

        return issueService.resolveIssue(
                issueId,
                request.getRootCause(),
                request.getActionTaken(),
                request.getResolutionNotes());
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/summary")
    public IssueSummaryResponseDto getSummary() {
        return issueService.getSummary();
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PatchMapping("/{issueId}/status")
    public String updateIssueStatus(
            @PathVariable Long issueId,
            @RequestParam IssueStatus status) {

        return issueService.updateIssueStatus(
                issueId,
                status);
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/accept")
    public String acceptIssue(
            @PathVariable Long issueId) {

        return issueService.acceptIssue(issueId);
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @GetMapping("/status/{status}")
    public List<IssueResponseDto> getIssuesByStatus(
            @PathVariable IssueStatus status) {

        return issueService.getIssuesByStatus(status);
    }
    
    @GetMapping("/priority/{priority}")
    public List<IssueResponseDto> getIssuesByPriority(
            @PathVariable IssuePriority priority) {

        return issueService.getIssuesByPriority(priority);
    }
    @GetMapping("/category/{category}")
    public List<IssueResponseDto> getIssuesByCategory(
            @PathVariable IssueCategory category) {

        return issueService.getIssuesByCategory(category);
    }
    
    @GetMapping("/source/{sourceType}")
    public List<IssueResponseDto> getIssuesBySource(
            @PathVariable SourceType sourceType) {

        return issueService.getIssuesBySource(sourceType);
    }
    
    @PostMapping("/{issueId}/comment")
    public String addComment(
            @PathVariable Long issueId,
            @RequestBody AddCommentRequestDto request) {

        return issueService.addComment(
                issueId,
                request.getComment());
    }
    
    @GetMapping("/{issueId}/rejection-history")
    public RejectionHistoryResponseDto
    getRejectionHistory(
            @PathVariable Long issueId) {

        return issueService
                .getRejectionHistory(
                        issueId);
    }
    
    @PostMapping("/{issueId}/attachment")
    public String addAttachment(
            @PathVariable Long issueId,
            @RequestBody AttachmentRequestDto request) {

        return issueService.addAttachment(
                issueId,
                request.getFileName());
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/start-work")
    public String startWork(
            @PathVariable Long issueId,
            @RequestBody StartWorkRequestDto request) {

        return issueService.startWork(
                issueId,
                request.getNotes());
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/progress")
    public String updateProgress(
            @PathVariable Long issueId,
            @RequestBody ProgressUpdateRequestDto request) {

        return issueService.updateProgress(
                issueId,
                request.getProgress(),
                request.getRemarks());
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/field-visit")
    public String addFieldVisit(
            @PathVariable Long issueId,
            @RequestBody FieldVisitRequestDto request) {

        return issueService.addFieldVisit(
                issueId,
                request.getLatitude(),
                request.getLongitude(),
                request.getVisitNotes());
    }
    @PreAuthorize("hasRole('SERVICE_ENGINEER')")
    @PostMapping("/{issueId}/escalate")
    public String escalateIssue(
            @PathVariable Long issueId,
            @RequestBody EscalateIssueRequestDto request) {

        return issueService.escalateIssue(
                issueId,
                request.getReason());
    }
}