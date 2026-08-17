package com.ami.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.AssignEngineerRequestDto;
import com.ami.dto.requests.AssignmentFailureRequestDto;
import com.ami.dto.requests.CancelMaintenanceRequestDto;
import com.ami.dto.requests.CompleteMaintenanceRequestDto;
import com.ami.dto.requests.CreateMaintenanceRequestDto;
import com.ami.dto.requests.ReassignEngineerRequestDto;
import com.ami.dto.requests.RescheduleMaintenanceRequestDto;
import com.ami.dto.requests.StartMaintenanceRequestDto;
import com.ami.dto.requests.UpdateMaintenanceAttachmentsRequestDto;
import com.ami.dto.requests.UpdateMaintenanceChecklistRequestDto;
import com.ami.dto.requests.UpdateMaintenancePhotoRequestDto;
import com.ami.dto.requests.UpdateMaintenanceRemarksRequestDto;
import com.ami.dto.responses.MaintenanceAttachmentsResponseDto;
import com.ami.dto.responses.MaintenanceChecklistResponseDto;
import com.ami.dto.responses.MaintenanceDashboardResponseDto;
import com.ami.dto.responses.MaintenanceHistoryResponseDto;
import com.ami.dto.responses.MaintenancePhotoResponseDto;
import com.ami.dto.responses.MaintenanceRemarksResponseDto;
import com.ami.dto.responses.MaintenanceResponseDto;
import com.ami.dto.responses.MaintenanceTimelineResponseDto;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;
import com.ami.dto.responses.MaintenanceHistoryResponseDto;
public interface MaintenanceService {

    MaintenanceResponseDto createMaintenance(
            CreateMaintenanceRequestDto request);

    MaintenanceResponseDto getMaintenanceById(
            Long id);

    Page<MaintenanceResponseDto> getAllMaintenance(

            int page,

            int size,

            String search,

            MaintenanceType maintenanceType,

            MaintenanceStatus status,

            String sortBy,

            String direction);

    MaintenanceResponseDto updateMaintenance(

            Long id,

            CreateMaintenanceRequestDto request);

    String deleteMaintenance(
            Long id);

    MaintenanceDashboardResponseDto getDashboard();
    
    List<MaintenanceTimelineResponseDto> getTimeline();
    
    List<MaintenanceTimelineResponseDto> getTimeline(
            Long maintenanceId);
    
    List<MaintenanceHistoryResponseDto> getHistory(
            String deviceId);
    
    List<MaintenanceResponseDto> getUpcomingMaintenance();
    
    List<MaintenanceResponseDto> getCompletedMaintenance();
    
    MaintenanceResponseDto assignEngineer(
            Long maintenanceId,
            AssignEngineerRequestDto request);
    
    MaintenanceResponseDto reassignEngineer(
            Long maintenanceId,
            ReassignEngineerRequestDto request);
    
    MaintenanceResponseDto recordAssignmentFailure(
            Long maintenanceId,
            AssignmentFailureRequestDto request);
    
    MaintenanceResponseDto startMaintenance(
            Long maintenanceId,
            StartMaintenanceRequestDto request);
    
    MaintenanceResponseDto completeMaintenance(
            Long maintenanceId,
            CompleteMaintenanceRequestDto request);
    
    MaintenanceResponseDto cancelMaintenance(
            Long maintenanceId,
            CancelMaintenanceRequestDto request);
    
    MaintenanceResponseDto rescheduleMaintenance(
            Long maintenanceId,
            RescheduleMaintenanceRequestDto request);
    
    MaintenanceChecklistResponseDto getMaintenanceChecklist(
            Long maintenanceId);

    MaintenanceChecklistResponseDto updateMaintenanceChecklist(
            Long maintenanceId,
            UpdateMaintenanceChecklistRequestDto request);
    
    MaintenanceRemarksResponseDto getMaintenanceRemarks(
            Long maintenanceId);

    MaintenanceRemarksResponseDto updateMaintenanceRemarks(
            Long maintenanceId,
            UpdateMaintenanceRemarksRequestDto request);
    
    MaintenancePhotoResponseDto getMaintenancePhotos(
            Long maintenanceId);

    MaintenancePhotoResponseDto updateMaintenancePhotos(
            Long maintenanceId,
            UpdateMaintenancePhotoRequestDto request);
    
    MaintenanceAttachmentsResponseDto getMaintenanceAttachments(
            Long maintenanceId);

    MaintenanceAttachmentsResponseDto updateMaintenanceAttachments(
            Long maintenanceId,
            UpdateMaintenanceAttachmentsRequestDto request);
}