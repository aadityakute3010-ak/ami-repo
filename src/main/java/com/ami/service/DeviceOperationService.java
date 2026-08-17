package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CalibrationRequestDto;
import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.requests.PingDeviceRequestDto;
import com.ami.dto.requests.RefreshDeviceRequestDto;
import com.ami.dto.requests.RemoteConfigurationRequestDto;
import com.ami.dto.requests.RemoteRestartRequestDto;
import com.ami.dto.requests.RemoteSyncRequestDto;
import com.ami.dto.requests.ResetDeviceRequestDto;
import com.ami.dto.requests.RetryOperationRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceOperationSummaryResponseDto;
import com.ami.dto.responses.OperationHistoryResponseDto;
import com.ami.dto.responses.OperationTimelineResponseDto;
import com.ami.dto.responses.RecentActivityResponseDto;
import com.ami.enums.SourceType;

public interface DeviceOperationService {

    DeviceOperationResponseDto createOperation(
            CreateDeviceOperationRequestDto request);

    Page<DeviceOperationResponseDto> getAllOperations(

            int page,

            int size,

            String search,

            SourceType sourceType,

            String status,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction);

    DeviceOperationResponseDto
    getOperationById(
            Long id);

    Page<DeviceOperationResponseDto> getByDeviceId(

            String deviceId,

            int page,

            int size,

            String sortBy,

            String direction);

    List<DeviceOperationResponseDto>
    getByOperationType(
            String operationType);

    String archiveOperation(
            Long operationId,
            String archiveReason);

    String restoreOperation(
            Long archivedOperationId);
    
    DeviceOperationSummaryResponseDto
    getSummary();

    DeviceDashboardResponseDto
    getDashboard();

    DeviceAnalyticsResponseDto
    getAnalytics();

    List<DeviceOperationResponseDto>
    getBySourceType(
            SourceType sourceType);

    Page<DeviceOperationResponseDto> getByStatus(

            String status,

            int page,

            int size,

            String sortBy,

            String direction);

    Page<DeviceOperationResponseDto> getResolvedOperations(

            int page,

            int size,

            String sortBy,

            String direction);

    Page<DeviceOperationResponseDto> getPendingOperations(

            int page,

            int size,

            String sortBy,

            String direction);

    String resolveOperation(
            Long id);

    String acknowledgeOperation(
            Long id,
            String acknowledgedBy);

    String assignOperation(
            Long id,
            String assignedTo);

    String updateOperationStatus(
            Long id,
            String status);

    byte[] exportOperations(
            String format);
    
    String restartDevice(
            Long id);

    String syncDevice(
            Long id);

    String updateFirmware(
            Long id);

    String openValve(
            Long id);

    String closeValve(
            Long id);
   
    String remoteConfiguration(

            Long id,

            RemoteConfigurationRequestDto request);
    
    String remoteRestart(

            Long id,

            RemoteRestartRequestDto request);
    
    String remoteSync(

            Long id,

            RemoteSyncRequestDto request);
    
    String calibrateDevice(

            Long id,

            CalibrationRequestDto request);
    
    String emergencyShutdown(
            Long id);

    String resetGasAlarm(
            Long id);

    String startGasFlow(
            Long id);

    String stopGasFlow(
            Long id);

    String purgePipeline(
            Long id);

    String resumeGasSupply(
            Long id);
    
}