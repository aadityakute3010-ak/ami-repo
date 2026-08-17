package com.ami.service.impl;

import java.util.List;
import com.ami.entity.ArchivedDeviceOperation;
import com.ami.repository.ArchivedDeviceOperationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ami.entity.User;
import com.ami.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ami.dto.requests.CalibrationRequestDto;
import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.requests.RemoteConfigurationRequestDto;
import com.ami.dto.requests.RemoteRestartRequestDto;
import com.ami.dto.requests.RemoteSyncRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;
import com.ami.entity.DeviceOperation;
import com.ami.entity.DeviceTelemetry;
import com.ami.repository.DeviceOperationRepository;
import com.ami.repository.DeviceTelemetryRepository;
import com.ami.service.DeviceOperationService;
import com.ami.service.NotificationManagementService;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceOperationSummaryResponseDto;
import com.ami.enums.SourceType;
import com.ami.exception.BadRequestException;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.DeviceOperationMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.enums.NotificationType;
@Service
public class DeviceOperationServiceImpl
        implements DeviceOperationService {

    private final DeviceOperationRepository repository;
    private final DeviceTelemetryRepository telemetryRepository;
    private final ArchivedDeviceOperationRepository
    archivedDeviceOperationRepository;
    private final UserRepository userRepository;
    private final DeviceOperationMapper operationMapper;
     private final NotificationManagementService notificationManagementService;
    
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    private static final String STATUS_ASSIGNED = "ASSIGNED";

    public DeviceOperationServiceImpl(
            DeviceOperationRepository repository,
            DeviceTelemetryRepository telemetryRepository,
            NotificationManagementService notificationManagementService,
            ArchivedDeviceOperationRepository archivedDeviceOperationRepository,
            UserRepository userRepository,
            DeviceOperationMapper operationMapper) {

        this.repository = repository;
        this.telemetryRepository = telemetryRepository;
        this.notificationManagementService = notificationManagementService;
        this.archivedDeviceOperationRepository = archivedDeviceOperationRepository;
        this.userRepository = userRepository;
        this.operationMapper = operationMapper;
    }
    @Override
    public DeviceOperationResponseDto createOperation(
            CreateDeviceOperationRequestDto request) {

        DeviceOperation operation =
                DeviceOperation.builder()
                        .deviceId(request.getDeviceId())
                        .sourceType(request.getSourceType())
                        .operationType(request.getOperationType())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .severity(request.getSeverity())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus().toUpperCase()
                                        : STATUS_PENDING)
                        .assignedTo(request.getAssignedTo())
                        .rootCause(request.getRootCause())
                        .responseMessage(
                                request.getResponseMessage())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .resolved(
                                request.getResolved() != null
                                        ? request.getResolved()
                                        : false)
                        .executedAt(
                                java.time.LocalDateTime.now())
                        .acknowledgedBy(
                                request.getAcknowledgedBy())
                        .acknowledgedAt(
                                java.time.LocalDateTime.now())
                        .build();
        operation = repository.save(operation);

        return mapToResponse(operation);
    }

    @Override
    public Page<DeviceOperationResponseDto> getAllOperations(

            int page,

            int size,

            String search,

            SourceType sourceType,

            String status,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort);

        Specification<DeviceOperation> spec =
                (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {

            spec = spec.and((root, query, cb) ->
                    cb.or(

                            cb.like(
                                    cb.lower(root.get("deviceId")),
                                    "%" + search.toLowerCase() + "%"),

                            cb.like(
                                    cb.lower(root.get("operationType")),
                                    "%" + search.toLowerCase() + "%"),

                            cb.like(
                                    cb.lower(root.get("assignedTo")),
                                    "%" + search.toLowerCase() + "%")

                    ));
        }

        if (sourceType != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("sourceType"),
                            sourceType));
        }

        if (status != null && !status.isBlank()) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("status"),
                            status));
        }

        if (fromDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("requestedAt"),
                            fromDate));
        }

        if (toDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("requestedAt"),
                            toDate));
        }

        return repository.findAll(
                spec,
                pageable)
                .map(this::mapToResponse);
    }

    @Override
    public DeviceOperationResponseDto
    getOperationById(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        return mapToResponse(operation);
    }

    @Override
    public Page<DeviceOperationResponseDto> getByDeviceId(

            String deviceId,

            int page,

            int size,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceOperation> spec =
                (root, query, cb) -> cb.equal(
                        root.get("deviceId"),
                        deviceId);

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    public List<DeviceOperationResponseDto>
    getByOperationType(
            String operationType) {

        return repository.findByOperationType(
                        operationType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    @Transactional
    public String archiveOperation(
            Long operationId,
            String archiveReason) {

    	 DeviceOperation operation =
    	            getOperation(operationId);

        User loggedInUser = getLoggedInUser();

        ArchivedDeviceOperation archivedOperation =
        		operationMapper.mapToArchivedOperation(
                        operation,
                        loggedInUser,
                        archiveReason);

        archivedDeviceOperationRepository.save(
                archivedOperation);

        repository.delete(operation);

        return "Operation archived successfully";
    }
    @Override
    @Transactional
    public String restoreOperation(
            Long archivedOperationId) {

        ArchivedDeviceOperation archivedOperation =
                archivedDeviceOperationRepository.findById(
                        archivedOperationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Archived operation not found"));

        DeviceOperation operation =
        		operationMapper.mapToOperation(
                        archivedOperation);

        repository.save(
                operation);

        archivedDeviceOperationRepository.delete(
                archivedOperation);

        return "Operation restored successfully";
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(
                authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }
   
    
    @Override
    public DeviceOperationSummaryResponseDto
    getSummary() {

        return DeviceOperationSummaryResponseDto
                .builder()

                .totalOperations(
                        repository.count())

                .resolvedOperations(
                        repository.countByResolved(true))

                .pendingOperations(
                        repository.countByResolved(false))

                .waterOperations(
                        repository.countBySourceType(
                                SourceType.WATER))

                .gasOperations(
                        repository.countBySourceType(
                                SourceType.GAS))

                .energyOperations(
                        repository.countBySourceType(
                                SourceType.ENERGY))

                .solarOperations(
                        repository.countBySourceType(
                                SourceType.SOLAR))

                .build();
    }
    @Override
    public DeviceDashboardResponseDto
    getDashboard() {

        long totalDevices =
                repository.findAll()
                        .stream()
                        .map(DeviceOperation::getDeviceId)
                        .distinct()
                        .count();

        long resolvedOperations =
                repository.countByResolved(true);

        long pendingOperations =
                repository.countByResolved(false);

        return DeviceDashboardResponseDto
                .builder()
                .totalDevices(totalDevices)
                .onlineDevices(0L)
                .offlineDevices(0L)
                .leakDetectedDevices(0L)
                .energyDevices(
                        repository.countBySourceType(
                                SourceType.ENERGY))
                .waterDevices(
                        repository.countBySourceType(
                                SourceType.WATER))
                .gasDevices(
                        repository.countBySourceType(
                                SourceType.GAS))
                .solarDevices(
                        repository.countBySourceType(
                                SourceType.SOLAR))
                .activeOperations(
                        resolvedOperations + pendingOperations)
                .resolvedOperations(
                        resolvedOperations)
                .pendingOperations(
                        pendingOperations)
                .totalConsumption(0.0)
                .averagePressure(0.0)
                .averageTemperature(0.0)
                .averageFlowRate(0.0)
                .build();
    }
    @Override
    public DeviceAnalyticsResponseDto
    getAnalytics() {

        long total =
                repository.count();

        long resolved =
                repository.countByResolved(true);

        long pending =
                repository.countByResolved(false);

        double resolvedPercentage =
                total == 0
                        ? 0
                        : (resolved * 100.0) / total;

        double pendingPercentage =
                total == 0
                        ? 0
                        : (pending * 100.0) / total;

        return DeviceAnalyticsResponseDto
                .builder()
                .totalConsumption(0.0)
                .averageConsumption(0.0)
                .peakConsumption(0.0)
                .averagePressure(0.0)
                .averageTemperature(0.0)
                .averageFlowRate(0.0)
                .totalReadings(total)
                .forecastConsumption(0.0)
                .leakagePercentage(0.0)
                .averageBatteryLevel(0.0)
                .averagePipelineHealth(0.0)
                .averageSensorHealth(0.0)
                .onlinePercentage(resolvedPercentage)
                .offlinePercentage(pendingPercentage)
                .build();
    }
    @Override
    public List<DeviceOperationResponseDto>
    getBySourceType(
            SourceType sourceType) {

        return repository.findBySourceType(
                sourceType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public Page<DeviceOperationResponseDto> getByStatus(

            String status,

            int page,

            int size,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceOperation> spec =
                (root, query, cb) ->
                        cb.equal(root.get("status"), status);

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    public Page<DeviceOperationResponseDto> getResolvedOperations(

            int page,

            int size,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceOperation> spec =
                (root, query, cb) ->
                        cb.isTrue(root.get("resolved"));

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    public Page<DeviceOperationResponseDto> getPendingOperations(

            int page,

            int size,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceOperation> spec =
                (root, query, cb) ->
                        cb.isFalse(root.get("resolved"));

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    public String resolveOperation(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setResolved(true);
        operation.setStatus(STATUS_RESOLVED);

        repository.save(operation);

        return "Operation resolved successfully";
    }
    @Override
    public String acknowledgeOperation(
            Long id,
            String acknowledgedBy) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setAcknowledgedBy(
                acknowledgedBy);

        operation.setAcknowledgedAt(
                java.time.LocalDateTime.now());

        operation.setStatus(
                STATUS_ACKNOWLEDGED);

        repository.save(operation);
        return "Operation acknowledged successfully";
    }
    @Override
    public String assignOperation(
            Long id,
            String assignedTo) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setAssignedTo(
                assignedTo);

        operation.setStatus(
                STATUS_ASSIGNED);

        repository.save(operation);

        return "Operation assigned successfully";
    }
    @Override
    public String updateOperationStatus(
            Long id,
            String status) {

    	DeviceOperation operation =
    	        getOperation(id);
        if (status == null || status.isBlank()) {

            throw new BadRequestException(
                    "Status cannot be empty");
        }

        operation.setStatus(
                status.trim().toUpperCase());

        repository.save(operation);

        return "Operation status updated successfully";
    }
    
    @Override
    public String restartDevice(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "RESTART");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Device restarted successfully");

        repository.save(operation);
        createNotification(
                NotificationType.DEVICE,
                "Device Restart",
                "Device " + operation.getDeviceId()
                        + " restarted successfully",
                "ADMIN");

                     return "Device restarted successfully";
    }
    @Override
    public String syncDevice(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "SYNC");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Device synchronized successfully");

        repository.save(operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Device Sync",
                "Device " + operation.getDeviceId()
                        + " synchronized successfully",
                "ADMIN");

        return "Device synchronized successfully";
    }
    @Override
    public String updateFirmware(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "FIRMWARE_UPDATE");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Firmware updated successfully");

        repository.save(operation);
        
        createNotification(
        	    NotificationType.DEVICE,
        	    "Firmware Update",
        	    "Firmware updated successfully for device "
        	            + operation.getDeviceId(),
        	    "ADMIN");

        return "Firmware updated successfully";
    }
    @Override
    public String openValve(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "OPEN_VALVE");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Valve opened successfully");

        repository.save(operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Valve Open",
                "Valve opened successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Valve opened successfully";
    }
    @Override
    public String closeValve(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "CLOSE_VALVE");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Valve closed successfully");

        repository.save(operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Valve Close",
                "Valve closed successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Valve closed successfully";
    }
    @Override
    public byte[] exportOperations(
            String format) {

        switch (format.toLowerCase()) {

            case "excel":
                return exportExcel();

            case "pdf":
                return exportPdf();

            case "csv":
            default:
                return exportCsv();
        }
    }
    private byte[] exportCsv() {

        List<DeviceOperation> operations =
                repository.findAll();

        StringBuilder csv =
                new StringBuilder();

        csv.append(
                "Id,DeviceId,SourceType,OperationType,Title,Status,AssignedTo,Resolved\n");

        for (DeviceOperation operation : operations) {

            csv.append(operation.getId()).append(",");
            csv.append(operation.getDeviceId()).append(",");
            csv.append(operation.getSourceType()).append(",");
            csv.append(operation.getOperationType()).append(",");
            csv.append(operation.getTitle()).append(",");
            csv.append(operation.getStatus()).append(",");
            csv.append(operation.getAssignedTo()).append(",");
            csv.append(operation.getResolved()).append("\n");
        }

        return csv.toString().getBytes(
                java.nio.charset.StandardCharsets.UTF_8);
    }
    private byte[] exportExcel() {

        try (Workbook workbook = new XSSFWorkbook()) {

            var sheet =
                    workbook.createSheet(
                            "Device Operations");

            Row header =
                    sheet.createRow(0);

            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("Device Id");
            header.createCell(2).setCellValue("Source Type");
            header.createCell(3).setCellValue("Operation Type");
            header.createCell(4).setCellValue("Title");
            header.createCell(5).setCellValue("Status");
            header.createCell(6).setCellValue("Assigned To");
            header.createCell(7).setCellValue("Resolved");

            List<DeviceOperation> operations =
                    repository.findAll();

            int rowNum = 1;

            for (DeviceOperation operation : operations) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(operation.getId());

                row.createCell(1)
                        .setCellValue(operation.getDeviceId());

                row.createCell(2)
                        .setCellValue(
                                operation.getSourceType().name());

                row.createCell(3)
                        .setCellValue(
                                operation.getOperationType());

                row.createCell(4)
                        .setCellValue(
                                operation.getTitle());

                row.createCell(5)
                        .setCellValue(
                                operation.getStatus());

                row.createCell(6)
                        .setCellValue(
                                operation.getAssignedTo());

                row.createCell(7)
                        .setCellValue(
                                operation.getResolved());
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            out.flush();

            return out.toByteArray();

        }catch (Exception e) {

            throw new IllegalStateException(
                    "Excel export failed",
                    e);
        }
    }
    private byte[] exportPdf() {

        try {

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    out);

            document.open();

            document.add(
                    new Paragraph(
                            "Device Operations Report"));

            document.add(
                    new Paragraph(" "));

            List<DeviceOperation> operations =
                    repository.findAll();

            for (DeviceOperation operation : operations) {

                document.add(

                        new Paragraph(

                                "Device : "
                                        + operation.getDeviceId()

                                        + " | Type : "
                                        + operation.getOperationType()

                                        + " | Status : "
                                        + operation.getStatus()

                                        + " | Source : "
                                        + operation.getSourceType()

                                        + " | Resolved : "
                                        + operation.getResolved()));
            }

            document.close();
            out.flush();

            return out.toByteArray();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "PDF export failed",
                    e);
        }
    }
    
    @Override
    public String remoteConfiguration(

            Long id,

            RemoteConfigurationRequestDto request) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "REMOTE_CONFIGURATION");

        operation.setStatus(
                "COMPLETED");

        operation.setRequestedBy(
                request.getRequestedBy());

        operation.setRequestedAt(
                LocalDateTime.now());

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Remote configuration applied successfully");

        operation.setResponse(
                request.getConfiguration());

        repository.save(
                operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Remote Configuration",
                "Remote configuration applied successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Remote configuration executed successfully";
    }
    @Override
    public String remoteRestart(

            Long id,

            RemoteRestartRequestDto request) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "REMOTE_RESTART");

        operation.setStatus(
                "COMPLETED");

        operation.setRequestedBy(
                request.getRequestedBy());

        operation.setRequestedAt(
                LocalDateTime.now());

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Remote restart executed successfully");

        operation.setRemarks(
                request.getRemarks());

        repository.save(
                operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Remote Restart",
                "Remote restart executed successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");
        return "Remote restart executed successfully";
    }
    @Override
    public String remoteSync(

            Long id,

            RemoteSyncRequestDto request) {
    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "REMOTE_SYNC");

        operation.setStatus(
                "COMPLETED");

        operation.setRequestedBy(
                request.getRequestedBy());

        operation.setRequestedAt(
                LocalDateTime.now());

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Remote sync completed successfully");

        operation.setRemarks(
                request.getRemarks());

        repository.save(
                operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Remote Sync",
                "Remote synchronization completed successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Remote sync executed successfully";
    }
    @Override
    public String calibrateDevice(

            Long id,

            CalibrationRequestDto request) {

    	DeviceOperation operation =
    	        getOperation(id);
        operation.setOperationType(
                "CALIBRATION");

        operation.setStatus(
                "COMPLETED");

        operation.setRequestedBy(
                request.getRequestedBy());

        operation.setRequestedAt(
                LocalDateTime.now());

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Calibration completed successfully");

        operation.setResponse(
                request.getCalibrationValue());

        operation.setRemarks(
                request.getRemarks());

        repository.save(
                operation);
        
        createNotification(
                NotificationType.DEVICE,
                "Device Calibration",
                "Calibration completed successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Calibration completed successfully";
    }
    
    @Override
    public String emergencyShutdown(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "EMERGENCY_SHUTDOWN");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Emergency shutdown executed successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setEmergencyShutdown(true);

        telemetry.setAlarmActive(true);

        telemetry.setValveStatus("CLOSED");

        telemetry.setStatus("EMERGENCY");

        telemetry.setDeviceOnline(false);

        telemetryRepository.save(telemetry);

        repository.save(operation);

        createNotification(
                NotificationType.DEVICE,
                "Emergency Shutdown",
                "Emergency shutdown executed for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Emergency shutdown executed successfully";
    }
    @Override
    public String resetGasAlarm(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "RESET_GAS_ALARM");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Gas alarm reset successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setAlarmActive(false);

        telemetry.setEmergencyShutdown(false);

        telemetry.setStatus("ACTIVE");

        telemetryRepository.save(telemetry);

        repository.save(operation);

        createNotification(
                NotificationType.DEVICE,
                "Gas Alarm Reset",
                "Gas alarm reset successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Gas alarm reset successfully";
    }
    
    @Override
    public String startGasFlow(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "START_GAS_FLOW");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Gas flow started successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setValveStatus(
                "OPEN");

        telemetry.setStatus(
                "ACTIVE");

        telemetry.setDeviceOnline(
                true);

        telemetry.setEmergencyShutdown(
                false);

        telemetry.setAlarmActive(
                false);

        telemetryRepository.save(
                telemetry);

        repository.save(operation);
        createNotification(
                NotificationType.DEVICE,
                "Gas Flow Started",
                "Gas flow started successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Gas flow started successfully";
    }
    
    @Override
    public String stopGasFlow(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "STOP_GAS_FLOW");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Gas flow stopped successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setValveStatus(
                "CLOSED");

        telemetry.setStatus(
                "STOPPED");

        telemetry.setDeviceOnline(
                true);

        telemetry.setEmergencyShutdown(
                false);

        telemetry.setAlarmActive(
                false);

        telemetryRepository.save(
                telemetry);

        repository.save(operation);

        createNotification(
                NotificationType.DEVICE,
                "Gas Flow Stopped",
                "Gas flow stopped successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Gas flow stopped successfully";
    }
    @Override
    public String purgePipeline(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "PURGE_PIPELINE");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Pipeline purged successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setStatus(
                "PURGING");

        telemetry.setValveStatus(
                "OPEN");

        telemetry.setDeviceOnline(
                true);

        telemetry.setAlarmActive(
                false);

        telemetry.setEmergencyShutdown(
                false);

        telemetryRepository.save(
                telemetry);

        repository.save(operation);

        createNotification(
                NotificationType.DEVICE,
                "Pipeline Purge",
                "Pipeline purged successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Pipeline purged successfully";
    }
    @Override
    public String resumeGasSupply(
            Long id) {

    	DeviceOperation operation =
    	        getOperation(id);

        operation.setOperationType(
                "RESUME_GAS_SUPPLY");

        operation.setStatus(
                STATUS_RESOLVED);

        operation.setResolved(
                true);

        operation.setExecutedAt(
                LocalDateTime.now());

        operation.setResponseMessage(
                "Gas supply resumed successfully");
        
        DeviceTelemetry telemetry =
                getLatestTelemetry(
                        operation.getDeviceId());

        telemetry.setStatus(
                "ACTIVE");

        telemetry.setValveStatus(
                "OPEN");

        telemetry.setDeviceOnline(
                true);

        telemetry.setEmergencyShutdown(
                false);

        telemetry.setAlarmActive(
                false);

        telemetryRepository.save(
                telemetry);

        repository.save(operation);

        createNotification(
                NotificationType.DEVICE,
                "Gas Supply Resumed",
                "Gas supply resumed successfully for device "
                        + operation.getDeviceId(),
                "ADMIN");

        return "Gas supply resumed successfully";
    }
    private DeviceTelemetry getLatestTelemetry(
            String deviceId) {

    	return telemetryRepository
    	        .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Telemetry not found for device: " + deviceId));
    }
    private DeviceOperation getOperation(
            Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Operation not found"));
    }
    private void createNotification(
            NotificationType type,
            String title,
            String message,
            String recipient) {

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(type)

                        .title(title)

                        .message(message)

                        .recipient(recipient)

                        .build());
    }
    private DeviceOperationResponseDto
    mapToResponse(
            DeviceOperation operation) {

        return DeviceOperationResponseDto
                .builder()
                .id(operation.getId())
                .deviceId(operation.getDeviceId())
                .sourceType(operation.getSourceType())
                .operationType(operation.getOperationType())
                .title(operation.getTitle())
                .description(operation.getDescription())
                .severity(operation.getSeverity())
                .status(operation.getStatus())
                .assignedTo(operation.getAssignedTo())
                .rootCause(operation.getRootCause())
                .latitude(operation.getLatitude())
                .longitude(operation.getLongitude())
                .resolved(operation.getResolved())
                .responseMessage(
                        operation.getResponseMessage())
                .executedAt(
                        operation.getExecutedAt())
                .acknowledgedBy(
                        operation.getAcknowledgedBy())
                .acknowledgedAt(
                        operation.getAcknowledgedAt())
                .build();
}
}