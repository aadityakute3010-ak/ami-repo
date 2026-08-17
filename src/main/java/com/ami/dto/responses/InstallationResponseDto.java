package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import com.ami.enums.AssignmentStatus;
import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;
import com.ami.enums.InstallationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationResponseDto {

    private Long id;

    private String installationNumber;

    private String deviceId;

    private String deviceName;

    private String meterNumber;

    private String serialNumber;

    private InstallationSource source;

    private String customerId;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String state;

    private String city;

    private String zone;

    private String area;

    private String address;

    private Double latitude;

    private Double longitude;

    private InstallationPriority priority;

    private InstallationStatus status;
    
    private AssignmentStatus assignmentStatus;

    private LocalDateTime scheduledDate;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

   private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private InstallationEngineerResponseDto assignedEngineer;
    
    private String assignedBy;

    private LocalDateTime assignedAt;

    private Integer assignmentRetryCount;

    private LocalDateTime lastAssignmentAttempt;

    private Double completionPercentage;

    private InstallationChecklistResponseDto checklist;

    private List<InstallationPhotoResponseDto> photos;

    private List<InstallationTimelineResponseDto> timeline;
    
    private List<InstallationRemarkResponseDto> remarks;
    
    private List<InstallationHistoryResponseDto> history;

}