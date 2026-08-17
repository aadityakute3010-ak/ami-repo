package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ami.dto.requests.AcceptIssueRequestDto;
import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.CommentRequestDto;
import com.ami.dto.requests.CreateIssueRequestDto;
import com.ami.dto.requests.FieldVisitRequestDto;
import com.ami.dto.requests.MaterialRequestDto;
import com.ami.dto.requests.ProgressUpdateRequestDto;
import com.ami.dto.requests.RejectIssueRequestDto;
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

import com.ami.service.IssueService;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }
    
 // ==========================
 // CREATE ISSUE
 // ==========================

 @PostMapping
 public IssueResponseDto createIssue(
         @RequestBody CreateIssueRequestDto request) {

     return issueService.createIssue(request);
 }

 // ==========================
 // GET ALL ISSUES
 // ==========================

 @GetMapping
 public PageResponseDto<IssueResponseDto> getAllIssues(

         @RequestParam(defaultValue = "") String search,

         @RequestParam(required = false)
         IssueStatus status,

         @RequestParam(required = false)
         IssuePriority priority,

         @RequestParam(required = false)
         IssueCategory category,

         @RequestParam(required = false)
         SourceType sourceType,

         @RequestParam(required = false)
         String city,

         @RequestParam(required = false)
         Long engineerId,

         @RequestParam(required = false)
         Long customerId,

         @RequestParam(required = false)
         LocalDateTime fromDate,

         @RequestParam(required = false)
         LocalDateTime toDate,

         @RequestParam(defaultValue = "0")
         Integer page,

         @RequestParam(defaultValue = "10")
         Integer size,

         @RequestParam(defaultValue = "createdAt,desc")
         String sort) {

     return issueService.getAllIssues(

             search,
             status,
             priority,
             category,
             sourceType,
             city,
             engineerId,
             customerId,
             fromDate,
             toDate,
             page,
             size,
             sort);
 }

 // ==========================
 // GET ISSUE BY ID
 // ==========================

 @GetMapping("/{id}")
 public IssueResponseDto getIssueById(
         @PathVariable Long id) {

     return issueService.getIssueById(id);
 }

 // ==========================
 // UPDATE ISSUE
 // ==========================

 @PutMapping("/{id}")
 public IssueResponseDto updateIssue(
         @PathVariable Long id,
         @RequestBody UpdateIssueRequestDto request) {

     return issueService.updateIssue(id, request);
 }

 // ==========================
 // DELETE ISSUE
 // ==========================

 @DeleteMapping("/{id}")
 public void deleteIssue(
         @PathVariable Long id) {

     issueService.deleteIssue(id);
 }
//=====================================================
//ASSIGN ENGINEER
//=====================================================

@PutMapping("/{id}/assign")
public IssueResponseDto assignEngineer(
      @PathVariable Long id,
      @RequestBody AssignEngineerRequestDto request) {

  return issueService.assignEngineer(id, request);
}

//=====================================================
//ACCEPT ISSUE
//=====================================================

@PutMapping("/{id}/accept")
public IssueResponseDto acceptIssue(
      @PathVariable Long id,
      @RequestBody AcceptIssueRequestDto request) {

  return issueService.acceptIssue(id, request);
}

//=====================================================
//REJECT ISSUE
//=====================================================

@PutMapping("/{id}/reject")
public IssueResponseDto rejectIssue(
      @PathVariable Long id,
      @RequestBody RejectIssueRequestDto request) {

  return issueService.rejectIssue(id, request);
}

//=====================================================
//START WORK
//=====================================================

@PutMapping("/{id}/start-work")
public IssueResponseDto startWork(
        @PathVariable Long id,
        @RequestBody(required = false)
        ProgressUpdateRequestDto request) {

    return issueService.startWork(
            id,
            request);
}

//=====================================================
//UPDATE PROGRESS
//=====================================================

@PutMapping("/{id}/progress")
public IssueResponseDto updateProgress(
      @PathVariable Long id,
      @RequestBody ProgressUpdateRequestDto request) {

  return issueService.updateProgress(id, request);
}

//=====================================================
//RESOLVE ISSUE
//=====================================================

@PutMapping("/{id}/resolve")
public IssueResponseDto resolveIssue(
      @PathVariable Long id,
      @RequestBody ResolveIssueRequestDto request) {

  return issueService.resolveIssue(id, request);
}

//=====================================================
//CLOSE ISSUE
//=====================================================

@PutMapping("/{id}/close")
public IssueResponseDto closeIssue(
      @PathVariable Long id) {

  return issueService.closeIssue(id);
}
//=====================================================
//TIMELINE
//=====================================================

@GetMapping("/{id}/timeline")
public List<IssueTimelineResponseDto> getTimeline(
     @PathVariable Long id) {

 return issueService.getTimeline(id);
}

//=====================================================
//COMMENTS
//=====================================================

@GetMapping("/{id}/comments")
public List<IssueCommentResponseDto> getComments(
     @PathVariable Long id) {

 return issueService.getComments(id);
}

@PostMapping("/{id}/comments")
public IssueCommentResponseDto addComment(
     @PathVariable Long id,
     @RequestBody CommentRequestDto request) {

 return issueService.addComment(id, request);
}

@DeleteMapping("/{issueId}/comments/{commentId}")
public void deleteComment(
     @PathVariable Long issueId,
     @PathVariable Long commentId) {

 issueService.deleteComment(issueId, commentId);
}

//=====================================================
//ATTACHMENTS
//=====================================================

