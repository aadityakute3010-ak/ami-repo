package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.RejectIssueRequestDto;
import com.ami.dto.requests.AcceptIssueRequestDto;
import com.ami.dto.requests.CommentRequestDto;
import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.FieldVisitRequestDto;
import com.ami.dto.requests.MaterialRequestDto;
import com.ami.dto.requests.ProgressUpdateRequestDto;
import com.ami.dto.requests.ResolveIssueRequestDto;
import com.ami.dto.requests.UpdateIssueRequestDto;
import com.ami.dto.responses.EngineerPerformanceResponseDto;
import com.ami.dto.responses.FieldVisitResponseDto;
import com.ami.dto.responses.IssueAnalyticsResponseDto;
import com.ami.dto.responses.IssueAttachmentResponseDto;
import com.ami.dto.responses.IssueCommentResponseDto;
import com.ami.dto.responses.IssueDashboardResponseDto;
import com.ami.dto.responses.IssueMaterialResponseDto;
import com.ami.dto.responses.IssueMySummaryResponseDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.dto.responses.IssueSlaResponseDto;
import com.ami.dto.responses.IssueTimelineResponseDto;
import com.ami.dto.responses.PageResponseDto;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

public interface IssueService {

    IssueResponseDto createIssue(
            CreateIssueRequestDto request);

    IssueResponseDto updateIssue(
            Long id,
            UpdateIssueRequestDto request);

    void deleteIssue(
            Long id);

    IssueResponseDto getIssueById(
            Long id);

    PageResponseDto<IssueResponseDto> getAllIssues(
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
            String sort);

    IssueResponseDto assignEngineer(
            Long id,
            AssignEngineerRequestDto request);

    IssueResponseDto acceptIssue(
            Long id,
            AcceptIssueRequestDto request);

    IssueResponseDto rejectIssue(
            Long id,
            RejectIssueRequestDto request);

    IssueResponseDto startWork(
            Long id,
            ProgressUpdateRequestDto request);

    IssueResponseDto updateProgress(
            Long id,
            ProgressUpdateRequestDto request);

    IssueResponseDto resolveIssue(
            Long id,
            ResolveIssueRequestDto request);

    IssueResponseDto closeIssue(
            Long id);
    
    

    IssueSlaResponseDto getSlaDetails(
            Long issueId);

    IssueResponseDto markSlaBreach(
            Long issueId,
            String reason);

    List<IssueTimelineResponseDto> getTimeline(
            Long issueId);

    List<IssueCommentResponseDto> getComments(
            Long issueId);

    IssueCommentResponseDto addComment(
            Long issueId,
            CommentRequestDto request);

    void deleteComment(
            Long issueId,
            Long commentId);

    List<IssueAttachmentResponseDto> getAttachments(
            Long issueId);

    IssueAttachmentResponseDto uploadAttachment(
            Long issueId,
            MultipartFile file);

    void deleteAttachment(
            Long issueId,
            Long attachmentId);

    List<IssueMaterialResponseDto> getMaterials(
            Long issueId);

    IssueMaterialResponseDto addMaterial(
            Long issueId,
            MaterialRequestDto request);

    void deleteMaterial(
            Long issueId,
            Long materialId);

    List<FieldVisitResponseDto> getFieldVisits(
            Long issueId);

    FieldVisitResponseDto createFieldVisit(
            Long issueId,
            FieldVisitRequestDto request);

    FieldVisitResponseDto updateFieldVisit(
            Long visitId,
            FieldVisitRequestDto request);

    IssueDashboardResponseDto getDashboard();

    IssueAnalyticsResponseDto getAnalytics();
    
    PageResponseDto<IssueResponseDto> getAssignedIssues(

            Long engineerId,

            String search,

            IssueStatus status,

            IssuePriority priority,

            Integer page,

            Integer size,

            String sort);
    
    IssueResponseDto escalateIssue(
            Long id,
            String reason);
    
    List<EngineerPerformanceResponseDto> getEngineerPerformance();
    
    IssueMySummaryResponseDto getMySummary(
            Long engineerId);

    byte[] exportCsv();

    byte[] exportExcel();

    byte[] exportPdf();
}