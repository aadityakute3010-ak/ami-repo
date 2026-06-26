package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.UpdateIssueRequestDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.dto.responses.IssueSummaryResponseDto;
import com.ami.dto.responses.RejectionHistoryResponseDto;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

public interface IssueService {

    IssueResponseDto createIssue(CreateIssueRequestDto request);

    List<IssueResponseDto> getAllIssues();

    IssueResponseDto getIssueById(Long issueId);

    IssueResponseDto updateIssue(Long issueId,
                                 UpdateIssueRequestDto request);

    String deleteIssue(Long issueId);

    String assignEngineer(Long issueId,
                          Long engineerId);

    String rejectIssue(Long issueId,
                       String reason);

    
    
    String updateIssueStatus(
            Long issueId,
            IssueStatus status);
    
    String acceptIssue(Long issueId);

    IssueSummaryResponseDto getSummary();
    
    List<IssueResponseDto> getIssuesByStatus(
            IssueStatus status);
    
    List<IssueResponseDto> getIssuesByPriority(
            IssuePriority priority);
    
    List<IssueResponseDto> getIssuesByCategory(
            IssueCategory category);
    
    List<IssueResponseDto> getIssuesBySource(
            SourceType sourceType);
    
    String addComment(
            Long issueId,
            String comment);
    
    RejectionHistoryResponseDto
    getRejectionHistory(Long issueId);
    
    String addAttachment(
            Long issueId,
            String fileName);
    
    String startWork(
            Long issueId,
            String notes);

    String updateProgress(
            Long issueId,
            String progress,
            String remarks);

    String addFieldVisit(
            Long issueId,
            String latitude,
            String longitude,
            String visitNotes);

    String escalateIssue(
            Long issueId,
            String reason);

    String resolveIssue(
            Long issueId,
            String rootCause,
            String actionTaken,
            String resolutionNotes);
}