@GetMapping("/{id}/attachments")
public List<IssueAttachmentResponseDto> getAttachments(
     @PathVariable Long id) {

 return issueService.getAttachments(id);
}

@PostMapping(
     value = "/{id}/attachments",
     consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public IssueAttachmentResponseDto uploadAttachment(
     @PathVariable Long id,
     @RequestParam("file") MultipartFile file) {

 return issueService.uploadAttachment(id, file);
}

@DeleteMapping("/{issueId}/attachments/{attachmentId}")
public void deleteAttachment(
     @PathVariable Long issueId,
     @PathVariable Long attachmentId) {

 issueService.deleteAttachment(issueId, attachmentId);
}

//=====================================================
//MATERIALS
//=====================================================

@GetMapping("/{id}/materials")
public List<IssueMaterialResponseDto> getMaterials(
     @PathVariable Long id) {

 return issueService.getMaterials(id);
}

@PostMapping("/{id}/materials")
public IssueMaterialResponseDto addMaterial(
     @PathVariable Long id,
     @RequestBody MaterialRequestDto request) {

 return issueService.addMaterial(id, request);
}

@DeleteMapping("/{issueId}/materials/{materialId}")
public void deleteMaterial(
     @PathVariable Long issueId,
     @PathVariable Long materialId) {

 issueService.deleteMaterial(issueId, materialId);
}

//=====================================================
//FIELD VISITS
//=====================================================

@GetMapping("/{id}/field-visits")
public List<FieldVisitResponseDto> getFieldVisits(
     @PathVariable Long id) {

 return issueService.getFieldVisits(id);
}

@PostMapping("/{id}/field-visits")
public FieldVisitResponseDto createFieldVisit(
     @PathVariable Long id,
     @RequestBody FieldVisitRequestDto request) {

 return issueService.createFieldVisit(id, request);
}

@PutMapping("/field-visits/{visitId}")
public FieldVisitResponseDto updateFieldVisit(
     @PathVariable Long visitId,
     @RequestBody FieldVisitRequestDto request) {

 return issueService.updateFieldVisit(visitId, request);
}
//=====================================================
//ADMIN DASHBOARD
//=====================================================

@GetMapping("/dashboard")
public IssueDashboardResponseDto getDashboard() {

 return issueService.getDashboard();
}

//=====================================================
//ANALYTICS
//=====================================================

@GetMapping("/analytics")
public IssueAnalyticsResponseDto getAnalytics() {

 return issueService.getAnalytics();
}

//=====================================================
//MY ASSIGNED ISSUES
//=====================================================

@GetMapping("/assigned/{engineerId}")
public PageResponseDto<IssueResponseDto> getAssignedIssues(

        @PathVariable Long engineerId,

        @RequestParam(required = false)
        String search,

        @RequestParam(required = false)
        IssueStatus status,

        @RequestParam(required = false)
        IssuePriority priority,

        @RequestParam(defaultValue = "0")
        Integer page,

        @RequestParam(defaultValue = "10")
        Integer size,

        @RequestParam(defaultValue = "assignedAt")
        String sort) {

    return issueService.getAssignedIssues(

            engineerId,

            search,

            status,

            priority,

            page,

            size,

            sort);
}

//=====================================================
//MY SUMMARY
//=====================================================

@GetMapping("/my-summary/{engineerId}")
public IssueMySummaryResponseDto getMySummary(
     @PathVariable Long engineerId) {

 return issueService.getMySummary(engineerId);
}

//=====================================================
//ENGINEER PERFORMANCE
//=====================================================

@GetMapping("/engineer-performance")
public List<EngineerPerformanceResponseDto> getEngineerPerformance() {

 return issueService.getEngineerPerformance();
}

//=====================================================
//SLA DETAILS
//=====================================================

@GetMapping("/{id}/sla")
public IssueSlaResponseDto getSlaDetails(
     @PathVariable Long id) {

 return issueService.getSlaDetails(id);
}

//=====================================================
//MARK SLA BREACH
//=====================================================

@PutMapping("/{id}/sla-breach")
public IssueResponseDto markSlaBreach(
     @PathVariable Long id,
     @RequestParam String reason) {

 return issueService.markSlaBreach(id, reason);
}

//=====================================================
//EXPORT CSV
//=====================================================

@GetMapping("/export/csv")
public ResponseEntity<byte[]> exportCsv() {

 return ResponseEntity.ok()

         .header(
                 HttpHeaders.CONTENT_DISPOSITION,
                 "attachment; filename=issues.csv")

         .contentType(MediaType.parseMediaType("text/csv"))

         .body(issueService.exportCsv());
}

//=====================================================
//EXPORT EXCEL
//=====================================================

@GetMapping("/export/excel")
public ResponseEntity<byte[]> exportExcel() {

 return ResponseEntity.ok()

         .header(
                 HttpHeaders.CONTENT_DISPOSITION,
                 "attachment; filename=issues.xlsx")

         .contentType(
                 MediaType.parseMediaType(
                         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

         .body(issueService.exportExcel());
}

//=====================================================
//EXPORT PDF
//=====================================================

@GetMapping("/export/pdf")
public ResponseEntity<byte[]> exportPdf() {

 return ResponseEntity.ok()

         .header(
                 HttpHeaders.CONTENT_DISPOSITION,
                 "attachment; filename=issues.pdf")

         .contentType(MediaType.APPLICATION_PDF)

         .body(issueService.exportPdf());
}

}