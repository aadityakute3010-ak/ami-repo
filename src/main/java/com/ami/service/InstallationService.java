package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

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
import com.ami.dto.requests.UploadInstallationAttachmentRequestDto;
import com.ami.dto.responses.InstallationAnalyticsResponseDto;
import com.ami.dto.responses.InstallationAssignmentAttemptResponseDto;
import com.ami.dto.responses.InstallationAttachmentResponseDto;
import com.ami.dto.responses.InstallationChecklistResponseDto;
import com.ami.dto.responses.InstallationDashboardResponseDto;
import com.ami.dto.responses.InstallationEngineerWorkloadResponseDto;
import com.ami.dto.responses.InstallationHistoryResponseDto;
import com.ami.dto.responses.InstallationPhotoResponseDto;
import com.ami.dto.responses.InstallationRemarkResponseDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.dto.responses.InstallationSourceSummaryResponseDto;
import com.ami.dto.responses.InstallationStatisticsResponseDto;
import com.ami.dto.responses.InstallationTimelineResponseDto;
import com.ami.dto.responses.PageResponseDto;
import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;

public interface InstallationService {

  
    InstallationResponseDto createInstallation(
            CreateInstallationRequestDto request);

    PageResponseDto<InstallationResponseDto> getAllInstallations(
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
            String sort);

    InstallationResponseDto getInstallationById(
            Long id);

    InstallationResponseDto updateInstallation(
            Long id,
            UpdateInstallationRequestDto request);

    void deleteInstallation(Long id);



    InstallationResponseDto assignEngineer(
            Long id,
            AssignInstallationEngineerRequestDto request);

    InstallationResponseDto startInstallation(
            Long id);

    InstallationResponseDto completeInstallation(
            Long id,
            CompleteInstallationRequestDto request);

    InstallationResponseDto cancelInstallation(
            Long id,
            CancelInstallationRequestDto request);


InstallationResponseDto rescheduleInstallation(
        Long id,
        RescheduleInstallationRequestDto request);

 

    InstallationDashboardResponseDto getDashboard();

    InstallationStatisticsResponseDto getStatistics();

    InstallationAnalyticsResponseDto getAnalytics();

    List<InstallationSourceSummaryResponseDto> getSourceSummary();



    List<InstallationEngineerWorkloadResponseDto> getEngineerWorkload();

    List<InstallationEngineerWorkloadResponseDto> getAvailableEngineers();

    List<InstallationEngineerWorkloadResponseDto> getAllEngineers();

  

    List<InstallationHistoryResponseDto> getHistory(
            Long installationId);

    List<InstallationTimelineResponseDto> getTimeline(
            Long installationId);

    List<InstallationRemarkResponseDto> getRemarks(
            Long installationId);

    InstallationRemarkResponseDto addRemark(
            Long installationId,
            AddInstallationRemarkRequestDto request);
    
    void deleteRemark(
            Long installationId,
            Long remarkId);

    InstallationChecklistResponseDto getChecklist(
            Long installationId);

    InstallationChecklistResponseDto updateChecklist(
            Long installationId,
            InstallationChecklistRequestDto request);

    List<InstallationPhotoResponseDto> getPhotos(
            Long installationId);

    InstallationPhotoResponseDto uploadPhoto(
            Long installationId,
            MultipartFile file);

    void deletePhoto(
            Long installationId,
            Long photoId);
    
    List<InstallationAttachmentResponseDto> getAttachments(
            Long installationId);

    InstallationAttachmentResponseDto uploadAttachment(
            Long installationId,
            MultipartFile file,
            UploadInstallationAttachmentRequestDto request);

    void deleteAttachment(
            Long installationId,
            Long attachmentId);

    InstallationResponseDto reassignEngineer(
            Long installationId,
            ReassignInstallationEngineerRequestDto request);

    InstallationResponseDto markAssignmentFailed(
            Long installationId,
            InstallationAssignmentFailureRequestDto request);

    List<InstallationAssignmentAttemptResponseDto>
    getAssignmentAttempts(
            Long installationId);



    List<InstallationResponseDto> getEngineerAssignments(
            Long engineerId);


byte[] exportCsv();

byte[] exportExcel();

byte[] exportPdf();
}