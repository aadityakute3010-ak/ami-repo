package com.ami.service.impl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import com.ami.entity.DeviceTelemetry;
import com.ami.dto.responses.DeviceHealthResponseDto;
import java.util.List;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import com.ami.dto.requests.CreateDeviceTelemetryRequestDto;
import com.ami.dto.requests.ReportFilterRequestDto;
import com.ami.dto.responses.DashboardChartResponseDto;
import com.ami.dto.responses.DeviceAnalyticsResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceLogResponseDto;
import com.ami.dto.responses.DeviceTelemetryResponseDto;
import com.ami.dto.responses.GasAlarmResponseDto;
import com.ami.dto.responses.GasAnalyticsResponseDto;
import com.ami.dto.responses.GasDashboardResponseDto;
import com.ami.dto.responses.GasHistoryResponseDto;
import com.ami.dto.responses.GasLeakResponseDto;
import com.ami.dto.responses.GasLiveTelemetryResponseDto;
import com.ami.dto.responses.GasQualityResponseDto;
import com.ami.dto.responses.GasSummaryResponseDto;
import com.ami.dto.responses.LeakSummaryResponseDto;
import com.ami.dto.responses.MapLocationResponseDto;
import com.ami.dto.responses.PumpStatusResponseDto;
import com.ami.dto.responses.ReportResponseDto;
import com.ami.dto.responses.TankLevelResponseDto;
import com.ami.dto.responses.TimelineResponseDto;
import com.ami.dto.responses.ValveStatusResponseDto;
import com.ami.dto.responses.WaterAnalyticsResponseDto;
import com.ami.dto.responses.WaterDashboardResponseDto;
import com.ami.dto.responses.WaterHistoryResponseDto;
import com.ami.dto.responses.WaterLiveTelemetryResponseDto;
import com.ami.dto.responses.WaterQualityResponseDto;
import com.ami.dto.responses.WaterSummaryResponseDto;
import com.ami.repository.DeviceTelemetryRepository;
import com.ami.repository.EnergyTelemetryRepository;
import com.ami.repository.GasTelemetryRepository;
import com.ami.repository.InstallationRepository;
import com.ami.repository.IssueRepository;
import com.ami.repository.MaintenanceRepository;
import com.ami.repository.SolarTelemetryRepository;
import com.ami.repository.WaterTelemetryRepository;
import com.ami.service.DeviceTelemetryService;
import com.ami.service.TelemetryAlertService;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.InstallationStatus;
import com.ami.enums.IssueStatus;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.PumpStatus;
import com.ami.enums.SourceType;
import com.ami.exception.BadRequestException;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.DeviceTelemetryMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import com.ami.entity.telemetry.WaterTelemetry;
import com.ami.entity.telemetry.GasTelemetry;
import com.ami.entity.telemetry.EnergyTelemetry;
import com.ami.entity.telemetry.SolarTelemetry;
import com.ami.entity.ArchivedDeviceTelemetry;
import com.ami.entity.Device;
import com.ami.entity.User;
import com.ami.repository.ArchivedDeviceTelemetryRepository;
import com.ami.repository.DeviceOperationRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Service
public class DeviceTelemetryServiceImpl
        implements DeviceTelemetryService {
	
	private static final String STATUS_PENDING = "PENDING";
	private static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
	private static final String STATUS_RESOLVED = "RESOLVED";

    private final DeviceTelemetryRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final TelemetryAlertService telemetryAlertService;
    private final WaterTelemetryRepository waterRepository;
    private final ArchivedDeviceTelemetryRepository archivedTelemetryRepository;
    

    private final UserRepository userRepository;

    private final GasTelemetryRepository gasRepository;

    private final EnergyTelemetryRepository energyRepository;

    private final SolarTelemetryRepository solarRepository;
    
    private final InstallationRepository installationRepository;

    private final MaintenanceRepository maintenanceRepository;

    private final IssueRepository issueRepository;

    private final DeviceOperationRepository deviceOperationRepository;
    
    private final DeviceTelemetryMapper telemetryMapper;
    
    private final DeviceRepository deviceRepository;
   

    public DeviceTelemetryServiceImpl(

            DeviceTelemetryRepository repository,

            WaterTelemetryRepository waterRepository,

            GasTelemetryRepository gasRepository,

            EnergyTelemetryRepository energyRepository,

            SolarTelemetryRepository solarRepository,

            SimpMessagingTemplate messagingTemplate,

            TelemetryAlertService telemetryAlertService,

            ArchivedDeviceTelemetryRepository archivedTelemetryRepository,

            UserRepository userRepository,
            
            DeviceRepository deviceRepository,
            
            InstallationRepository installationRepository,

            MaintenanceRepository maintenanceRepository,

            IssueRepository issueRepository,

            DeviceOperationRepository deviceOperationRepository,
            DeviceTelemetryMapper telemetryMapper) {

        this.repository = repository;

        this.waterRepository = waterRepository;

        this.gasRepository = gasRepository;

        this.energyRepository = energyRepository;

        this.solarRepository = solarRepository;

        this.messagingTemplate = messagingTemplate;

        this.telemetryAlertService = telemetryAlertService;

        this.archivedTelemetryRepository = archivedTelemetryRepository;

        this.userRepository = userRepository;
        
        this.deviceRepository = deviceRepository;
        
        this.installationRepository = installationRepository;

        this.maintenanceRepository = maintenanceRepository;

        this.issueRepository = issueRepository;

        this.deviceOperationRepository = deviceOperationRepository;
        
        this.telemetryMapper = telemetryMapper;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "telemetryDashboard", allEntries = true),
            @CacheEvict(value = "telemetryAnalytics", allEntries = true),
            @CacheEvict(value = "waterDashboard", allEntries = true)
    })
    public DeviceTelemetryResponseDto createTelemetry(
            CreateDeviceTelemetryRequestDto request) {
    	
    	// ==========================================
    	// WATER MODULE
    	// ==========================================
    	if (request.getSourceType() == SourceType.WATER) {

    		WaterTelemetry telemetry =
    		        telemetryMapper.mapToWaterEntity(request);

    	    telemetry =
    	            waterRepository.save(telemetry);

    	   // telemetryAlertService.checkWaterAlerts(
    	         //   telemetry);

    	    DeviceTelemetryResponseDto response =
    	    		telemetryMapper.mapWaterResponse(telemetry);

    	    messagingTemplate.convertAndSend(
    	            "/topic/telemetry",
    	            response);

    	    messagingTemplate.convertAndSend(
    	            "/topic/telemetry/" + telemetry.getDeviceId(),
    	            response);

    	    return response;
    	}
    	
    	// ==========================================
    	// GAS MODULE
    	// ==========================================
    	if (request.getSourceType() == SourceType.GAS) {

    	    GasTelemetry telemetry =
    	    	    telemetryMapper.mapToGasEntity(request);

    	    telemetry =
    	            gasRepository.save(telemetry);

    	    // telemetryAlertService.checkGasAlerts(
    	    //         telemetry);

    	    DeviceTelemetryResponseDto response =
    	    		telemetryMapper.mapGasResponse(telemetry);

    	    messagingTemplate.convertAndSend(
    	            "/topic/gas",
    	            response);

    	    messagingTemplate.convertAndSend(
    	            "/topic/gas/" + telemetry.getDeviceId(),
    	            response);

    	    return response;
    	}

    	DeviceTelemetry telemetry =
    	        telemetryMapper.mapToDeviceTelemetry(request);
        telemetry =
                repository.save(
                        telemetry);

        // Automatic Alert Generation
        telemetryAlertService.checkTelemetryAlerts(
                telemetry);

        DeviceTelemetryResponseDto response =
                mapToResponse(
                        telemetry);

        messagingTemplate.convertAndSend(
                "/topic/telemetry",
                response);

        messagingTemplate.convertAndSend(
                "/topic/telemetry/" + telemetry.getDeviceId(),
                response);

        return response;
        
    }
    @Override
    @Transactional(readOnly = true)
    public Page<DeviceTelemetryResponseDto> getAllTelemetry(

            int page,
            int size,
            String search,
            SourceType sourceType,
            Boolean online,
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

        Specification<DeviceTelemetry> spec =
                (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {

            spec = spec.and((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("deviceId")),
                            "%" + search.toLowerCase() + "%"));
        }

        if (sourceType != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("sourceType"),
                            sourceType));
        }

        if (online != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(
                            root.get("deviceOnline"),
                            online));
        }

        if (fromDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("readingTime"),
                            fromDate));
        }

        if (toDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("readingTime"),
                            toDate));
        }

        return repository.findAll(
                spec,
                pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceTelemetryResponseDto
    getTelemetryById(
            Long id) {

    	DeviceTelemetry telemetry =
    	        repository.findById(id)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Telemetry not found"));

        return mapToResponse(
                telemetry);
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTelemetryResponseDto> getTelemetryByDeviceId(
            String deviceId,
            SourceType sourceType) {

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

            return waterRepository.findByDeviceId(deviceId)
                    .stream()
                    .map(telemetryMapper::mapWaterResponse)
                    .toList();
        }

        // ==========================================
        // GAS
        // ==========================================
        if (sourceType == SourceType.GAS) {

            return gasRepository.findByDeviceId(deviceId)
                    .stream()
                    .map(telemetryMapper::mapGasResponse)
                    .toList();
        }

        // ==========================================
        // ENERGY
        // ==========================================
        if (sourceType == SourceType.ENERGY) {

            return energyRepository.findByDeviceId(deviceId)
                    .stream()
                    .map(telemetryMapper::mapEnergyResponse)
                    .toList();
        }

        // ==========================================
        // SOLAR
        // ==========================================
        if (sourceType == SourceType.SOLAR) {

            return solarRepository.findByDeviceId(deviceId)
                    .stream()
                    .map(telemetryMapper::mapSolarResponse)
                    .toList();
        }

        return List.of();
    }
    @Override
    @Transactional(readOnly = true)
    public DeviceTelemetryResponseDto getLatestTelemetry(
            String deviceId,
            SourceType sourceType) {

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

        	WaterTelemetry telemetry =
        	        waterRepository
        	                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Water telemetry not found"));
        	return telemetryMapper.mapWaterResponse(telemetry);
        }

        // ==========================================
        // GAS
        // ==========================================
        if (sourceType == SourceType.GAS) {

        	GasTelemetry telemetry =
        	        gasRepository
        	                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Gas telemetry not found"));

            return telemetryMapper.mapGasResponse(telemetry);
        }

        // ==========================================
        // ENERGY
        // ==========================================
        if (sourceType == SourceType.ENERGY) {

            EnergyTelemetry telemetry =
                    energyRepository
                            .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                            .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Energy telemetry not found"));

            return telemetryMapper.mapEnergyResponse(telemetry);
        }

        // ==========================================
        // SOLAR
        // ==========================================
        if (sourceType == SourceType.SOLAR) {

            SolarTelemetry telemetry =
                    solarRepository
                            .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                            .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Solar telemetry not found"));

            return telemetryMapper.mapSolarResponse(telemetry);
        }
        throw new IllegalArgumentException(
                "Unsupported Source Type");
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTelemetryResponseDto>
    getBySourceType(
            SourceType sourceType) {

        return repository.findBySourceType(
                sourceType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTelemetryResponseDto>
    getOnlineDevices() {

        return repository.findByDeviceOnline(
                true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTelemetryResponseDto>
    getOfflineDevices() {

        return repository.findByDeviceOnline(
                false)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public Page<DeviceTelemetryResponseDto> getDeviceHistory(

            String deviceId,

            int page,

            int size,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceTelemetry> spec =
                (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("deviceId"), deviceId));

        if (fromDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("readingTime"),
                            fromDate));
        }

        if (toDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("readingTime"),
                            toDate));
        }

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<DeviceTelemetryResponseDto> getTelemetryHistory(

            int page,

            int size,

            SourceType sourceType,

            LocalDateTime fromDate,

            LocalDateTime toDate,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<DeviceTelemetry> spec =
                (root, query, cb) -> cb.conjunction();

        if (sourceType != null) {

            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("sourceType"), sourceType));
        }

        if (fromDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("readingTime"),
                            fromDate));
        }

        if (toDate != null) {

            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("readingTime"),
                            toDate));
        }

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("deviceDashboard")
    public DeviceDashboardResponseDto
    getDashboard(
            SourceType sourceType) {


    	long totalDevices =
    	        repository.countDistinctDevices(sourceType);

    	long onlineDevices =
    	        repository.countDistinctOnlineDevices(sourceType);

    	long leakDetectedDevices =
    	        repository.countDistinctLeakDetectedDevices(sourceType);

    	long pumpRunningDevices =
    	        repository.countDistinctPumpRunningDevices(sourceType);

    	long pumpStoppedDevices =
    	        repository.countDistinctPumpStoppedDevices(sourceType);

    	long offlineDevices =
    	        totalDevices - onlineDevices;
        
    	double totalConsumption =
    	        repository.getTotalConsumption(sourceType);

    	double avgPressure =
    	        repository.getAveragePressure(sourceType);

    	double avgTemperature =
    	        repository.getAverageTemperature(sourceType);

    	double avgFlowRate =
    	        repository.getAverageFlowRate(sourceType);

    	double avgBatteryLevel =
    	        repository.getAverageBatteryLevel(sourceType);

    	double avgSignalStrength =
    	        repository.getAverageSignalStrength(sourceType);

    	double maxConsumption =
    	        repository.getMaximumConsumption(sourceType);

    	double minConsumption =
    	        repository.getMinimumConsumption(sourceType);

    	long tamperDevices =
    	        repository.countDistinctTamperDevices(sourceType);

    	long valveOpenDevices =
    	        repository.countDistinctValveOpenDevices(sourceType);

    	long valveClosedDevices =
    	        repository.countDistinctValveClosedDevices(sourceType);

    	long lowBatteryDevices =
    	        repository.countDistinctLowBatteryDevices(sourceType);

    	long poorSignalDevices =
    	        repository.countDistinctPoorSignalDevices(sourceType);
        
        

        return DeviceDashboardResponseDto
                .builder()

                .totalDevices(
                        totalDevices)

                .onlineDevices(
                        onlineDevices)

                .offlineDevices(
                        offlineDevices)

                .leakDetectedDevices(
                        leakDetectedDevices)
                .energyDevices(
                        repository.countDistinctDevicesBySourceType(
                                SourceType.ENERGY))

                .waterDevices(
                        repository.countDistinctDevicesBySourceType(
                                SourceType.WATER))

                .gasDevices(
                        repository.countDistinctDevicesBySourceType(
                                SourceType.GAS))

                .solarDevices(
                        repository.countDistinctDevicesBySourceType(
                                SourceType.SOLAR))
                .activeOperations(0L)

                .pendingOperations(0L)

                .resolvedOperations(0L)

                .totalConsumption(
                        totalConsumption)

                .averagePressure(
                        avgPressure)

                .averageTemperature(
                        avgTemperature)

                .averageFlowRate(
                        avgFlowRate)
                
                .averageBatteryLevel(
                        avgBatteryLevel)

                .averageSignalStrength(
                        avgSignalStrength)

                .maximumConsumption(
                        maxConsumption)

                .minimumConsumption(
                        minConsumption)

                .tamperDevices(
                        tamperDevices)

                .lowBatteryDevices(
                        lowBatteryDevices)

                .poorSignalDevices(
                        poorSignalDevices)

                .valveOpenDevices(
                        valveOpenDevices)

                .valveClosedDevices(
                        valveClosedDevices)
                
                .pumpRunningDevices(
                        pumpRunningDevices)

                .pumpStoppedDevices(
                        pumpStoppedDevices)

                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("deviceAnalytics")
    public DeviceAnalyticsResponseDto
    getAnalytics(
            SourceType sourceType) {

    
    	double totalConsumption =
    	        repository.getTotalConsumption(sourceType);
                        
                       
        double averageConsumption =
                 repository.getAverageConsumption(sourceType);

        double peakConsumption =
                repository.getMaximumConsumption(sourceType);
        double averagePressure =
                repository.getAveragePressure(sourceType);
        
        double averageTemperature =
                repository.getAverageTemperature(sourceType);

        double averageFlowRate =
                repository.getAverageFlowRate(sourceType);
        
        long onlineDevices =
                repository.countOnlineReadings(sourceType);

        long totalReadings =
                repository.countTelemetryReadings(sourceType);
        
        long offlineDevices =
                totalReadings - onlineDevices;
        
        long leakDetectedCount =
                repository.countLeakDetectedReadings(sourceType);

        return DeviceAnalyticsResponseDto
                .builder()

                .totalConsumption(
                        totalConsumption)

                .averageConsumption(
                        averageConsumption)

                .peakConsumption(
                        peakConsumption)

                .averagePressure(
                        averagePressure)

                .averageTemperature(
                        averageTemperature)

                .averageFlowRate(
                        averageFlowRate)

                .totalReadings(
                        totalReadings)

                .forecastConsumption(
                        averageConsumption * 1.08)

                .leakagePercentage(

                        totalReadings == 0

                                ? 0

                                : (leakDetectedCount * 100.0)
                                / totalReadings)

                .averageBatteryLevel(
                        repository.getAverageBatteryLevel(
                                sourceType))

                .averagePipelineHealth(
                        repository.getAveragePipelineHealth(
                                sourceType))

                .averageSensorHealth(
                        repository.getAverageSensorHealth(
                                sourceType))

                .onlinePercentage(

                        totalReadings == 0

                                ? 0

                                : (onlineDevices * 100.0)
                                / totalReadings)

                .offlinePercentage(

                        totalReadings == 0

                                ? 0

                                : (offlineDevices * 100.0)
                                / totalReadings)

                .build();
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
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "telemetryDashboard", allEntries = true),
            @CacheEvict(value = "telemetryAnalytics", allEntries = true),
            @CacheEvict(value = "waterDashboard", allEntries = true)
    })
    public String archiveTelemetry(
            Long telemetryId,
            SourceType sourceType,
            String archiveReason) {

        User loggedInUser = getLoggedInUser();

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

        	WaterTelemetry telemetry =
        	        waterRepository.findById(telemetryId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Water telemetry not found"));

            ArchivedDeviceTelemetry archived =
            		 telemetryMapper.mapToArchivedTelemetry(
                            telemetry,
                            loggedInUser,
                            archiveReason);

            archivedTelemetryRepository.save(
                    archived);

            waterRepository.delete(
                    telemetry);

            return "Water telemetry archived successfully";
        }

        // ==========================================
        // GAS
        // ==========================================
        if (sourceType == SourceType.GAS) {

        	GasTelemetry telemetry =
        	        gasRepository.findById(telemetryId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Gas telemetry not found"));

            ArchivedDeviceTelemetry archived =
            		 telemetryMapper.mapToArchivedTelemetry(
                            telemetry,
                            loggedInUser,
                            archiveReason);

            archivedTelemetryRepository.save(
                    archived);

            gasRepository.delete(
                    telemetry);

            return "Gas telemetry archived successfully";
        }

        // ==========================================
        // ENERGY
        // ==========================================
        if (sourceType == SourceType.ENERGY) {

        	EnergyTelemetry telemetry =
        	        energyRepository.findById(telemetryId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Energy telemetry not found"));

            ArchivedDeviceTelemetry archived =
            		 telemetryMapper.mapToArchivedTelemetry(
                            telemetry,
                            loggedInUser,
                            archiveReason);

            archivedTelemetryRepository.save(
                    archived);

            energyRepository.delete(
                    telemetry);

            return "Energy telemetry archived successfully";
        }

        // ==========================================
        // SOLAR
        // ==========================================
        if (sourceType == SourceType.SOLAR) {

        	SolarTelemetry telemetry =
        	        solarRepository.findById(telemetryId)
        	                .orElseThrow(() ->
        	                        new ResourceNotFoundException(
        	                                "Solar telemetry not found"));

            ArchivedDeviceTelemetry archived =
            		 telemetryMapper.mapToArchivedTelemetry(
                            telemetry,
                            loggedInUser,
                            archiveReason);

            archivedTelemetryRepository.save(
                    archived);

            solarRepository.delete(
                    telemetry);

            return "Solar telemetry archived successfully";
        }

        throw new IllegalArgumentException(
                "Unsupported Source Type");
    }
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "telemetryDashboard", allEntries = true),
            @CacheEvict(value = "telemetryAnalytics", allEntries = true),
            @CacheEvict(value = "waterDashboard", allEntries = true)
    })
    public String restoreTelemetry(
            Long archivedTelemetryId) {

    	ArchivedDeviceTelemetry archivedTelemetry =
    	        archivedTelemetryRepository.findById(
    	                archivedTelemetryId)
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Archived telemetry not found"));

        SourceType sourceType =
                archivedTelemetry.getSourceType();

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

            WaterTelemetry telemetry =
            		telemetryMapper.mapToWaterTelemetry(
                            archivedTelemetry);

            waterRepository.save(
                    telemetry);

            archivedTelemetryRepository.delete(
                    archivedTelemetry);

            return "Water telemetry restored successfully";
        }

        // ==========================================
        // GAS
        // ==========================================
        if (sourceType == SourceType.GAS) {

            GasTelemetry telemetry =
            		telemetryMapper.mapToGasTelemetry(
                            archivedTelemetry);

            gasRepository.save(
                    telemetry);

            archivedTelemetryRepository.delete(
                    archivedTelemetry);

            return "Gas telemetry restored successfully";
        }

        // ==========================================
        // ENERGY
        // ==========================================
        if (sourceType == SourceType.ENERGY) {

            EnergyTelemetry telemetry =
            		telemetryMapper.mapToEnergyTelemetry(
                            archivedTelemetry);

            energyRepository.save(
                    telemetry);

            archivedTelemetryRepository.delete(
                    archivedTelemetry);
            
            return "Energy telemetry restored successfully";

        }

        // ==========================================
        // SOLAR
        // ==========================================
        if (sourceType == SourceType.SOLAR) {

            SolarTelemetry telemetry =
            		telemetryMapper.mapToSolarTelemetry(
                            archivedTelemetry);

            solarRepository.save(
                    telemetry);

            archivedTelemetryRepository.delete(
                    archivedTelemetry);

            return "Solar telemetry restored successfully";
        }

        throw new IllegalArgumentException(
                "Unsupported Source Type");
    }
   
    @Transactional(readOnly = true)
    public DashboardChartResponseDto getHourlyConsumptionChart(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusHours(24);

        List<DeviceTelemetry> telemetry =
                sourceType == null
                        ? repository.findByReadingTimeAfter(from)
                        : repository.findBySourceTypeAndReadingTimeAfter(
                                sourceType,
                                from);

        List<String> labels = new ArrayList<>();

        List<Double> values = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm");

        telemetry.stream()

                .sorted((a, b) ->
                        a.getReadingTime()
                                .compareTo(b.getReadingTime()))

                .forEach(t -> {

                    labels.add(
                            t.getReadingTime()
                                    .format(formatter));

                    values.add(
                            t.getConsumption() == null
                                    ? 0
                                    : t.getConsumption());
                });

        return DashboardChartResponseDto.builder()

                .labels(labels)

                .values(values)

                .build();
    }
   
    @Transactional(readOnly = true)
    private DashboardChartResponseDto getDailyConsumptionChart(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusDays(30);

        Map<LocalDate, Double> dailyData = new LinkedHashMap<>();

        // ==========================================
        // WATER MODULE
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (WaterTelemetry t : telemetry) {

                LocalDate date = t.getReadingTime().toLocalDate();

                dailyData.merge(
                        date,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // GAS MODULE
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (GasTelemetry t : telemetry) {

                LocalDate date = t.getReadingTime().toLocalDate();

                dailyData.merge(
                        date,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // GENERIC MODULE (Energy / Solar)
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            for (DeviceTelemetry t : telemetry) {

                LocalDate date = t.getReadingTime().toLocalDate();

                dailyData.merge(
                        date,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        return DashboardChartResponseDto.builder()

                .labels(
                        dailyData.keySet()
                                .stream()
                                .map(LocalDate::toString)
                                .toList())

                .values(
                        new ArrayList<>(dailyData.values()))

                .build();
    }
   
    @Transactional(readOnly =true)
    private DashboardChartResponseDto getWeeklyConsumptionChart(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusWeeks(12);

        Map<String, Double> weeklyData =
                new LinkedHashMap<>();

        WeekFields weekFields =
                WeekFields.ISO;

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (WaterTelemetry t : telemetry) {

                LocalDate date =
                        t.getReadingTime().toLocalDate();

                String week =
                        date.getYear()
                                + "-W"
                                + date.get(weekFields.weekOfWeekBasedYear());

                weeklyData.merge(
                        week,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // GAS
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (GasTelemetry t : telemetry) {

                LocalDate date =
                        t.getReadingTime().toLocalDate();

                String week =
                        date.getYear()
                                + "-W"
                                + date.get(weekFields.weekOfWeekBasedYear());

                weeklyData.merge(
                        week,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // ENERGY / SOLAR
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            for (DeviceTelemetry t : telemetry) {

                LocalDate date =
                        t.getReadingTime().toLocalDate();

                String week =
                        date.getYear()
                                + "-W"
                                + date.get(weekFields.weekOfWeekBasedYear());

                weeklyData.merge(
                        week,
                        t.getConsumption() == null ? 0 : t.getConsumption(),
                        Double::sum);
            }
        }

        return DashboardChartResponseDto.builder()

                .labels(new ArrayList<>(weeklyData.keySet()))

                .values(new ArrayList<>(weeklyData.values()))

                .build();
    }
  
    @Transactional(readOnly = true)
    private DashboardChartResponseDto getMonthlyConsumptionChart(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusMonths(12);

        Map<String, Double> monthlyData =
                new LinkedHashMap<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM");

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (WaterTelemetry t : telemetry) {

                String month =
                        t.getReadingTime().format(formatter);

                monthlyData.merge(
                        month,
                        t.getConsumption() == null
                                ? 0
                                : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // GAS
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            for (GasTelemetry t : telemetry) {

                String month =
                        t.getReadingTime().format(formatter);

                monthlyData.merge(
                        month,
                        t.getConsumption() == null
                                ? 0
                                : t.getConsumption(),
                        Double::sum);
            }
        }

        // ==========================================
        // ENERGY / SOLAR
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            for (DeviceTelemetry t : telemetry) {

                String month =
                        t.getReadingTime().format(formatter);

                monthlyData.merge(
                        month,
                        t.getConsumption() == null
                                ? 0
                                : t.getConsumption(),
                        Double::sum);
            }
        }

        return DashboardChartResponseDto.builder()

                .labels(
                        new ArrayList<>(monthlyData.keySet()))

                .values(
                        new ArrayList<>(monthlyData.values()))

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public DashboardChartResponseDto getFlowRateTrend(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusDays(30);

        List<String> labels = new ArrayList<>();

        List<Double> values = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM-dd");

        // ==========================================
        // WATER MODULE
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getFlowRate() == null
                                ? 0
                                : t.getFlowRate());
            });
        }

        // ==========================================
        // GAS MODULE
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getFlowRate() == null
                                ? 0
                                : t.getFlowRate());
            });
        }

        // ==========================================
        // GENERIC MODULE (Energy / Solar)
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getFlowRate() == null
                                ? 0
                                : t.getFlowRate());
            });
        }

        return DashboardChartResponseDto.builder()

                .labels(labels)

                .values(values)

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public DashboardChartResponseDto getPressureTrend(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusDays(30);

        List<String> labels = new ArrayList<>();

        List<Double> values = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM-dd");

        // ==========================================
        // WATER MODULE
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime()
                                .format(formatter));

                values.add(
                        t.getPressure() == null
                                ? 0
                                : t.getPressure());
            });
        }

        // ==========================================
        // GAS MODULE
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime()
                                .format(formatter));

                values.add(
                        t.getPressure() == null
                                ? 0
                                : t.getPressure());
            });
        }

        // ==========================================
        // GENERIC MODULE (Energy / Solar)
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime()
                                .format(formatter));

                values.add(
                        t.getPressure() == null
                                ? 0
                                : t.getPressure());
            });
        }

        return DashboardChartResponseDto.builder()

                .labels(labels)

                .values(values)

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public DashboardChartResponseDto getSignalTrend(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusDays(30);

        List<String> labels =
                new ArrayList<>();

        List<Double> values =
                new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM-dd");

        // ==========================================
        // WATER
        // ==========================================
        if (sourceType == SourceType.WATER) {

            List<WaterTelemetry> telemetry =
                    waterRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getSignalStrength() == null
                                ? 0.0
                                : t.getSignalStrength().doubleValue());
            });
        }

        // ==========================================
        // GAS
        // ==========================================
        else if (sourceType == SourceType.GAS) {

            List<GasTelemetry> telemetry =
                    gasRepository.findByReadingTimeAfterOrderByReadingTimeAsc(from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getSignalStrength() == null
                                ? 0.0
                                : t.getSignalStrength().doubleValue());
            });
        }

        // ==========================================
        // ENERGY / SOLAR
        // ==========================================
        else {

            List<DeviceTelemetry> telemetry =
                    sourceType == null
                            ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                            : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                    sourceType,
                                    from);

            telemetry.forEach(t -> {

                labels.add(
                        t.getReadingTime().format(formatter));

                values.add(
                        t.getSignalStrength() == null
                                ? 0.0
                                : t.getSignalStrength().doubleValue());
            });
        }

        return DashboardChartResponseDto.builder()
                .labels(labels)
                .values(values)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public byte[] exportTelemetry(
            String format,
            SourceType sourceType,
            String reportType) {

    	List<DeviceTelemetry> telemetryList =
    	        sourceType == null
    	                ? repository.findAll()
    	                : repository.findBySourceType(
    	                        sourceType);

    	LocalDateTime now =
    	        LocalDateTime.now();

    	switch (reportType.toUpperCase()) {

    	    case "DAILY":

    	        telemetryList =
    	                telemetryList.stream()
    	                        .filter(t ->

    	                                t.getReadingTime() != null

    	                                &&

    	                                t.getReadingTime()
    	                                        .toLocalDate()
    	                                        .equals(
    	                                                now.toLocalDate()))
    	                        .toList();

    	        break;

    	    case "MONTHLY":

    	        telemetryList =
    	                telemetryList.stream()
    	                        .filter(t ->

    	                                t.getReadingTime() != null

    	                                &&

    	                                t.getReadingTime()
    	                                        .getMonthValue()

    	                                        ==

    	                                        now.getMonthValue()

    	                                &&

    	                                t.getReadingTime()
    	                                        .getYear()

    	                                        ==

    	                                        now.getYear())
    	                        .toList();

    	        break;

    	    case "CONSUMPTION":

    	        telemetryList =
    	                telemetryList.stream()
    	                        .filter(t ->

    	                                t.getConsumption() != null

    	                                &&

    	                                t.getConsumption() > 0)
    	                        .toList();

    	        break;
default:
    	        throw new IllegalArgumentException(
    	                "Invalid report type. Supported values are ALL, DAILY, MONTHLY, CONSUMPTION");
    	       
    	}

        switch (format.toLowerCase()) {

            case "excel":
                return exportExcel(
                        telemetryList);

            case "pdf":
                return exportPdf(
                        telemetryList);

            case "csv":
            default:
                return exportCsv(
                        telemetryList);
        }
    }
    private byte[] exportCsv(
            List<DeviceTelemetry> telemetryList){

        

        StringBuilder csv =
                new StringBuilder();

        csv.append(
                "Id,DeviceId,SourceType,FlowRate,Pressure,Temperature,Consumption,"
                + "TotalFlow,GasConcentration,GasDensity,GasQuality,"
                + "LeakSeverity,AlarmActive,EmergencyShutdown,SignalStrength,"
                + "Online,LeakDetected,BatteryLevel,ValveStatus,"
                + "PipelineHealth,SensorHealth,Status,ReadingTime\n");

        for (DeviceTelemetry telemetry : telemetryList) {

            csv.append(telemetry.getId()).append(",");
            csv.append(telemetry.getDeviceId()).append(",");
            csv.append(telemetry.getSourceType()).append(",");
            csv.append(telemetry.getFlowRate()).append(",");
            csv.append(telemetry.getPressure()).append(",");
            csv.append(telemetry.getTemperature()).append(",");
            csv.append(telemetry.getConsumption()).append(",");
            csv.append(telemetry.getTotalFlow()).append(",");
            csv.append(telemetry.getGasConcentration()).append(",");
            csv.append(telemetry.getGasDensity()).append(",");
            csv.append(telemetry.getGasQuality()).append(",");
            csv.append(telemetry.getLeakSeverity()).append(",");
            csv.append(telemetry.getAlarmActive()).append(",");
            csv.append(telemetry.getEmergencyShutdown()).append(",");
            csv.append(telemetry.getSignalStrength()).append(",");
            csv.append(telemetry.getDeviceOnline()).append(",");
            csv.append(telemetry.getLeakDetected()).append(",");
            csv.append(telemetry.getBatteryLevel()).append(",");
            csv.append(telemetry.getValveStatus()).append(",");
            csv.append(telemetry.getPipelineHealthScore()).append(",");
            csv.append(telemetry.getSensorHealthScore()).append(",");
            csv.append(telemetry.getStatus()).append(",");
            csv.append(telemetry.getReadingTime()).append("\n");
        }

        return csv.toString().getBytes(
                java.nio.charset.StandardCharsets.UTF_8);
    }
    private byte[] exportExcel(
            List<DeviceTelemetry> telemetryList) {

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {

            var sheet =
                    workbook.createSheet(
                            "Telemetry");

            Row header =
                    sheet.createRow(0);

            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("Device Id");
            header.createCell(2).setCellValue("Source");
            header.createCell(3).setCellValue("Flow Rate");
            header.createCell(4).setCellValue("Pressure");
            header.createCell(5).setCellValue("Temperature");
            header.createCell(6).setCellValue("Consumption");
            header.createCell(7).setCellValue("Online");
            header.createCell(8).setCellValue("Leak");
            header.createCell(9).setCellValue("Battery");
            header.createCell(10).setCellValue("Valve");
            header.createCell(11).setCellValue("Pipeline");
            header.createCell(12).setCellValue("Sensor");
            header.createCell(13).setCellValue("Status");
            header.createCell(14).setCellValue("Reading Time");
            header.createCell(15).setCellValue("Total Flow");
            header.createCell(16).setCellValue("Gas Concentration");
            header.createCell(17).setCellValue("Gas Density");
            header.createCell(18).setCellValue("Gas Quality");
            header.createCell(19).setCellValue("Leak Severity");
            header.createCell(20).setCellValue("Alarm Active");
            header.createCell(21).setCellValue("Emergency Shutdown");
            header.createCell(22).setCellValue("Signal Strength");

            

            int rowNum = 1;

            for (DeviceTelemetry telemetry : telemetryList) {

                Row row =
                        sheet.createRow(
                                rowNum++);

                row.createCell(0)
                        .setCellValue(
                                telemetry.getId());

                row.createCell(1)
                        .setCellValue(
                                telemetry.getDeviceId());

                row.createCell(2)
                        .setCellValue(
                                telemetry.getSourceType().name());

                row.createCell(3)
                        .setCellValue(
                                telemetry.getFlowRate());

                row.createCell(4)
                        .setCellValue(
                                telemetry.getPressure());

                row.createCell(5)
                        .setCellValue(
                                telemetry.getTemperature());

                row.createCell(6)
                        .setCellValue(
                                telemetry.getConsumption());

                row.createCell(7)
                        .setCellValue(
                                telemetry.getDeviceOnline());

                row.createCell(8)
                        .setCellValue(
                                telemetry.getLeakDetected());
                
                row.createCell(9)
                .setCellValue(
                        telemetry.getBatteryLevel());

        row.createCell(10)
                .setCellValue(
                        telemetry.getValveStatus());

        row.createCell(11)
                .setCellValue(
                        telemetry.getPipelineHealthScore());

        row.createCell(12)
                .setCellValue(
                        telemetry.getSensorHealthScore());

        row.createCell(13)
                .setCellValue(
                        telemetry.getStatus());

        row.createCell(14)
        .setCellValue(
                telemetry.getReadingTime() != null
                        ? telemetry.getReadingTime().toString()
                        : "");
        
        row.createCell(15)
        .setCellValue(
                telemetry.getTotalFlow() != null
                        ? telemetry.getTotalFlow()
                        : 0);

row.createCell(16)
        .setCellValue(
                telemetry.getGasConcentration() != null
                        ? telemetry.getGasConcentration()
                        : 0);

row.createCell(17)
        .setCellValue(
                telemetry.getGasDensity() != null
                        ? telemetry.getGasDensity()
                        : 0);

row.createCell(18)
        .setCellValue(
                telemetry.getGasQuality() != null
                        ? telemetry.getGasQuality()
                        : "");

row.createCell(19)
        .setCellValue(
                telemetry.getLeakSeverity() != null
                        ? telemetry.getLeakSeverity()
                        : "");

row.createCell(20)
        .setCellValue(
                Boolean.TRUE.equals(
                        telemetry.getAlarmActive()));

row.createCell(21)
        .setCellValue(
                Boolean.TRUE.equals(
                        telemetry.getEmergencyShutdown()));

row.createCell(22)
        .setCellValue(
                telemetry.getSignalStrength() != null
                        ? telemetry.getSignalStrength()
                        : 0);
            }

            for (int i = 0; i < 23; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Excel export failed", e);
        }
    }
    private byte[] exportPdf(
            List<DeviceTelemetry> telemetryList) {

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
                            "Device Telemetry Report"));

            document.add(
                    new Paragraph(" "));

           

            for (DeviceTelemetry telemetry : telemetryList) {

                document.add(

                        new Paragraph(

                                "Device : "
                                        + telemetry.getDeviceId()

                                        + " | Source : "
                                        + telemetry.getSourceType()

                                        + " | Consumption : "
                                        + telemetry.getConsumption()
                                        
                                        + " | Total Flow : "
                                        + telemetry.getTotalFlow()

                                        + " | Gas Concentration : "
                                        + telemetry.getGasConcentration()

                                        + " | Gas Density : "
                                        + telemetry.getGasDensity()

                                        + " | Gas Quality : "
                                        + telemetry.getGasQuality()

                                        + " | Leak Severity : "
                                        + telemetry.getLeakSeverity()

                                        + " | Alarm : "
                                        + telemetry.getAlarmActive()

                                        + " | Emergency Shutdown : "
                                        + telemetry.getEmergencyShutdown()

                                        + " | Signal : "
                                        + telemetry.getSignalStrength()

                                        + " | Pressure : "
                                        + telemetry.getPressure()

                                        + " | Online : "
                                        + telemetry.getDeviceOnline()

                                        + " | Leak : "
                                        + telemetry.getLeakDetected()

                                        + " | Battery : "
                                        + telemetry.getBatteryLevel()

                                        + " | Valve : "
                                        + telemetry.getValveStatus()

                                        + " | Pipeline : "
                                        + telemetry.getPipelineHealthScore()

                                        + " | Sensor : "
                                        + telemetry.getSensorHealthScore()

                                        + " | Status : "
                                        + telemetry.getStatus()

                                        + " | Time : "
                                        + telemetry.getReadingTime()));
            }

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "PDF export failed",
                    e);
        }
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("waterSummary")
    public WaterSummaryResponseDto getSummary() {

    	List<WaterTelemetry> list = waterRepository.findAll();

        return WaterSummaryResponseDto.builder()

                .todayConsumption(
                        list.stream()
                                .mapToDouble(t -> t.getConsumption() == null ? 0 : t.getConsumption())
                                .sum())

                .yesterdayConsumption(0.0)

                .monthlyConsumption(
                        list.stream()
                                .mapToDouble(t -> t.getConsumption() == null ? 0 : t.getConsumption())
                                .sum())

                .averageDailyConsumption(
                        list.stream()
                                .mapToDouble(t -> t.getConsumption() == null ? 0 : t.getConsumption())
                                .average()
                                .orElse(0))

                .peakFlowRate(
                        list.stream()
                                .mapToDouble(t -> t.getFlowRate() == null ? 0 : t.getFlowRate())
                                .max()
                                .orElse(0))

                .averageFlowRate(
                        list.stream()
                                .mapToDouble(t -> t.getFlowRate() == null ? 0 : t.getFlowRate())
                                .average()
                                .orElse(0))

                .averagePressure(
                        list.stream()
                                .mapToDouble(t -> t.getPressure() == null ? 0 : t.getPressure())
                                .average()
                                .orElse(0))

                .averageBatteryLevel(
                        list.stream()
                                .mapToDouble(t -> t.getBatteryLevel() == null ? 0 : t.getBatteryLevel())
                                .average()
                                .orElse(0))

                .averageSignalStrength(
                        list.stream()
                                .mapToInt(t -> t.getSignalStrength() == null ? 0 : t.getSignalStrength())
                                .average()
                                .orElse(0))

                .activeDevices(waterRepository.countByDeviceOnline(true))

                .offlineDevices(waterRepository.countByDeviceOnline(false))

                .leaksDetected(waterRepository.countByLeakDetected(true))

                .activeAlerts(0L)

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<WaterLiveTelemetryResponseDto> getLiveTelemetry() {

    	return waterRepository.findTop20ByOrderByReadingTimeDesc()
                .stream()
                .map(this::mapToLiveTelemetry)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("waterAnalytics")
    public WaterAnalyticsResponseDto getAnalytics() {

        return mapAnalytics(
                waterRepository.findAll());
    } 
   
    @Override
    @Transactional(readOnly = true)
    public List<WaterHistoryResponseDto> getHistory(
            String deviceId) {

        return waterRepository.findByDeviceIdOrderByReadingTimeAsc(deviceId)
                .stream()
                .map(this::mapToHistory)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public WaterQualityResponseDto getQuality(
            String deviceId) {
    	WaterTelemetry telemetry =
    	        waterRepository
    	                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Water telemetry not found"));

       

        return WaterQualityResponseDto.builder()

                .deviceId(telemetry.getDeviceId())

                .pressure(telemetry.getPressure())

                .temperature(telemetry.getTemperature())
                
                .ph(
                        telemetry.getPh())

                .tds(
                        telemetry.getTds())

                .turbidity(
                        telemetry.getTurbidity())

                .conductivity(
                        telemetry.getConductivity())

                .dissolvedOxygen(
                        telemetry.getDissolvedOxygen())

                .chlorineLevel(
                        telemetry.getChlorineLevel())

                .flowRate(telemetry.getFlowRate())

                .consumption(telemetry.getConsumption())

                .pipelineHealthScore(
                        telemetry.getPipelineHealthScore())

                .sensorHealthScore(
                        telemetry.getSensorHealthScore())

                .leakDetected(
                        telemetry.getLeakDetected())

                .tamperDetected(
                        telemetry.getTamperDetected())

                .qualityStatus(
                        telemetry.getStatus())

                .readingTime(
                        telemetry.getReadingTime())
                
                
               

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<TankLevelResponseDto> getTankLevels() {

        return waterRepository.findTop20ByOrderByReadingTimeDesc()

                .stream()

                .map(water -> {

                    Double currentLevel =
                            water.getConsumption() == null ? 0.0 : water.getConsumption();

                    Double maximumCapacity = 100.0;

                    Double availableCapacity =
                            maximumCapacity - currentLevel;

                    Double percentageFilled =
                            (currentLevel / maximumCapacity) * 100;

                    Integer healthScore =
                            water.getPipelineHealthScore() == null
                                    ? 100
                                    : water.getPipelineHealthScore().intValue();

                    return TankLevelResponseDto.builder()

                            .deviceId(water.getDeviceId())

                            .deviceName("Water Meter")

                            .tankName("Main Tank")

                            .currentLevel(currentLevel)

                            .maximumCapacity(maximumCapacity)

                            .minimumCapacity(0.0)

                            .availableCapacity(availableCapacity)

                            .percentageFilled(percentageFilled)

                            .overflow(percentageFilled >= 95)

                            .lowLevel(percentageFilled <= 30)

                            .criticalLevel(percentageFilled <= 10)

                            .inflowRate(water.getFlowRate())

                            .outflowRate(water.getFlowRate())

                            .dailyConsumption(currentLevel)

                            .healthScore(healthScore)

                            .recommendation(
                                    percentageFilled <= 10
                                            ? "Immediate refill required"
                                            : percentageFilled <= 30
                                                    ? "Tank level is low"
                                                    : percentageFilled >= 95
                                                            ? "Tank is almost full"
                                                            : "Tank operating normally")

                            .status(water.getStatus())

                            .readingTime(water.getReadingTime())

                            .build();
                })

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<PumpStatusResponseDto> getPumpStatus() {

        return waterRepository.findTop20ByOrderByReadingTimeDesc()

                .stream()

                .map(water -> PumpStatusResponseDto.builder()

                        .deviceId(
                                water.getDeviceId())

                        .deviceName(
                                "Water Meter")

                        .pumpName(
                                "Main Pump")

                        .running(
                                Boolean.TRUE.equals(
                                        water.getDeviceOnline()))

                        .status(
                                water.getStatus())

                        .pressure(
                                water.getPressure())

                        .flowRate(
                                water.getFlowRate())

                        .powerConsumption(
                                water.getCurrent())

                        .runtimeHours(
                                water.getRuntimeHours())

                        .lastStartedAt(
                                water.getLastStartedAt())

                        .lastStoppedAt(
                                water.getLastStoppedAt())

                        .updatedAt(
                                water.getReadingTime())

                        .build())

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<ValveStatusResponseDto> getValveStatus() {

        return waterRepository.findTop20ByOrderByReadingTimeDesc()

                .stream()

                .map(water -> ValveStatusResponseDto.builder()

                        .deviceId(
                                water.getDeviceId())

                        .valveStatus(
                                water.getValveStatus())

                        .open(
                                "OPEN".equalsIgnoreCase(
                                        water.getValveStatus()))

                        .updatedAt(
                                water.getReadingTime())

                        .build())

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<LeakSummaryResponseDto> getLeaks() {

        return waterRepository.findByLeakDetected(true)

                .stream()

                .map(water -> {

                    Double pressure =
                            water.getPressure() == null ? 0.0 : water.getPressure();

                    String severity =
                            pressure < 20
                                    ? "CRITICAL"
                                    : pressure < 40
                                    ? "HIGH"
                                    : pressure < 60
                                    ? "MEDIUM"
                                    : "LOW";

                    Integer leakScore =
                            severity.equals("CRITICAL") ? 100
                            : severity.equals("HIGH") ? 80
                            : severity.equals("MEDIUM") ? 60
                            : 30;

                    return LeakSummaryResponseDto.builder()

                            .deviceId(
                                    water.getDeviceId())

                            .deviceName(
                                    "Water Meter")

                            .leakDetected(
                                    true)

                            .severity(
                                    severity)

                            .location(
                                    water.getLeakLocation())

                            .pressure(
                                    pressure)

                            .flowRate(
                                    water.getFlowRate())

                            .estimatedLoss(
                                    water.getEstimatedWaterLoss())

                            .criticalLeak(
                                    "CRITICAL".equals(severity))

                            .leakScore(
                                    leakScore)

                            .recommendation(
                                    "CRITICAL".equals(severity)
                                            ? "Immediate inspection required"
                                            : "Monitor leak condition")

                            .status(
                                    water.getStatus())

                            .detectedAt(
                                    water.getReadingTime())

                            .build();
                })

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceLogResponseDto> getLogs(
            String deviceId) {

        return waterRepository
                .findByDeviceIdOrderByReadingTimeAsc(deviceId)

                .stream()

                .map(t -> DeviceLogResponseDto.builder()

                        .id(t.getId())

                        .deviceId(t.getDeviceId())

                        .logLevel(t.getStatus())

                        .message(
                                "Telemetry received")

                        .generatedBy(
                                "SYSTEM")

                        .createdAt(
                                t.getReadingTime())

                        .build())

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("waterDashboard")
    public WaterDashboardResponseDto getDashboard() {

        List<WaterTelemetry> list = waterRepository.findAll();
        
        long activeInstallations =
                installationRepository.countByStatus(
                        InstallationStatus.IN_PROGRESS);

        long activeServiceEngineers =
                userRepository.countByAvailabilityStatus(
                        EngineerAvailabilityStatus.AVAILABLE);

        long activeMaintenance =
                maintenanceRepository.countByStatus(
                        MaintenanceStatus.IN_PROGRESS);

        long openIssues =
                issueRepository.countByStatus(
                        IssueStatus.OPEN);
        long pendingOperations =
                deviceOperationRepository.countBySourceTypeAndStatus(
                        SourceType.WATER,
                        STATUS_PENDING);

        long acknowledgedOperations =
                deviceOperationRepository.countBySourceTypeAndStatus(
                        SourceType.WATER,
                        STATUS_ACKNOWLEDGED);

        long resolvedOperations =
                deviceOperationRepository.countBySourceTypeAndStatus(
                        SourceType.WATER,
                        STATUS_RESOLVED);

        return WaterDashboardResponseDto.builder()

                .totalDevices(
                        (long) list.stream()
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .onlineDevices(
                        waterRepository.countByDeviceOnline(true))

                .offlineDevices(
                        waterRepository.countByDeviceOnline(false))

                .todayConsumption(
                        list.stream()
                                .mapToDouble(w -> w.getConsumption() == null ? 0 : w.getConsumption())
                                .sum())

                .averageFlowRate(
                        list.stream()
                                .mapToDouble(w -> w.getFlowRate() == null ? 0 : w.getFlowRate())
                                .average()
                                .orElse(0))

                .averagePressure(
                        list.stream()
                                .mapToDouble(w -> w.getPressure() == null ? 0 : w.getPressure())
                                .average()
                                .orElse(0))

                .leakDetectedDevices(
                        waterRepository.countByLeakDetected(true))

                .activeInstallations(
                        activeInstallations)

                .activeServiceEngineers(
                        activeServiceEngineers)

                .activeMaintenance(
                        activeMaintenance)

                .openIssues(
                        openIssues)
                
                .pendingOperations(pendingOperations)

                .acknowledgedOperations(acknowledgedOperations)

                .resolvedOperations(resolvedOperations)

                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterLiveTelemetryResponseDto getLiveTelemetry(
            String deviceId) {

    	WaterTelemetry telemetry =
    	        waterRepository
    	                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Water telemetry not found"));

        return mapToLiveTelemetry(
                telemetry);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterAnalyticsResponseDto getAnalytics(
            String deviceId) {

        List<WaterTelemetry> telemetry =
                waterRepository
                        .findByDeviceIdOrderByReadingTimeAsc(deviceId);

        return mapAnalytics(
                telemetry);
    }
    private WaterLiveTelemetryResponseDto mapToLiveTelemetry(
            WaterTelemetry telemetry) {

        return WaterLiveTelemetryResponseDto.builder()

                .deviceId(telemetry.getDeviceId())

                .flowRate(telemetry.getFlowRate())

                .pressure(telemetry.getPressure())

                .consumption(telemetry.getConsumption())

                .batteryLevel(telemetry.getBatteryLevel())

                .signalStrength(telemetry.getSignalStrength())

                .temperature(telemetry.getTemperature())

                .voltage(null)

                .current(null)

                .tamperDetected(telemetry.getTamperDetected())

                .leakDetected(telemetry.getLeakDetected())

                .valveStatus(telemetry.getValveStatus())

                .deviceOnline(telemetry.getDeviceOnline())

                .readingTime(telemetry.getReadingTime())

                .build();
    }
    private WaterHistoryResponseDto mapToHistory(
            WaterTelemetry telemetry) {

        return WaterHistoryResponseDto.builder()

                .id(telemetry.getId())

                .deviceId(telemetry.getDeviceId())

                .flowRate(telemetry.getFlowRate())

                .pressure(telemetry.getPressure())

                .consumption(telemetry.getConsumption())

                .batteryLevel(telemetry.getBatteryLevel())

                .signalStrength(telemetry.getSignalStrength())

                .temperature(telemetry.getTemperature())

                .voltage(null)

                .current(null)

                .leakDetected(telemetry.getLeakDetected())

                .tamperDetected(telemetry.getTamperDetected())

                .valveStatus(telemetry.getValveStatus())

                .readingTime(telemetry.getReadingTime())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("gasDashboard")
    public GasDashboardResponseDto getGasDashboard() {

        List<GasTelemetry> list =
                gasRepository.findAll();

        long totalDevices =
                list.stream()
                        .map(GasTelemetry::getDeviceId)
                        .distinct()
                        .count();

        long onlineDevices =
                list.stream()
                        .filter(t -> Boolean.TRUE.equals(t.getDeviceOnline()))
                        .map(GasTelemetry::getDeviceId)
                        .distinct()
                        .count();

        long offlineDevices =
                totalDevices - onlineDevices;

        long activeLeaks =
                list.stream()
                        .filter(t -> Boolean.TRUE.equals(t.getLeakDetected()))
                        .map(GasTelemetry::getDeviceId)
                        .distinct()
                        .count();

        long activeAlarms =
                list.stream()
                        .filter(t -> Boolean.TRUE.equals(t.getAlarmActive()))
                        .map(GasTelemetry::getDeviceId)
                        .distinct()
                        .count();

        long emergencyShutdownCount =
                list.stream()
                        .filter(t -> Boolean.TRUE.equals(t.getEmergencyShutdown()))
                        .map(GasTelemetry::getDeviceId)
                        .distinct()
                        .count();
        
        long connectedPipelines =
                deviceRepository.countBySourceType(SourceType.GAS);

        double totalConsumption =
                list.stream()
                        .mapToDouble(t -> t.getConsumption() == null ? 0 : t.getConsumption())
                        .sum();
        
        double todayConsumption =
                list.stream()
                        .filter(t -> t.getReadingTime() != null)
                        .filter(t -> t.getReadingTime().toLocalDate().equals(LocalDate.now()))
                        .mapToDouble(t -> t.getConsumption() == null ? 0 : t.getConsumption())
                        .sum();

        double totalFlow =
                list.stream()
                        .mapToDouble(t -> t.getFlowRate() == null ? 0 : t.getFlowRate())
                        .sum();

        double averagePressure =
                list.stream()
                        .mapToDouble(t -> t.getPressure() == null ? 0 : t.getPressure())
                        .average()
                        .orElse(0);

        double averageTemperature =
                list.stream()
                        .mapToDouble(t -> t.getTemperature() == null ? 0 : t.getTemperature())
                        .average()
                        .orElse(0);

        double averageGasConcentration =
                list.stream()
                        .mapToDouble(t -> t.getGasConcentration() == null ? 0 : t.getGasConcentration())
                        .average()
                        .orElse(0);

        double averageGasDensity =
                list.stream()
                        .mapToDouble(t -> t.getGasDensity() == null ? 0 : t.getGasDensity())
                        .average()
                        .orElse(0);
        
        long activeZones =
                deviceRepository.findBySourceType(SourceType.GAS)
                        .stream()
                        .map(Device::getZone)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();

        return GasDashboardResponseDto.builder()
                .totalDevices(totalDevices)
                .onlineDevices(onlineDevices)
                .offlineDevices(offlineDevices)
                .activeAlarms(activeAlarms)
                .activeLeaks(activeLeaks)
                .totalConsumption(totalConsumption)
                .todayConsumption(todayConsumption)
                .activeZones(activeZones)
                .connectedPipelines(connectedPipelines)
                .totalFlow(totalFlow)
                .averagePressure(averagePressure)
                .averageTemperature(averageTemperature)
                .averageGasConcentration(averageGasConcentration)
                .averageGasDensity(averageGasDensity)
                .emergencyShutdownCount(emergencyShutdownCount)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("gasSummary")
    public GasSummaryResponseDto getGasSummary() {

        List<GasTelemetry> telemetry =
                gasRepository.findAll();

        return GasSummaryResponseDto.builder()

                .totalDevices(
                        (long) telemetry.stream()
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .onlineDevices(
                        telemetry.stream()
                                .filter(t -> Boolean.TRUE.equals(t.getDeviceOnline()))
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .offlineDevices(
                        telemetry.stream()
                                .filter(t -> !Boolean.TRUE.equals(t.getDeviceOnline()))
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .totalConsumption(
                        telemetry.stream()
                                .mapToDouble(t -> t.getConsumption() != null
                                        ? t.getConsumption()
                                        : 0.0)
                                .sum())

                .totalFlow(
                        telemetry.stream()
                                .mapToDouble(t -> t.getFlowRate() != null
                                        ? t.getFlowRate()
                                        : 0.0)
                                .sum())

                .averagePressure(
                        telemetry.stream()
                                .mapToDouble(t -> t.getPressure() != null
                                        ? t.getPressure()
                                        : 0.0)
                                .average()
                                .orElse(0.0))

                .averageTemperature(
                        telemetry.stream()
                                .mapToDouble(t -> t.getTemperature() != null
                                        ? t.getTemperature()
                                        : 0.0)
                                .average()
                                .orElse(0.0))

                .averageGasConcentration(
                        telemetry.stream()
                                .mapToDouble(t -> t.getGasConcentration() != null
                                        ? t.getGasConcentration()
                                        : 0.0)
                                .average()
                                .orElse(0.0))

                .averageGasDensity(
                        telemetry.stream()
                                .mapToDouble(t -> t.getGasDensity() != null
                                        ? t.getGasDensity()
                                        : 0.0)
                                .average()
                                .orElse(0.0))

                .activeLeaks(
                        telemetry.stream()
                                .filter(t -> Boolean.TRUE.equals(t.getLeakDetected()))
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .activeAlarms(
                        telemetry.stream()
                                .filter(t -> Boolean.TRUE.equals(t.getAlarmActive()))
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .emergencyShutdownCount(
                        telemetry.stream()
                                .filter(t -> Boolean.TRUE.equals(t.getEmergencyShutdown()))
                                .map(GasTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<GasLiveTelemetryResponseDto> getGasLiveTelemetry() {

        return gasRepository.findAll()
                .stream()
                .map(telemetry -> GasLiveTelemetryResponseDto.builder()

                        .deviceId(telemetry.getDeviceId())

                        .pressure(telemetry.getPressure())

                        .flowRate(telemetry.getFlowRate())

                        .totalFlow(telemetry.getFlowRate())

                        .consumption(telemetry.getConsumption())

                        .gasConcentration(telemetry.getGasConcentration())

                        .gasDensity(telemetry.getGasDensity())

                        .gasQuality(telemetry.getGasQuality())

                        .temperature(telemetry.getTemperature())

                        .leakDetected(telemetry.getLeakDetected())

                        .emergencyShutdown(telemetry.getEmergencyShutdown())

                        .alarmActive(telemetry.getAlarmActive())

                        .deviceOnline(telemetry.getDeviceOnline())

                        .batteryLevel(telemetry.getBatteryLevel())

                        .readingTime(telemetry.getReadingTime())

                        .build())

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public GasLiveTelemetryResponseDto getGasLiveTelemetry(
            String deviceId) {

    	GasTelemetry telemetry =
    	        gasRepository
    	                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Gas telemetry not found for device: " + deviceId));

        return GasLiveTelemetryResponseDto.builder()

                .deviceId(telemetry.getDeviceId())

                .pressure(telemetry.getPressure())

                .flowRate(telemetry.getFlowRate())

                .totalFlow(telemetry.getFlowRate())

                .consumption(telemetry.getConsumption())

                .gasConcentration(telemetry.getGasConcentration())

                .gasDensity(telemetry.getGasDensity())

                .gasQuality(telemetry.getGasQuality())

                .temperature(telemetry.getTemperature())

                .leakDetected(telemetry.getLeakDetected())

                .emergencyShutdown(telemetry.getEmergencyShutdown())

                .alarmActive(telemetry.getAlarmActive())

                .deviceOnline(telemetry.getDeviceOnline())

                .batteryLevel(telemetry.getBatteryLevel())

                .readingTime(telemetry.getReadingTime())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<GasHistoryResponseDto> getGasHistory(
            String deviceId) {

        return gasRepository.findByDeviceId(deviceId)
                .stream()
                .sorted((t1, t2) ->
                        t1.getReadingTime().compareTo(
                                t2.getReadingTime()))
                .map(telemetry -> GasHistoryResponseDto.builder()

                        .deviceId(telemetry.getDeviceId())

                        .pressure(telemetry.getPressure())

                        .flowRate(telemetry.getFlowRate())

                        .consumption(telemetry.getConsumption())

                        .gasConcentration(
                                telemetry.getGasConcentration())

                        .leakDetected(
                                telemetry.getLeakDetected())

                        .status(
                                telemetry.getStatus())

                        .readingTime(
                                telemetry.getReadingTime())

                        .build())

                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public GasAnalyticsResponseDto getGasAnalytics() {

        return GasAnalyticsResponseDto.builder()
                .consumptionChart(getDailyConsumptionChart(SourceType.GAS))
                .pressureChart(getPressureTrend(SourceType.GAS))
                .flowChart(getFlowRateTrend(SourceType.GAS))
                .concentrationChart(buildGasConcentrationChart())
                .densityChart(buildGasDensityChart())
                .leakChart(buildGasLeakChart())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public GasAnalyticsResponseDto getGasAnalytics(
            String deviceId) {

    	  gasRepository
          .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
          .orElseThrow(() ->
                  new ResourceNotFoundException(
                          "Gas telemetry not found for device: " + deviceId));

        return GasAnalyticsResponseDto.builder()
                .consumptionChart(buildDeviceConsumptionChart(deviceId))
                .pressureChart(buildDevicePressureChart(deviceId))
                .flowChart(buildDeviceFlowChart(deviceId))
                .concentrationChart(buildDeviceGasConcentrationChart(deviceId))
                .densityChart(buildDeviceGasDensityChart(deviceId))
                .leakChart(buildDeviceGasLeakChart(deviceId))
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public GasQualityResponseDto getGasQuality(
            String deviceId) {

    	  GasTelemetry telemetry = gasRepository
    	            .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
    	            .orElseThrow(() ->
    	                    new ResourceNotFoundException(
    	                            "Gas quality data not found for device: " + deviceId));

        return GasQualityResponseDto.builder()
                .deviceId(telemetry.getDeviceId())
                .gasQuality(telemetry.getGasQuality())
                .gasDensity(telemetry.getGasDensity())
                .gasConcentration(telemetry.getGasConcentration())
                .pressure(telemetry.getPressure())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<GasLeakResponseDto> getGasLeaks() {

        return gasRepository.findByLeakDetected(true)
                .stream()
                .map(telemetry -> GasLeakResponseDto.builder()
                        .deviceId(telemetry.getDeviceId())
                        .leakDetected(telemetry.getLeakDetected())
                        .readingTime(telemetry.getReadingTime())
                        .build())
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<GasAlarmResponseDto> getGasAlarms() {

        return gasRepository.findByAlarmActive(true)
                .stream()
                .map(telemetry -> GasAlarmResponseDto.builder()
                        .deviceId(telemetry.getDeviceId())
                        .alarmActive(telemetry.getAlarmActive())
                        .emergencyShutdown(telemetry.getEmergencyShutdown())
                        .status(telemetry.getStatus())
                        .readingTime(telemetry.getReadingTime())
                        .build())
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeviceLogResponseDto> getGasLogs(
            String deviceId) {

        return gasRepository.findByDeviceId(deviceId)
                .stream()
                .sorted((t1, t2) ->
                        t1.getReadingTime().compareTo(
                                t2.getReadingTime()))
                .map(telemetry -> DeviceLogResponseDto.builder()

                        .deviceId(telemetry.getDeviceId())

                        .logLevel(telemetry.getStatus())

                        .message(
                                "Pressure: " + telemetry.getPressure()
                                + ", Flow Rate: " + telemetry.getFlowRate()
                                + ", Consumption: " + telemetry.getConsumption()
                                + ", Leak: " + telemetry.getLeakDetected()
                                + ", Alarm: " + telemetry.getAlarmActive())

                        .generatedBy("SYSTEM")

                        .createdAt(telemetry.getReadingTime())

                        .build())

                .toList();
    }
    private DashboardChartResponseDto buildGasConcentrationChart() {

        return 
        		DashboardChartResponseDto.builder()
                .labels(List.of())
                .values(List.of())
                .build();
    }
    private DashboardChartResponseDto buildGasDensityChart() {

        return DashboardChartResponseDto.builder()
              
                .labels(List.of())
                .values(List.of())
                .build();
    }
    private DashboardChartResponseDto buildGasLeakChart() {

        return DashboardChartResponseDto.builder()
                
                .labels(List.of("Leak", "Normal"))
                .values(List.of(0.0, 0.0))
                .build();
    }
    private DashboardChartResponseDto buildDevicePressureChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
             
                .labels(List.of())
                .values(List.of())
                .build();
    }
    private DashboardChartResponseDto buildDeviceFlowChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
    
                .labels(List.of())
                .values(List.of())
                .build();
    }
    private DashboardChartResponseDto buildDeviceGasConcentrationChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
                
                .labels(List.of())
                .values(List.of())
                .build();
    }
    private DashboardChartResponseDto buildDeviceGasLeakChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
                
                .labels(List.of("Leak", "Normal"))
                .values(List.of(0.0, 0.0))
                .build();
    }
    private DashboardChartResponseDto buildDeviceConsumptionChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
             
                .labels(new ArrayList<>())
                .values(new ArrayList<>())
                .build();
    }
    private DashboardChartResponseDto buildDeviceGasDensityChart(
            String deviceId) {

        return DashboardChartResponseDto.builder()
           
                .labels(new ArrayList<>())
                .values(new ArrayList<>())
                .build();
    }
    private WaterAnalyticsResponseDto mapAnalytics(
            List<WaterTelemetry> list) {

        return WaterAnalyticsResponseDto.builder()

                .totalConsumption(
                        list.stream()
                                .mapToDouble(w -> w.getConsumption() == null ? 0 : w.getConsumption())
                                .sum())

                .averageConsumption(
                        list.stream()
                                .mapToDouble(w -> w.getConsumption() == null ? 0 : w.getConsumption())
                                .average()
                                .orElse(0))

                .maximumConsumption(
                        list.stream()
                                .mapToDouble(w -> w.getConsumption() == null ? 0 : w.getConsumption())
                                .max()
                                .orElse(0))

                .minimumConsumption(
                        list.stream()
                                .mapToDouble(w -> w.getConsumption() == null ? 0 : w.getConsumption())
                                .min()
                                .orElse(0))

                .averageFlowRate(
                        list.stream()
                                .mapToDouble(w -> w.getFlowRate() == null ? 0 : w.getFlowRate())
                                .average()
                                .orElse(0))

                .averagePressure(
                        list.stream()
                                .mapToDouble(w -> w.getPressure() == null ? 0 : w.getPressure())
                                .average()
                                .orElse(0))

                .averageBatteryLevel(
                        list.stream()
                                .mapToDouble(w -> w.getBatteryLevel() == null ? 0 : w.getBatteryLevel())
                                .average()
                                .orElse(0))

                .averageSignalStrength(
                        list.stream()
                                .mapToInt(w -> w.getSignalStrength() == null ? 0 : w.getSignalStrength())
                                .average()
                                .orElse(0))

                .averageTemperature(
                        list.stream()
                                .mapToDouble(w -> w.getTemperature() == null ? 0 : w.getTemperature())
                                .average()
                                .orElse(0))

                .totalDevices(
                        (long) list.stream()
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .onlineDevices(
                        list.stream()
                                .filter(w -> Boolean.TRUE.equals(w.getDeviceOnline()))
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .offlineDevices(
                        list.stream()
                                .filter(w -> Boolean.FALSE.equals(w.getDeviceOnline()))
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .leakDetectedDevices(
                        list.stream()
                                .filter(w -> Boolean.TRUE.equals(w.getLeakDetected()))
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .lowPressureDevices(
                        list.stream()
                                .filter(w -> w.getPressure() != null && w.getPressure() < 20)
                                .map(WaterTelemetry::getDeviceId)
                                .distinct()
                                .count())

                .activeAlerts(0L)

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public DashboardChartResponseDto getConsumptionChart(
            SourceType sourceType,
            String period) {

        if (period == null || period.isBlank()) {
            period = "MONTHLY";
        }

        switch (period.toUpperCase()) {

            case "DAILY":
                return getDailyConsumptionChart(sourceType);

            case "WEEKLY":
                return getWeeklyConsumptionChart(sourceType);

            case "MONTHLY":
                return getMonthlyConsumptionChart(sourceType);

            case "YEARLY":
                return getYearlyConsumptionChart(sourceType);

            default:
                throw new BadRequestException(
                        "Invalid period. Supported values: DAILY, WEEKLY, MONTHLY, YEARLY");
        }
    }
  
    @Transactional(readOnly = true)
    private  DashboardChartResponseDto getYearlyConsumptionChart(
            SourceType sourceType) {

        LocalDateTime from =
                LocalDateTime.now().minusYears(5);

        List<DeviceTelemetry> telemetry =
                sourceType == null
                        ? repository.findByReadingTimeAfterOrderByReadingTimeAsc(from)
                        : repository.findBySourceTypeAndReadingTimeAfterOrderByReadingTimeAsc(
                                sourceType,
                                from);

        Map<String, Double> yearlyData =
                new LinkedHashMap<>();

        for (DeviceTelemetry t : telemetry) {

            String year =
                    String.valueOf(
                            t.getReadingTime().getYear());

            yearlyData.merge(
                    year,
                    t.getConsumption() == null
                            ? 0
                            : t.getConsumption(),
                    Double::sum);
        }

        return DashboardChartResponseDto.builder()

                .labels(
                        new ArrayList<>(
                                yearlyData.keySet()))

                .values(
                        new ArrayList<>(
                                yearlyData.values()))

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<TimelineResponseDto> getTimeline(
            String deviceId) {

        return repository
                .findByDeviceIdOrderByReadingTimeAsc(deviceId)
                .stream()
                .map(telemetry -> {

                    String event = "Telemetry Received";

                    if (Boolean.TRUE.equals(
                            telemetry.getLeakDetected())) {

                        event = "Leak Detected";

                    } else if (Boolean.TRUE.equals(
                            telemetry.getTamperDetected())) {

                        event = "Tamper Detected";

                    } else if (telemetry.getValveStatus() != null) {

                        event = "Valve " + telemetry.getValveStatus();
                    }

                    return TimelineResponseDto.builder()

                            .deviceId(
                                    telemetry.getDeviceId())

                            .event(
                                    event)

                            .status(
                                    telemetry.getStatus())

                            .timestamp(
                                    telemetry.getReadingTime())

                            .build();
                })
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto generateReport(
            ReportFilterRequestDto request) {

        long totalRecords = repository.findAll()
                .stream()

                .filter(t -> request.getDeviceId() == null
                        || request.getDeviceId().equals(
                                t.getDeviceId()))

                .filter(t -> request.getSourceType() == null
                        || request.getSourceType().equals(
                                t.getSourceType()))

                .filter(t -> request.getFromDate() == null
                        || !t.getReadingTime().toLocalDate()
                                .isBefore(request.getFromDate()))

                .filter(t -> request.getToDate() == null
                        || !t.getReadingTime().toLocalDate()
                                .isAfter(request.getToDate()))

                .count();

        return ReportResponseDto.builder()

                .reportName(
                        request.getReportType() + "_REPORT")

                .reportType(
                        request.getReportType())

                .generatedBy(
                        "SYSTEM")

                .generatedAt(
                        LocalDateTime.now().toString())

                .totalRecords(
                        totalRecords)

                .downloadUrl(
                        "/api/telemetry/export?format="
                                + request.getReportType())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable("deviceHealth")
    public DeviceHealthResponseDto getDeviceHealth(
            String deviceId) {

    	DeviceTelemetry telemetry =

    	        repository
    	                .findTopByDeviceIdOrderByReadingTimeDesc(
    	                        deviceId)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Device not found"));

        int score = 100;

        if (telemetry.getBatteryLevel() != null
                && telemetry.getBatteryLevel() < 20) {

            score -= 25;
        }

        if (telemetry.getSignalStrength() != null
                && telemetry.getSignalStrength() < 30) {

            score -= 25;
        }

        if (Boolean.FALSE.equals(
                telemetry.getDeviceOnline())) {

            score -= 30;
        }

        if (Boolean.TRUE.equals(
                telemetry.getLeakDetected())) {

            score -= 20;
        }

        if (score < 0) {
            score = 0;
        }

        String health;

        if (score >= 90) {

            health = "EXCELLENT";

        } else if (score >= 75) {

            health = "GOOD";

        } else if (score >= 50) {

            health = "FAIR";

        } else {

            health = "POOR";
        }

        String recommendation;

        switch (health) {

            case "EXCELLENT" ->
                    recommendation =
                            "Device is operating normally.";

            case "GOOD" ->
                    recommendation =
                            "Monitor device periodically.";

            case "FAIR" ->
                    recommendation =
                            "Maintenance recommended.";

            default ->
                    recommendation =
                            "Immediate inspection required.";
        }

        return DeviceHealthResponseDto.builder()

                .deviceId(
                        telemetry.getDeviceId())

                .batteryLevel(
                        telemetry.getBatteryLevel())

                .signalStrength(
                        telemetry.getSignalStrength())

                .temperature(
                        telemetry.getTemperature())

                .pressure(
                        telemetry.getPressure())

                .valveStatus(
                        telemetry.getValveStatus())

                .pumpStatus(
                        telemetry.getPumpStatus() != null
                                ? telemetry.getPumpStatus().name()
                                : null)

                .communicationStatus(
                        telemetry.getDeviceOnline())

                .firmwareVersion("N/A")

                .lastCommunication(
                        telemetry.getReadingTime())

                .healthScore(
                        score)

                .overallHealth(
                        health)

                .recommendation(
                        recommendation)

                .build();
    }
    @Override
    @Caching(evict = {

            @CacheEvict(
                    value = "deviceDashboard",
                    allEntries = true),

            @CacheEvict(
                    value = "deviceAnalytics",
                    allEntries = true),

            @CacheEvict(
                    value = "waterDashboard",
                    allEntries = true),

            @CacheEvict(
                    value = "waterSummary",
                    allEntries = true),

            @CacheEvict(
                    value = "waterAnalytics",
                    allEntries = true),

            @CacheEvict(
                    value = "gasDashboard",
                    allEntries = true),

            @CacheEvict(
                    value = "gasSummary",
                    allEntries = true)

    })
    public String refreshDashboard() {

        return "Dashboard refreshed successfully";
    }
    @Override
    public DeviceTelemetryResponseDto refreshDevice(

            String deviceId,

            SourceType sourceType) {

        return getLatestTelemetry(
                deviceId,
                sourceType);
    }
    @Override
    public DeviceTelemetryResponseDto retryDevice(

            String deviceId,

            SourceType sourceType) {

        return getLatestTelemetry(
                deviceId,
                sourceType);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MapLocationResponseDto> getDeviceLocations(
            SourceType sourceType) {

        return deviceRepository.findBySourceType(sourceType)
                .stream()
                .map(telemetryMapper::mapLocationResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MapLocationResponseDto> getPipelineLocations(
            SourceType sourceType) {

        return deviceRepository.findBySourceType(sourceType)
                .stream()
                .map(telemetryMapper::mapLocationResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MapLocationResponseDto> getZoneMonitoring(
            SourceType sourceType) {

        return deviceRepository.findBySourceType(sourceType)
                .stream()
                .map(telemetryMapper::mapLocationResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MapLocationResponseDto> getLeakLocations(
            SourceType sourceType) {

        if (sourceType != SourceType.WATER) {
            return List.of();
        }

        return waterRepository.findByLeakDetectedTrue()
                .stream()
                .map(WaterTelemetry::getDeviceId)
                .distinct()
                .map(deviceRepository::findByDeviceId)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(telemetryMapper::mapLocationResponse)
                .toList();
    }
    private WaterTelemetry getLatestWaterTelemetry(String deviceId) {
        return waterRepository
                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Water telemetry not found"));
    }

    private GasTelemetry getLatestGasTelemetry(String deviceId) {
        return gasRepository
                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Gas telemetry not found"));
    }

    private EnergyTelemetry getLatestEnergyTelemetry(String deviceId) {
        return energyRepository
                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Energy telemetry not found"));
    }

    private SolarTelemetry getLatestSolarTelemetry(String deviceId) {
        return solarRepository
                .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Solar telemetry not found"));
    }
    private DeviceTelemetryResponseDto
    mapToResponse(
            DeviceTelemetry telemetry) {

        return DeviceTelemetryResponseDto
                .builder()
                .id(
                        telemetry.getId())
                .deviceId(
                        telemetry.getDeviceId())
                .sourceType(
                        telemetry.getSourceType())
                .flowRate(
                        telemetry.getFlowRate())
                .pressure(
                        telemetry.getPressure())
                .temperature(
                        telemetry.getTemperature())
                .ph(
                        telemetry.getPh())

                .tds(
                        telemetry.getTds())

                .turbidity(
                        telemetry.getTurbidity())

                .conductivity(
                        telemetry.getConductivity())

                .dissolvedOxygen(
                        telemetry.getDissolvedOxygen())

                .chlorineLevel(
                        telemetry.getChlorineLevel())
                .consumption(
                        telemetry.getConsumption())
                .totalFlow(
                        telemetry.getTotalFlow())

                .gasConcentration(
                        telemetry.getGasConcentration())

                .gasDensity(
                        telemetry.getGasDensity())

                .gasQuality(
                        telemetry.getGasQuality())

                .leakSeverity(
                        telemetry.getLeakSeverity())

                .alarmActive(
                        telemetry.getAlarmActive())

                .emergencyShutdown(
                        telemetry.getEmergencyShutdown())
                .leakDetected(
                        telemetry.getLeakDetected())
                .deviceOnline(
                        telemetry.getDeviceOnline())
                .batteryLevel(
                        telemetry.getBatteryLevel())
                .valveStatus(
                        telemetry.getValveStatus())
                .pumpStatus(
                        telemetry.getPumpStatus())
                .pipelineHealthScore(
                        telemetry.getPipelineHealthScore())
                .sensorHealthScore(
                        telemetry.getSensorHealthScore())
                .status(
                        telemetry.getStatus())
                .readingTime(
                        telemetry.getReadingTime())
                .runtimeHours(
                        telemetry.getRuntimeHours())

                .lastStartedAt(
                        telemetry.getLastStartedAt())

                .lastStoppedAt(
                        telemetry.getLastStoppedAt())
                .estimatedWaterLoss(
                        telemetry.getEstimatedWaterLoss())

                .leakLocation(
                        telemetry.getLeakLocation())
                .build();
        
    }
}
