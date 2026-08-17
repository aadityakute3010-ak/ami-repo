package com.ami.service.impl;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.CommunicationSettingsDto;
import com.ami.dto.requests.CreateDeviceRequestDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.CustomerInfoDto;
import com.ami.dto.requests.DeviceInfoDto;
import com.ami.dto.requests.MeterInfoDto;
import com.ami.dto.requests.UpdateConfigurationRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.requests.UpdateFirmwareRequestDto;
import com.ami.dto.responses.ConfigurationResponseDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceBulkUploadResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceHealthChartDto;
import com.ami.dto.responses.DeviceHealthResponseDto;
import com.ami.dto.responses.DeviceListResponseDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceStatusChartDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.FirmwareHistoryResponseDto;
import com.ami.dto.responses.FirmwareResponseDto;
import com.ami.dto.responses.InventoryResponseDto;
import com.ami.dto.responses.LocationResponseDto;
import com.ami.dto.responses.OfflineDeviceDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.entity.ArchivedDevice;
import com.ami.entity.Device;
import com.ami.entity.DeviceAudit;
import com.ami.entity.DeviceTelemetry;
import com.ami.entity.FirmwareHistory;
import com.ami.entity.Meter;
import com.ami.entity.User;
import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.DeviceStatus;
import com.ami.enums.InstallationStatus;
import com.ami.enums.IssueStatus;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.ProtocolType;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import com.ami.exception.BadRequestException;
import com.ami.exception.DuplicateResourceException;
import com.ami.exception.ResourceNotFoundException;
import com.ami.exception.UnauthorizedException;
import com.ami.repository.ArchivedDeviceRepository;
import com.ami.repository.DeviceAuditRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.DeviceTelemetryRepository;
import com.ami.repository.FirmwareHistoryRepository;
import com.ami.repository.InstallationRepository;
import com.ami.repository.IssueRepository;
import com.ami.repository.MaintenanceRepository;
import com.ami.repository.UserRepository;
import com.ami.service.DeviceService;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.ami.mapper.DeviceMapper;
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

	private final DeviceRepository deviceRepository;

	private final UserRepository userRepository;

	private final DeviceAuditRepository deviceAuditRepository;
	
	private final FirmwareHistoryRepository firmwareHistoryRepository;
	
	private final DeviceTelemetryRepository deviceTelemetryRepository;
	
	private final ArchivedDeviceRepository archivedDeviceRepository;
	
	private final DeviceMapper deviceMapper;
	
	private final InstallationRepository installationRepository;

	private final IssueRepository issueRepository;

	private final MaintenanceRepository maintenanceRepository;
	
	
	private User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
	
	private void validateSuperAdmin(User user) {

	    if (user.getRole() != RoleType.SUPER_ADMIN) {
	        throw new UnauthorizedException("Only Super Admin can perform this operation");
	    }
	}

	private void validateAdminOrSuperAdmin(User user) {

	    if (user.getRole() != RoleType.SUPER_ADMIN
	            && user.getRole() != RoleType.ADMIN) {

	        throw new UnauthorizedException(
	                "Only Admin or Super Admin can perform this operation");
	    }
	}

	private void validateAdmin(User user) {

	    if (user.getRole() != RoleType.ADMIN) {
	        throw new UnauthorizedException("Only Admin can perform this operation");
	    }
	}

	private void validateDuplicateDevicesInRequest(List<CreateDeviceRequestDto> devices) {

		Set<String> deviceIds = new HashSet<>();
		Set<String> macs = new HashSet<>();
		Set<String> serials = new HashSet<>();

		for (CreateDeviceRequestDto dto : devices) {

			String deviceId = dto.getDevice().getDeviceId();

			String macAddress = dto.getDevice().getMacAddress();

			String serialNumber = dto.getDevice().getSerialNumber();

			if (!deviceIds.add(deviceId)) {
				throw new DuplicateResourceException("Duplicate deviceId in request: " + deviceId);
			}

			if (!macs.add(macAddress)) {
				throw new DuplicateResourceException("Duplicate macAddress in request: " + macAddress);
			}

			if (!serials.add(serialNumber)) {
				throw new DuplicateResourceException("Duplicate serialNumber in request: " + serialNumber);
			}
		}
	}
	
	private Device getDevice(
	        Long deviceId) {

	    return deviceRepository.findById(deviceId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Device not found"));
	}
	
	private void validateDeviceAccess(
	        User loggedInUser,
	        Device device) {

	    if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
	        return;
	    }

	    if (loggedInUser.getRole() == RoleType.ADMIN) {

	        if (device.getAssignedAdmin() == null
	                || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {

	            throw new UnauthorizedException("Access Denied");
	        }

	        return;
	    }

	    if (loggedInUser.getRole() == RoleType.USER) {

	        if (device.getAssignedUser() == null
	                || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {

	            throw new UnauthorizedException("Access Denied");
	        }

	        return;
	    }

	    throw new UnauthorizedException("Access Denied");
	}
	private void validateDeviceUpdateAccess(
	        User loggedInUser,
	        Device device) {

	    validateAdminOrSuperAdmin(loggedInUser);

	    if (loggedInUser.getRole() == RoleType.ADMIN) {

	        if (device.getAssignedAdmin() == null
	                || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {

	            throw new UnauthorizedException(
	                    "You can update only your own devices");
	        }
	    }
	}
	private void validatePagination(
	        int page,
	        int size,
	        String sortBy,
	        String direction) {

	    if (page < 0) {
	        throw new BadRequestException(
	                "Page index cannot be negative");
	    }

	    if (size <= 0 || size > 100) {
	        throw new BadRequestException(
	                "Page size must be between 1 and 100");
	    }

	    if (!List.of(
	            "id",
	            "deviceId",
	            "deviceName",
	            "status",
	            "createdAt")
	            .contains(sortBy)) {

	        throw new BadRequestException(
	                "Invalid sort field");
	    }

	    if (!direction.equalsIgnoreCase("ASC")
	            && !direction.equalsIgnoreCase("DESC")) {

	        throw new BadRequestException(
	                "Invalid sort direction");
	    }
	}
	private User resolveUser(Long id) {
		if (id == null)
			return null;
		return userRepository.findById(id)     .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	private void validateDeviceExists(CreateDeviceRequestDto dto) {

	    validateUniqueFields(
	            dto.getDevice().getDeviceId(),
	            dto.getDevice().getMacAddress(),
	            dto.getDevice().getSerialNumber(),
	            null
	    );
	}
	
	private void validateUniqueFields(
	        String deviceId,
	        String macAddress,
	        String serialNumber,
	        Long currentDeviceId) {

	    if (currentDeviceId == null) {

	        if (deviceRepository.existsByDeviceId(deviceId)) {
	            throw new DuplicateResourceException(
	                    "Device ID already exists");
	        }

	        if (deviceRepository.existsByMacAddress(macAddress)) {
	            throw new DuplicateResourceException(
	                    "MAC address already exists");
	        }

	        if (deviceRepository.existsBySerialNumber(serialNumber)) {
	            throw new DuplicateResourceException(
	                    "Serial number already exists");
	        }

	    } else {

	        if (deviceRepository.existsByDeviceIdAndIdNot(deviceId, currentDeviceId)) {
	            throw new DuplicateResourceException(
	                    "Device ID already exists");
	        }

	        if (deviceRepository.existsByMacAddressAndIdNot(macAddress, currentDeviceId)) {
	            throw new DuplicateResourceException(
	                    "MAC address already exists");
	        }

	        if (deviceRepository.existsBySerialNumberAndIdNot(serialNumber, currentDeviceId)) {
	            throw new DuplicateResourceException(
	                    "Serial number already exists");
	        }
	    }
	}

	private Device buildDevice(CreateDeviceRequestDto request, User assignedAdmin, User assignedUser, User superAdmin) {

		String deviceName = request.getDevice().getDeviceName().trim();

		Meter meter = Meter.builder().meterName(deviceName).sourceType(request.getMeter().getSourceType())
				.technologyType(request.getMeter().getTechnologyType())

				// backend default
				.status(DeviceStatus.ACTIVE)

				// dynamic meter fields
				.meterType(request.getMeter().getMeterType()).application(request.getMeter().getApplication())
				.diameterSize(request.getMeter().getDiameterSize()).literPerPulse(request.getMeter().getLiterPerPulse())

				.ctRatio(request.getMeter().getCtRatio()).ptRatio(request.getMeter().getPtRatio())
				.voltageClass(request.getMeter().getVoltageClass())

				.inverterType(request.getMeter().getInverterType()).plantCapacity(request.getMeter().getPlantCapacity())
				.panelCount(request.getMeter().getPanelCount())

				.meterStartReading(request.getMeter().getMeterStartReading()).build();

		Device device = Device.builder().deviceId(request.getDevice().getDeviceId()).deviceName(deviceName)
				.macAddress(request.getDevice().getMacAddress()).serialNumber(request.getDevice().getSerialNumber())

				.customerName(request.getCustomer().getCustomerName())
				.customerAddress(request.getCustomer().getCustomerAddress())
				.buildingOrWing(request.getCustomer().getBuildingOrWing()).area(request.getCustomer().getArea())
				.zone(request.getCustomer().getZone()).city(request.getCustomer().getCity())
				.state(request.getCustomer().getState()).meterLocation(request.getCustomer().getMeterLocation())
				.wakeupTime(request.getCommunication() != null ? request.getCommunication().getWakeupTime() : null)
				.dataSampleCount(
						request.getCommunication() != null ? request.getCommunication().getDataSampleCount() : null)
				.online(false).healthStatus(DeviceHealthStatus.OFFLINE)

				.createdBy(superAdmin).assignedAdmin(assignedAdmin).assignedUser(assignedUser).meter(meter).build();

		meter.setDevice(device);

		return device;
	}

	@Override
	@Transactional
	public List<DeviceResponseDto> createDevices(CreateDevicesRequestDto request) {

		User superAdmin = getLoggedInUser();

		validateSuperAdmin(superAdmin);

		User assignedAdmin = resolveUser(request.getAssignedAdminId());
		User assignedUser = resolveUser(request.getAssignedUserId());

		validateDuplicateDevicesInRequest(request.getDevices());

		List<Device> devicesToSave = new ArrayList<>();

		for (CreateDeviceRequestDto dto : request.getDevices()) {

			validateDeviceExists(dto);

			devicesToSave.add(buildDevice(dto, assignedAdmin, assignedUser, superAdmin));
		}

		List<Device> savedDevices = deviceRepository.saveAll(devicesToSave);
		for (Device savedDevice : savedDevices) {
			saveAudit(savedDevice, "DEVICE_CREATED", "Device created successfully",
					superAdmin.getFirstName() + " " + superAdmin.getLastName());
		}

		return savedDevices.stream().map(this::mapToResponse).toList();
	}

	private DeviceResponseDto mapToResponse(Device device) {

		return DeviceResponseDto.builder().id(device.getId()).deviceId(device.getDeviceId())
				.deviceName(device.getDeviceName()).macAddress(device.getMacAddress())
				.serialNumber(device.getSerialNumber())

				// Meter Basic Information
				.meterName(device.getMeter() != null ? device.getMeter().getMeterName() : null)
				.technologyType(device.getMeter() != null ? device.getMeter().getTechnologyType() : null)
				.sourceType(device.getMeter() != null ? device.getMeter().getSourceType() : null)
				.status(device.getMeter() != null ? device.getMeter().getStatus() : null)

				// Runtime
				.healthStatus(device.getHealthStatus())

				// Customer Information
				.customerName(device.getCustomerName()).customerAddress(device.getCustomerAddress())
				.buildingOrWing(device.getBuildingOrWing()).area(device.getArea()).zone(device.getZone())
				.city(device.getCity()).state(device.getState()).meterLocation(device.getMeterLocation())

				.wakeupTime(device.getWakeupTime()).dataSampleCount(device.getDataSampleCount())

				// Dynamic Meter Information
				.meterStartReading(device.getMeter() != null ? device.getMeter().getMeterStartReading() : null)
				.meterType(device.getMeter() != null ? device.getMeter().getMeterType() : null)
				.application(device.getMeter() != null ? device.getMeter().getApplication() : null)
				.diameterSize(device.getMeter() != null ? device.getMeter().getDiameterSize() : null)
				.literPerPulse(device.getMeter() != null ? device.getMeter().getLiterPerPulse() : null)
				.ctRatio(device.getMeter() != null ? device.getMeter().getCtRatio() : null)
				.ptRatio(device.getMeter() != null ? device.getMeter().getPtRatio() : null)
				.voltageClass(device.getMeter() != null ? device.getMeter().getVoltageClass() : null)
				.inverterType(device.getMeter() != null ? device.getMeter().getInverterType() : null)
				.plantCapacity(device.getMeter() != null ? device.getMeter().getPlantCapacity() : null)
				.panelCount(device.getMeter() != null ? device.getMeter().getPanelCount() : null)

				// Assignment
				.assignedAdminName(device.getAssignedAdmin() != null
						? device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName()
						: null)

				.assignedUserName(device.getAssignedUser() != null
						? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
						: null)

				// Audit
				.createdAt(device.getCreatedAt()).build();
	}
	@Override
	public PagedDeviceResponseDto getDevices(
	        int page,
	        int size,
	        String search,
	        DeviceStatus status,
	        SourceType sourceType,
	        TechnologyType technologyType,
	        String zone,
	        String location,
	        String sortBy,
	        String direction) {

	    User loggedInUser = getLoggedInUser();
	    
	    validatePagination(
	            page,
	            size,
	            sortBy,
	            direction);

	    Sort sort = direction.equalsIgnoreCase("ASC")
	            ? Sort.by(sortBy).ascending()
	            : Sort.by(sortBy).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    Long adminId = null;
	    Long userId = null;

	    if (loggedInUser.getRole() == RoleType.ADMIN) {
	        adminId = loggedInUser.getId();
	    } else if (loggedInUser.getRole() == RoleType.USER) {
	        userId = loggedInUser.getId();
	    }

	    Page<Device> devicePage = deviceRepository.findDevicesWithFilters(
	            adminId,
	            userId,
	            search,
	            status,
	            sourceType,
	            technologyType,
	            zone,
	            location,
	            pageable);

	    PagedDeviceResponseDto response = new PagedDeviceResponseDto();

	    response.setDevices(
	            devicePage.getContent()
	                    .stream()
	                    .map(deviceMapper::mapToDeviceListResponse)
	                    .toList());

	    response.setCurrentPage(devicePage.getNumber());
	    response.setTotalPages(devicePage.getTotalPages());
	    response.setTotalElements(devicePage.getTotalElements());

	    return response;
	}

	@Override
	public DeviceResponseDto assignDeviceToUser(Long deviceId, Long userId) {

		User loggedInUser = getLoggedInUser();

		Device device = getDevice(deviceId);

		validateAdminOrSuperAdmin(loggedInUser);


		User targetUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		

		// ADMIN RULES
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new UnauthorizedException("You cannot assign this device");
			}

			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new UnauthorizedException("You cannot assign device to this user");
			}
		}

		if (device.getAssignedUser() != null) {
		    throw new BadRequestException(
		            "Device is already assigned to user: "
		                    + device.getAssignedUser().getFirstName());
		}
		// Checks if the User have the source of that Device Type
		Meter meter = device.getMeter();
		if (meter == null) {
			throw new BadRequestException("Meter not configured for device");
		}
		SourceType sourceType = meter.getSourceType();
		if (!targetUser.getAssignedSources().contains(sourceType)) {
		    throw new UnauthorizedException(
		            "User does not have access to source: " + sourceType);
		}
		device.setAssignedUser(targetUser);
		Device updatedDevice = deviceRepository.save(device);
		return mapToResponse(updatedDevice);
	}

	public List<DeviceResponseDto> getAvailableDevicesForAssignment(Long userId) {

		User loggedInAdmin = getLoggedInUser();
		RoleType role = loggedInAdmin.getRole();
		validateAdminOrSuperAdmin(loggedInAdmin);



		User targetUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// USER MUST BELONG TO ADMIN
		if (role == RoleType.ADMIN) {
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInAdmin.getId())) {
				throw new UnauthorizedException("You cannot access this user");
			}
		}

		List<Device> availableDevices;

		if (role == RoleType.SUPER_ADMIN) {
			availableDevices = deviceRepository.findAvailableDevicesForSuperAdmin(targetUser.getAssignedSources());
		} else {
			availableDevices = deviceRepository.findAvailableDevicesForUser(loggedInAdmin.getId(),
					targetUser.getAssignedSources());
		}
		return availableDevices.stream()
				.filter(device -> device.getMeter() != null && device.getMeter().getStatus() == DeviceStatus.ACTIVE)
				.map(this::mapToResponse).toList();
	}

	@Override
	public DashboardSummaryResponseDto getDashboardSummary() {

		User loggedInUser = getLoggedInUser();
		List<Device> devices;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			devices = deviceRepository.findAll();

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {
			devices = deviceRepository.findByAssignedAdminId(loggedInUser.getId());

		} else if (loggedInUser.getRole() == RoleType.USER) {
			devices = deviceRepository.findByAssignedUserId(loggedInUser.getId());

		} else {
			throw new UnauthorizedException("Access Denied");
		}
		List<Device> validDevices = devices.stream().filter(d -> d.getMeter() != null).toList();
		DashboardSummaryResponseDto response = new DashboardSummaryResponseDto();
		// Source Counts
		response.setWaterCount(
				validDevices.stream().filter(d -> d.getMeter().getSourceType() == SourceType.WATER).count());
		response.setSolarCount(
				validDevices.stream().filter(d -> d.getMeter().getSourceType() == SourceType.SOLAR).count());
		response.setGasCount(validDevices.stream().filter(d -> d.getMeter().getSourceType() == SourceType.GAS).count());
		response.setEnergyCount(
				validDevices.stream().filter(d -> d.getMeter().getSourceType() == SourceType.ENERGY).count());

		// Technology Counts
		response.setWifiCount(
				validDevices.stream().filter(d -> d.getMeter().getTechnologyType() == TechnologyType.WIFI).count());
		response.setEthernetCount(
				validDevices.stream().filter(d -> d.getMeter().getTechnologyType() == TechnologyType.ETHERNET).count());
		response.setNbIotCount(
				validDevices.stream().filter(d -> d.getMeter().getTechnologyType() == TechnologyType.NB_IOT).count());
		response.setFourGCount(
				validDevices.stream().filter(d -> d.getMeter().getTechnologyType() == TechnologyType.FOUR_G).count());
		// New KPI Cards
		response.setActiveInstallations(
		        installationRepository.countByStatus(InstallationStatus.COMPLETED));

		response.setActiveServiceEngineers(
		        userRepository.countByRole(RoleType.SERVICE_ENGINEER));

		response.setActiveMaintenance(
		        maintenanceRepository.countByStatus(MaintenanceStatus.IN_PROGRESS));

		response.setOpenIssues(
		        issueRepository.countByStatus(IssueStatus.OPEN));
		return response;
	}

	@Override
	public DeviceDetailsResponseDto getDeviceDetails(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = getDevice(deviceId);

		validateDeviceAccess(
		        loggedInUser,
		        device);
		
		return deviceMapper.mapToDeviceDetailsResponse(device);
	}

	
	
	@Override
	public DeviceUpdateFormResponseDto getDeviceForUpdate(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = getDevice(deviceId);

		Meter meter = device.getMeter();

		if (meter == null) {
			throw new BadRequestException("Meter not configured for device");
		}
		validateAdminOrSuperAdmin(loggedInUser);

		validateDeviceAccess(
		        loggedInUser,
		        device);

		return deviceMapper.mapToDeviceUpdateFormResponse(device);
	}


	@Override
	public DeviceResponseDto updateDevice(
	        Long deviceId,
	        UpdateDeviceRequestDto request) {

	    User loggedInUser = getLoggedInUser();

	    Device device = getDevice(deviceId);

	    validateDeviceUpdateAccess(
	            loggedInUser,
	            device);

		Meter meter = device.getMeter();

		if (meter == null) {
			throw new BadRequestException("Meter not configured for device");
		}

		// SUPER ADMIN ONLY: Device Info + Unique field validation
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN && request.getDevice() != null) {

			if (request.getDevice().getDeviceId() != null) {

			    String newDeviceId = request.getDevice().getDeviceId().trim();

			    validateUniqueFields(
			            newDeviceId,
			            device.getMacAddress(),
			            device.getSerialNumber(),
			            device.getId());

			    device.setDeviceId(newDeviceId);
			}

			if (request.getDevice().getDeviceName() != null) {
				String newDeviceName = request.getDevice().getDeviceName().trim();

				device.setDeviceName(newDeviceName);
				meter.setMeterName(newDeviceName);
			}

			if (request.getDevice().getMacAddress() != null) {

			    String newMacAddress = request.getDevice().getMacAddress().trim();

			    validateUniqueFields(
			            device.getDeviceId(),
			            newMacAddress,
			            device.getSerialNumber(),
			            device.getId());

			    device.setMacAddress(newMacAddress);
			}

			if (request.getDevice().getSerialNumber() != null) {

			    String newSerialNumber = request.getDevice().getSerialNumber().trim();

			    validateUniqueFields(
			            device.getDeviceId(),
			            device.getMacAddress(),
			            newSerialNumber,
			            device.getId());

			    device.setSerialNumber(newSerialNumber);
			}
		}

		// SUPER ADMIN ONLY: Locked Meter Fields
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN && request.getMeter() != null) {

			if (request.getMeter().getSourceType() != null) {
				meter.setSourceType(request.getMeter().getSourceType());
			}

			if (request.getMeter().getTechnologyType() != null) {
				meter.setTechnologyType(request.getMeter().getTechnologyType());
			}

			if (request.getMeter().getMeterStartReading() != null) {
				meter.setMeterStartReading(request.getMeter().getMeterStartReading());
			}
		}

		// ADMIN + SUPER ADMIN: Customer Information
		if (request.getCustomer() != null) {

			if (request.getCustomer().getCustomerName() != null) {
				device.setCustomerName(request.getCustomer().getCustomerName());
			}

			if (request.getCustomer().getCustomerAddress() != null) {
				device.setCustomerAddress(request.getCustomer().getCustomerAddress());
			}

			if (request.getCustomer().getBuildingOrWing() != null) {
				device.setBuildingOrWing(request.getCustomer().getBuildingOrWing());
			}

			if (request.getCustomer().getArea() != null) {
				device.setArea(request.getCustomer().getArea());
			}

			if (request.getCustomer().getZone() != null) {
				device.setZone(request.getCustomer().getZone());
			}

			if (request.getCustomer().getCity() != null) {
				device.setCity(request.getCustomer().getCity());
			}

			if (request.getCustomer().getState() != null) {
				device.setState(request.getCustomer().getState());
			}

			if (request.getCustomer().getMeterLocation() != null) {
				device.setMeterLocation(request.getCustomer().getMeterLocation());
			}
		}

		// ADMIN + SUPER ADMIN: Communication Settings
		if (request.getCommunication() != null) {

			if (request.getCommunication().getWakeupTime() != null) {
				device.setWakeupTime(request.getCommunication().getWakeupTime());
			}

			if (request.getCommunication().getDataSampleCount() != null) {
				device.setDataSampleCount(request.getCommunication().getDataSampleCount());
			}
		}

		// ADMIN + SUPER ADMIN: Dynamic Meter Fields
		if (request.getMeter() != null) {

			if (request.getMeter().getMeterType() != null) {
				meter.setMeterType(request.getMeter().getMeterType());
			}

			if (request.getMeter().getApplication() != null) {
				meter.setApplication(request.getMeter().getApplication());
			}

			if (request.getMeter().getDiameterSize() != null) {
				meter.setDiameterSize(request.getMeter().getDiameterSize());
			}

			if (request.getMeter().getLiterPerPulse() != null) {
				meter.setLiterPerPulse(request.getMeter().getLiterPerPulse());
			}

			if (request.getMeter().getCtRatio() != null) {
				meter.setCtRatio(request.getMeter().getCtRatio());
			}

			if (request.getMeter().getPtRatio() != null) {
				meter.setPtRatio(request.getMeter().getPtRatio());
			}

			if (request.getMeter().getVoltageClass() != null) {
				meter.setVoltageClass(request.getMeter().getVoltageClass());
			}

			if (request.getMeter().getInverterType() != null) {
				meter.setInverterType(request.getMeter().getInverterType());
			}

			if (request.getMeter().getPlantCapacity() != null) {
				meter.setPlantCapacity(request.getMeter().getPlantCapacity());
			}

			if (request.getMeter().getPanelCount() != null) {
				meter.setPanelCount(request.getMeter().getPanelCount());
			}
		}

		Device updatedDevice = deviceRepository.save(device);

		saveAudit(updatedDevice, "DEVICE_UPDATED", "Device configuration updated",
				loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

		return mapToResponse(updatedDevice);
		}
	
	@Override
	@Transactional
	public String archiveDevice(
	        Long deviceId,
	        String archiveReason) {

		Device device = getDevice(deviceId);

	    User loggedInUser = getLoggedInUser();

	    ArchivedDevice archivedDevice =
	    		
	            deviceMapper.mapToArchivedDevice(
	                    device,
	                    loggedInUser,
	                    archiveReason);

	    archivedDeviceRepository.save(archivedDevice);

	    deviceRepository.delete(device);

	    return "Device archived successfully";
	}
	
	
	@Override
	@Transactional
	public String restoreDevice(
	        Long archivedDeviceId) {

	    ArchivedDevice archivedDevice =
	            archivedDeviceRepository.findById(
	                    archivedDeviceId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Archived device not found"));
	    
	    validateUniqueFields(
	            archivedDevice.getDeviceId(),
	            archivedDevice.getMacAddress(),
	            archivedDevice.getSerialNumber(),
	            null);

	    Device device = deviceMapper.mapToDevice(archivedDevice);

	    deviceRepository.save(
	            device);

	    archivedDeviceRepository.delete(
	            archivedDevice);

	    return "Device restored successfully";
	}
	private void saveAudit(Device device, String action, String description, String performedBy) {
		DeviceAudit audit = DeviceAudit.builder().device(device).action(action).description(description)
				.performedBy(performedBy).actionTime(LocalDateTime.now()).build();
		deviceAuditRepository.save(audit);
	}

	@Override
	public List<DeviceAuditResponseDto> getDeviceAudit(Long deviceId) {

		User loggedInUser = getLoggedInUser();
		Device device = getDevice(deviceId);
		validateDeviceAccess(
		        loggedInUser,
		        device);
		return deviceAuditRepository.findByDeviceOrderByActionTimeDesc(device).stream()
				.map(audit -> DeviceAuditResponseDto.builder().id(audit.getId()).action(audit.getAction())
						.description(audit.getDescription()).performedBy(audit.getPerformedBy())
						.actionTime(audit.getActionTime()).build())
				.toList();
	}

	@Override
	@Transactional
	public void assignAdminToDevice(Long deviceId, Long adminId) {

		User loggedInUser = getLoggedInUser();
		validateSuperAdmin(loggedInUser);
		Device device = getDevice(deviceId);

		if (device.getAssignedAdmin() != null) {
		    throw new BadRequestException("Device is already assigned to an admin");
		}

		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
		if (admin.getRole() != RoleType.ADMIN) {
			throw new BadRequestException("Selected user is not an admin");
		}
		validateAdminSourceAccess(device, admin);
		device.setAssignedAdmin(admin);
		deviceRepository.save(device);
	}

	private void validateAdminSourceAccess(Device device, User admin) {

		if (device.getMeter() == null) {
			throw new BadRequestException("Meter not configured for device");
		}
		SourceType sourceType = device.getMeter().getSourceType();
		if (admin.getAssignedSources() == null || !admin.getAssignedSources().contains(sourceType)) {
			throw new UnauthorizedException("Admin is not assigned to source type " + sourceType);
		}
	}

	@Override
	public DeviceDashboardResponseDto getDeviceDashboard() {
		return DeviceDashboardResponseDto.builder().summary(getDashboardSummary()).healthChart(buildHealthChart())
				.statusChart(buildStatusChart()).recentOfflineDevices(getRecentOfflineDevices()).build();
	}

	private DeviceHealthChartDto buildHealthChart() {

		User loggedInUser = getLoggedInUser();

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return getSuperAdminHealthChart();
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return getAdminHealthChart(loggedInUser.getId());
		}

		if (loggedInUser.getRole() == RoleType.USER) {
			return getUserHealthChart(loggedInUser.getId());
		}

		throw new UnauthorizedException("Access Denied");
	}

	private DeviceHealthChartDto getSuperAdminHealthChart() {
		return DeviceHealthChartDto.builder().totalDevices(deviceRepository.count())
				.healthyDevices(deviceRepository.countByHealthStatus(DeviceHealthStatus.HEALTHY))
				.warningDevices(deviceRepository.countByHealthStatus(DeviceHealthStatus.WARNING))
				.criticalDevices(deviceRepository.countByHealthStatus(DeviceHealthStatus.CRITICAL))
				.offlineDevices(deviceRepository.countByHealthStatus(DeviceHealthStatus.OFFLINE)).build();
	}

	private DeviceHealthChartDto getAdminHealthChart(Long adminId) {

		long healthy = deviceRepository.countByAssignedAdminIdAndHealthStatus(adminId, DeviceHealthStatus.HEALTHY);
		long warning = deviceRepository.countByAssignedAdminIdAndHealthStatus(adminId, DeviceHealthStatus.WARNING);
		long critical = deviceRepository.countByAssignedAdminIdAndHealthStatus(adminId, DeviceHealthStatus.CRITICAL);
		long offline = deviceRepository.countByAssignedAdminIdAndHealthStatus(adminId, DeviceHealthStatus.OFFLINE);
		return DeviceHealthChartDto.builder().totalDevices(healthy + warning + critical + offline)
				.healthyDevices(healthy).warningDevices(warning).criticalDevices(critical).offlineDevices(offline)
				.build();
	}

	private DeviceHealthChartDto getUserHealthChart(Long userId) {
		long healthy = deviceRepository.countByAssignedUserIdAndHealthStatus(userId, DeviceHealthStatus.HEALTHY);
		long warning = deviceRepository.countByAssignedUserIdAndHealthStatus(userId, DeviceHealthStatus.WARNING);
		long critical = deviceRepository.countByAssignedUserIdAndHealthStatus(userId, DeviceHealthStatus.CRITICAL);
		long offline = deviceRepository.countByAssignedUserIdAndHealthStatus(userId, DeviceHealthStatus.OFFLINE);
		return DeviceHealthChartDto.builder().totalDevices(healthy + warning + critical + offline)
				.healthyDevices(healthy).warningDevices(warning).criticalDevices(critical).offlineDevices(offline)
				.build();
	}

	private DeviceStatusChartDto buildStatusChart() {

		User loggedInUser = getLoggedInUser();

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return getSuperAdminStatusChart();
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return getAdminStatusChart(loggedInUser.getId());
		}

		if (loggedInUser.getRole() == RoleType.USER) {
			return getUserStatusChart(loggedInUser.getId());
		}

		throw new UnauthorizedException("Access Denied");
	}

	private DeviceStatusChartDto getSuperAdminStatusChart() {

		long active = deviceRepository.countByMeter_Status(DeviceStatus.ACTIVE);
		long inactive = deviceRepository.countByMeter_Status(DeviceStatus.INACTIVE);
		long faulty = deviceRepository.countByMeter_Status(DeviceStatus.FAULTY);
		long offline = deviceRepository.countByMeter_Status(DeviceStatus.OFFLINE);
		return DeviceStatusChartDto.builder().totalDevices(active + inactive + faulty + offline).activeDevices(active)
				.inactiveDevices(inactive).faultyDevices(faulty).offlineDevices(offline).build();
	}

	private DeviceStatusChartDto getAdminStatusChart(Long adminId) {

		long active = deviceRepository.countByAssignedAdminIdAndMeter_Status(adminId, DeviceStatus.ACTIVE);
		long inactive = deviceRepository.countByAssignedAdminIdAndMeter_Status(adminId, DeviceStatus.INACTIVE);
		long faulty = deviceRepository.countByAssignedAdminIdAndMeter_Status(adminId, DeviceStatus.FAULTY);
		long offline = deviceRepository.countByAssignedAdminIdAndMeter_Status(adminId, DeviceStatus.OFFLINE);
		return DeviceStatusChartDto.builder().totalDevices(active + inactive + faulty + offline).activeDevices(active)
				.inactiveDevices(inactive).faultyDevices(faulty).offlineDevices(offline).build();
	}

	private DeviceStatusChartDto getUserStatusChart(Long userId) {

		long active = deviceRepository.countByAssignedUserIdAndMeter_Status(userId, DeviceStatus.ACTIVE);
		long inactive = deviceRepository.countByAssignedUserIdAndMeter_Status(userId, DeviceStatus.INACTIVE);
		long faulty = deviceRepository.countByAssignedUserIdAndMeter_Status(userId, DeviceStatus.FAULTY);
		long offline = deviceRepository.countByAssignedUserIdAndMeter_Status(userId, DeviceStatus.OFFLINE);
		return DeviceStatusChartDto.builder().totalDevices(active + inactive + faulty + offline).activeDevices(active)
				.inactiveDevices(inactive).faultyDevices(faulty).offlineDevices(offline).build();
	}

	private List<OfflineDeviceDto> getRecentOfflineDevices() {

		User loggedInUser = getLoggedInUser();

		List<Device> devices;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			devices = deviceRepository.findTop10ByHealthStatusOrderByLastSyncTimeAsc(DeviceHealthStatus.OFFLINE);
		}

		else if (loggedInUser.getRole() == RoleType.ADMIN) {

			devices = deviceRepository.findTop10ByAssignedAdminIdAndHealthStatusOrderByLastSyncTimeAsc(
					loggedInUser.getId(), DeviceHealthStatus.OFFLINE);
		}

		else if (loggedInUser.getRole() == RoleType.USER) {

			devices = deviceRepository.findTop10ByAssignedUserIdAndHealthStatusOrderByLastSyncTimeAsc(
					loggedInUser.getId(), DeviceHealthStatus.OFFLINE);
		}

		else {
			throw new UnauthorizedException("Access Denied");
		}

		return devices.stream()
		        .map(deviceMapper::mapOfflineDevice)
		        .toList();
	}

	
	private String getCellValue(Row row, int index) {

		DataFormatter formatter = new DataFormatter();

		if (row == null || row.getCell(index) == null) {
			return "";
		}

		return formatter.formatCellValue(row.getCell(index)).trim();
	}

	private Double getDoubleCellValue(Row row, int index) {

		String value = getCellValue(row, index);

		if (value == null || value.isBlank()) {
			return null;
		}

		return Double.valueOf(value);
	}

	private Integer getIntegerCellValue(Row row, int index) {

		String value = getCellValue(row, index);

		if (value == null || value.isBlank()) {
			return null;
		}

		return (int) Double.parseDouble(value.trim());
	}

	private Double parseDouble(String value) {

	    if (value == null || value.trim().isEmpty()) {
	        return null;
	    }

	    return Double.valueOf(value.trim());
	}

	private Integer parseInteger(String value) {

		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		return (int) Double.parseDouble(value.trim());
	}

	@Override
	public DeviceBulkUploadResponseDto bulkUploadDevices(MultipartFile file) {

		User loggedInUser = getLoggedInUser();

		validateSuperAdmin(loggedInUser);

		String fileName = file.getOriginalFilename();

		if (fileName == null) {
			throw new  BadRequestException("Invalid file");
		}

		fileName = fileName.toLowerCase();

		if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
			return processExcel(file);
		}

		if (fileName.endsWith(".csv")) {
			return processCsv(file);
		}

		if (fileName.endsWith(".pdf")) {
			return processPdf(file);
		}

		throw new BadRequestException("Only XLSX, XLS, CSV and PDF files are allowed");
	}

	private DeviceBulkUploadResponseDto processExcel(MultipartFile file) {

		List<String> errors = new ArrayList<>();
		int total = 0;
		int success = 0;
		int failed = 0;
		
		 User superAdmin = getLoggedInUser();

		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				total++;
				try {
					Row row = sheet.getRow(i);
					CreateDeviceRequestDto request = mapExcelRow(row);
					saveBulkDevice(request, superAdmin);
					success++;
				} catch (Exception e) {
					failed++;
					errors.add("Row " + (i + 1) + " : " + e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new  BadRequestException("Failed to read Excel file");
		}
		return DeviceBulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private DeviceBulkUploadResponseDto processPdf(MultipartFile file) {

		List<String> errors = new ArrayList<>();

		int total = 0;
		int success = 0;
		int failed = 0;
		
		User superAdmin = getLoggedInUser();

		try (PDDocument document = PDDocument.load(file.getInputStream())) {

			PDFTextStripper stripper = new PDFTextStripper();

			String text = stripper.getText(document);

			String[] lines = text.split("\\r?\\n");

			for (int i = 0; i < lines.length; i++) {

				String line = lines[i].trim();

				if (line.isBlank()) {
					continue;
				}

				// Skip header line
				if (line.toLowerCase().startsWith("deviceid|devicename|macaddress")) {
					continue;
				}

				total++;

				try {
					CreateDeviceRequestDto request = mapPdfLine(line);

					saveBulkDevice(request, superAdmin);

					success++;

				} catch (Exception e) {
					failed++;
					errors.add("Line " + (i + 1) + " : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			throw new BadRequestException("Failed to read PDF file");
		}

		return DeviceBulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private CreateDeviceRequestDto mapPdfLine(String line) {

		CreateDeviceRequestDto dto = new CreateDeviceRequestDto();

		String[] values = line.split("\\|", -1);

		if (values.length < 27) {
			throw new BadRequestException("Invalid PDF row format. Expected 27 columns");
		}

		String deviceName = values[1].trim();

		DeviceInfoDto device = new DeviceInfoDto();
		device.setDeviceId(values[0].trim());
		device.setDeviceName(deviceName);
		device.setMacAddress(values[2].trim());
		device.setSerialNumber(values[3].trim());

		MeterInfoDto meter = new MeterInfoDto();

		// Device name and meter name will be same
		meter.setMeterName(deviceName);

		meter.setSourceType(SourceType.valueOf(values[4].trim().toUpperCase()));
		meter.setTechnologyType(TechnologyType.valueOf(values[5].trim().toUpperCase()));

		// Dynamic Meter Fields
		meter.setMeterType(values[6].trim());
		meter.setApplication(values[7].trim());

		// WATER
		meter.setDiameterSize(values[8].trim());
		meter.setLiterPerPulse(parseDouble(values[9]));

		// ENERGY
		meter.setCtRatio(values[10].trim());
		meter.setPtRatio(values[11].trim());
		meter.setVoltageClass(values[12].trim());

		// SOLAR
		meter.setInverterType(values[13].trim());
		meter.setPlantCapacity(values[14].trim());
		meter.setPanelCount(parseInteger(values[15]));

		// COMMON
		meter.setMeterStartReading(parseDouble(values[16]));

		CustomerInfoDto customer = new CustomerInfoDto();
		customer.setCustomerName(values[17].trim());
		customer.setCustomerAddress(values[18].trim());
		customer.setBuildingOrWing(values[19].trim());
		customer.setArea(values[20].trim());
		customer.setZone(values[21].trim());
		customer.setCity(values[22].trim());
		customer.setState(values[23].trim());
		customer.setMeterLocation(values[24].trim());

		CommunicationSettingsDto communication = new CommunicationSettingsDto();
		communication.setWakeupTime(values[25].trim());
		communication.setDataSampleCount(parseInteger(values[26]));

		dto.setDevice(device);
		dto.setMeter(meter);
		dto.setCustomer(customer);
		dto.setCommunication(communication);

		return dto;
	}

	private CreateDeviceRequestDto mapExcelRow(Row row) {

		CreateDeviceRequestDto dto = new CreateDeviceRequestDto();
		String deviceName = getCellValue(row, 1);
		DeviceInfoDto device = new DeviceInfoDto();
		device.setDeviceId(getCellValue(row, 0));
		device.setDeviceName(deviceName);
		device.setMacAddress(getCellValue(row, 2));
		device.setSerialNumber(getCellValue(row, 3));

		MeterInfoDto meter = new MeterInfoDto();

		// Device name and meter name will be same
		meter.setMeterName(deviceName);

		meter.setSourceType(SourceType.valueOf(getCellValue(row, 4).toUpperCase()));

		meter.setTechnologyType(TechnologyType.valueOf(getCellValue(row, 5).toUpperCase()));

		// Dynamic Meter Fields
		meter.setMeterType(getCellValue(row, 6));
		meter.setApplication(getCellValue(row, 7));

		// WATER
		meter.setDiameterSize(getCellValue(row, 8));
		meter.setLiterPerPulse(getDoubleCellValue(row, 9));

		// ENERGY
		meter.setCtRatio(getCellValue(row, 10));
		meter.setPtRatio(getCellValue(row, 11));
		meter.setVoltageClass(getCellValue(row, 12));

		// SOLAR
		meter.setInverterType(getCellValue(row, 13));
		meter.setPlantCapacity(getCellValue(row, 14));
		meter.setPanelCount(getIntegerCellValue(row, 15));

		// COMMON
		meter.setMeterStartReading(getDoubleCellValue(row, 16));

		CustomerInfoDto customer = new CustomerInfoDto();
		customer.setCustomerName(getCellValue(row, 17));
		customer.setCustomerAddress(getCellValue(row, 18));
		customer.setBuildingOrWing(getCellValue(row, 19));
		customer.setArea(getCellValue(row, 20));
		customer.setZone(getCellValue(row, 21));
		customer.setCity(getCellValue(row, 22));
		customer.setState(getCellValue(row, 23));
		customer.setMeterLocation(getCellValue(row, 24));
		CommunicationSettingsDto communication = new CommunicationSettingsDto();
		communication.setWakeupTime(getCellValue(row, 25));
		communication.setDataSampleCount(getIntegerCellValue(row, 26));
		dto.setDevice(device);
		dto.setMeter(meter);
		dto.setCustomer(customer);
		dto.setCommunication(communication);

		return dto;
	}

	private DeviceBulkUploadResponseDto processCsv(MultipartFile file) {

		List<String> errors = new ArrayList<>();

		int total = 0;
		int success = 0;
		int failed = 0;
		
		User superAdmin = getLoggedInUser();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
			for (CSVRecord record : csvParser) {
				total++;
				try {
					CreateDeviceRequestDto request = mapCsvRecord(record);
					saveBulkDevice(request, superAdmin);
					success++;
				} catch (Exception e) {
					failed++;
					errors.add("Row " + record.getRecordNumber() + " : " + e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new BadRequestException("Failed to read CSV file");
		}
		return DeviceBulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private void saveBulkDevice(
	        CreateDeviceRequestDto request,
	        User superAdmin) {

	    validateDeviceExists(request);

	    Device device = buildDevice(request, null, null, superAdmin);

	    deviceRepository.save(device);
	}	private CreateDeviceRequestDto mapCsvRecord(CSVRecord record) {

		CreateDeviceRequestDto dto = new CreateDeviceRequestDto();

		String deviceName = record.get("deviceName").trim();

		DeviceInfoDto device = new DeviceInfoDto();
		device.setDeviceId(record.get("deviceId").trim());
		device.setDeviceName(deviceName);
		device.setMacAddress(record.get("macAddress").trim());
		device.setSerialNumber(record.get("serialNumber").trim());

		MeterInfoDto meter = new MeterInfoDto();

		// Device name and meter name will be same
		meter.setMeterName(deviceName);

		meter.setSourceType(SourceType.valueOf(record.get("sourceType").trim().toUpperCase()));

		meter.setTechnologyType(TechnologyType.valueOf(record.get("technologyType").trim().toUpperCase()));

		// Dynamic Meter Fields
		meter.setMeterType(record.get("meterType").trim());
		meter.setApplication(record.get("application").trim());

		// WATER
		meter.setDiameterSize(record.get("diameterSize").trim());
		meter.setLiterPerPulse(parseDouble(record.get("literPerPulse")));

		// ENERGY
		meter.setCtRatio(record.get("ctRatio").trim());
		meter.setPtRatio(record.get("ptRatio").trim());
		meter.setVoltageClass(record.get("voltageClass").trim());

		// SOLAR
		meter.setInverterType(record.get("inverterType").trim());
		meter.setPlantCapacity(record.get("plantCapacity").trim());
		meter.setPanelCount(parseInteger(record.get("panelCount")));

		// COMMON
		meter.setMeterStartReading(parseDouble(record.get("meterStartReading")));

		CustomerInfoDto customer = new CustomerInfoDto();
		customer.setCustomerName(record.get("customerName").trim());
		customer.setCustomerAddress(record.get("customerAddress").trim());
		customer.setBuildingOrWing(record.get("buildingOrWing").trim());
		customer.setArea(record.get("area").trim());
		customer.setZone(record.get("zone").trim());
		customer.setCity(record.get("city").trim());
		customer.setState(record.get("state").trim());
		customer.setMeterLocation(record.get("meterLocation").trim());

		CommunicationSettingsDto communication = new CommunicationSettingsDto();
		communication.setWakeupTime(record.get("wakeupTime").trim());
		communication.setDataSampleCount(parseInteger(record.get("dataSampleCount")));

		dto.setDevice(device);
		dto.setMeter(meter);
		dto.setCustomer(customer);
		dto.setCommunication(communication);

		return dto;
	}

	@Override
	public ExportFileResponseDto exportDevices(String fileType) {

		User loggedInUser = getLoggedInUser();

		List<Device> devices;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			devices = deviceRepository.findAll();

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {

			devices = deviceRepository.findByAssignedAdminId(loggedInUser.getId());

		} else if (loggedInUser.getRole() == RoleType.USER) {

			devices = deviceRepository.findByAssignedUserId(loggedInUser.getId());

		} else {

			throw new UnauthorizedException("Access Denied");
		}

		if (fileType == null || fileType.isBlank()) {
			fileType = "csv";
		}

		fileType = fileType.trim().toLowerCase();

		return switch (fileType) {

		case "csv" -> ExportFileResponseDto.builder().file(exportDevicesToCsv(devices)).fileName("devices.csv")
				.contentType("text/csv").build();

		case "pdf" -> ExportFileResponseDto.builder().file(exportDevicesToPdf(devices)).fileName("devices.pdf")
				.contentType("application/pdf").build();

		case "xlsx" ->
			ExportFileResponseDto.builder().file(exportDevicesToExcel(devices, "xlsx")).fileName("devices.xlsx")
					.contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").build();

		case "xls" -> ExportFileResponseDto.builder().file(exportDevicesToExcel(devices, "xls")).fileName("devices.xls")
				.contentType("application/vnd.ms-excel").build();

		default -> throw new BadRequestException("Only csv, pdf, xlsx and xls formats are allowed");
		};
	}

	private byte[] exportDevicesToCsv(List<Device> devices) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)) {

			csvPrinter.printRecord("deviceId", "deviceName", "macAddress", "serialNumber", "meterName", "sourceType",
					"technologyType", "status", "meterType", "application", "diameterSize", "literPerPulse", "ctRatio",
					"ptRatio", "voltageClass", "inverterType", "plantCapacity", "panelCount", "meterStartReading",
					"customerName", "customerAddress", "buildingOrWing", "area", "zone", "city", "state",
					"meterLocation", "wakeupTime", "dataSampleCount", "assignedAdmin", "assignedUser", "online");

			for (Device device : devices) {

				Meter meter = device.getMeter();

				csvPrinter.printRecord(device.getDeviceId(), device.getDeviceName(), device.getMacAddress(),
						device.getSerialNumber(),

						meter != null ? meter.getMeterName() : null, meter != null ? meter.getSourceType() : null,
						meter != null ? meter.getTechnologyType() : null, meter != null ? meter.getStatus() : null,
						meter != null ? meter.getMeterType() : null, meter != null ? meter.getApplication() : null,
						meter != null ? meter.getDiameterSize() : null, meter != null ? meter.getLiterPerPulse() : null,
						meter != null ? meter.getCtRatio() : null, meter != null ? meter.getPtRatio() : null,
						meter != null ? meter.getVoltageClass() : null, meter != null ? meter.getInverterType() : null,
						meter != null ? meter.getPlantCapacity() : null, meter != null ? meter.getPanelCount() : null,
						meter != null ? meter.getMeterStartReading() : null,

						device.getCustomerName(), device.getCustomerAddress(), device.getBuildingOrWing(),
						device.getArea(), device.getZone(), device.getCity(), device.getState(),
						device.getMeterLocation(),

						device.getWakeupTime(), device.getDataSampleCount(),

						device.getAssignedAdmin() != null
								? device.getAssignedAdmin().getFirstName() + " "
										+ device.getAssignedAdmin().getLastName()
								: "",

						device.getAssignedUser() != null
								? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
								: "",

						device.getOnline());
			}

			csvPrinter.flush();
			return out.toByteArray();

		} catch (Exception e) {
			throw new BadRequestException ("Failed to export devices", e);
		}
	}

	private byte[] exportDevicesToExcel(List<Device> devices, String fileType) {

		try (Workbook workbook = fileType.equalsIgnoreCase("xls") ? new HSSFWorkbook() : new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Devices");

			String[] headers = { "deviceId", "deviceName", "macAddress", "serialNumber", "meterName", "sourceType",
					"technologyType", "status", "meterType", "application", "diameterSize", "literPerPulse", "ctRatio",
					"ptRatio", "voltageClass", "inverterType", "plantCapacity", "panelCount", "meterStartReading",
					"customerName", "customerAddress", "buildingOrWing", "area", "zone", "city", "state",
					"meterLocation", "wakeupTime", "dataSampleCount", "assignedAdmin", "assignedUser", "online" };

			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			int rowIndex = 1;

			for (Device device : devices) {

				Meter meter = device.getMeter();
				Row row = sheet.createRow(rowIndex++);

				row.createCell(0).setCellValue(value(device.getDeviceId()));
				row.createCell(1).setCellValue(value(device.getDeviceName()));
				row.createCell(2).setCellValue(value(device.getMacAddress()));
				row.createCell(3).setCellValue(value(device.getSerialNumber()));

				row.createCell(4).setCellValue(value(meter != null ? meter.getMeterName() : null));
				row.createCell(5).setCellValue(
						value(meter != null && meter.getSourceType() != null ? meter.getSourceType().name() : null));
				row.createCell(6).setCellValue(value(
						meter != null && meter.getTechnologyType() != null ? meter.getTechnologyType().name() : null));
				row.createCell(7).setCellValue(
						value(meter != null && meter.getStatus() != null ? meter.getStatus().name() : null));
				row.createCell(8).setCellValue(value(meter != null ? meter.getMeterType() : null));
				row.createCell(9).setCellValue(value(meter != null ? meter.getApplication() : null));
				row.createCell(10).setCellValue(value(meter != null ? meter.getDiameterSize() : null));
				row.createCell(11)
						.setCellValue(meter != null && meter.getLiterPerPulse() != null ? meter.getLiterPerPulse() : 0);
				row.createCell(12).setCellValue(value(meter != null ? meter.getCtRatio() : null));
				row.createCell(13).setCellValue(value(meter != null ? meter.getPtRatio() : null));
				row.createCell(14).setCellValue(value(meter != null ? meter.getVoltageClass() : null));
				row.createCell(15).setCellValue(value(meter != null ? meter.getInverterType() : null));
				row.createCell(16).setCellValue(value(meter != null ? meter.getPlantCapacity() : null));
				row.createCell(17)
						.setCellValue(meter != null && meter.getPanelCount() != null ? meter.getPanelCount() : 0);
				row.createCell(18).setCellValue(
						meter != null && meter.getMeterStartReading() != null ? meter.getMeterStartReading() : 0);

				row.createCell(19).setCellValue(value(device.getCustomerName()));
				row.createCell(20).setCellValue(value(device.getCustomerAddress()));
				row.createCell(21).setCellValue(value(device.getBuildingOrWing()));
				row.createCell(22).setCellValue(value(device.getArea()));
				row.createCell(23).setCellValue(value(device.getZone()));
				row.createCell(24).setCellValue(value(device.getCity()));
				row.createCell(25).setCellValue(value(device.getState()));
				row.createCell(26).setCellValue(value(device.getMeterLocation()));

				row.createCell(27).setCellValue(value(device.getWakeupTime()));
				row.createCell(28).setCellValue(device.getDataSampleCount() != null ? device.getDataSampleCount() : 0);

				row.createCell(29).setCellValue(device.getAssignedAdmin() != null
						? device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName()
						: "");

				row.createCell(30)
						.setCellValue(device.getAssignedUser() != null
								? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
								: "");

				row.createCell(31).setCellValue(device.getOnline() != null ? device.getOnline().toString() : "");
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();

		} catch (Exception e) {
			throw new BadRequestException ("Failed to export devices Excel", e);
		}
	}

	private byte[] exportDevicesToPdf(List<Device> devices) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Document document = new Document(PageSize.A3.rotate());
			PdfWriter.getInstance(document, out);

			document.open();

			document.add(new Paragraph("Devices Export"));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(32);
			table.setWidthPercentage(100);

			table.addCell("deviceId");
			table.addCell("deviceName");
			table.addCell("macAddress");
			table.addCell("serialNumber");

			table.addCell("meterName");
			table.addCell("sourceType");
			table.addCell("technologyType");
			table.addCell("status");
			table.addCell("meterType");
			table.addCell("application");
			table.addCell("diameterSize");
			table.addCell("literPerPulse");
			table.addCell("ctRatio");
			table.addCell("ptRatio");
			table.addCell("voltageClass");
			table.addCell("inverterType");
			table.addCell("plantCapacity");
			table.addCell("panelCount");
			table.addCell("meterStartReading");

			table.addCell("customerName");
			table.addCell("customerAddress");
			table.addCell("buildingOrWing");
			table.addCell("area");
			table.addCell("zone");
			table.addCell("city");
			table.addCell("state");
			table.addCell("meterLocation");

			table.addCell("wakeupTime");
			table.addCell("dataSampleCount");

			table.addCell("assignedAdmin");
			table.addCell("assignedUser");
			table.addCell("online");

			for (Device device : devices) {

				Meter meter = device.getMeter();

				table.addCell(value(device.getDeviceId()));
				table.addCell(value(device.getDeviceName()));
				table.addCell(value(device.getMacAddress()));
				table.addCell(value(device.getSerialNumber()));

				table.addCell(value(meter != null ? meter.getMeterName() : null));
				table.addCell(
						value(meter != null && meter.getSourceType() != null ? meter.getSourceType().name() : null));
				table.addCell(value(
						meter != null && meter.getTechnologyType() != null ? meter.getTechnologyType().name() : null));
				table.addCell(value(meter != null && meter.getStatus() != null ? meter.getStatus().name() : null));
				table.addCell(value(meter != null ? meter.getMeterType() : null));
				table.addCell(value(meter != null ? meter.getApplication() : null));
				table.addCell(value(meter != null ? meter.getDiameterSize() : null));
				table.addCell(value(meter != null ? meter.getLiterPerPulse() : null));
				table.addCell(value(meter != null ? meter.getCtRatio() : null));
				table.addCell(value(meter != null ? meter.getPtRatio() : null));
				table.addCell(value(meter != null ? meter.getVoltageClass() : null));
				table.addCell(value(meter != null ? meter.getInverterType() : null));
				table.addCell(value(meter != null ? meter.getPlantCapacity() : null));
				table.addCell(value(meter != null ? meter.getPanelCount() : null));
				table.addCell(value(meter != null ? meter.getMeterStartReading() : null));

				table.addCell(value(device.getCustomerName()));
				table.addCell(value(device.getCustomerAddress()));
				table.addCell(value(device.getBuildingOrWing()));
				table.addCell(value(device.getArea()));
				table.addCell(value(device.getZone()));
				table.addCell(value(device.getCity()));
				table.addCell(value(device.getState()));
				table.addCell(value(device.getMeterLocation()));

				table.addCell(value(device.getWakeupTime()));
				table.addCell(value(device.getDataSampleCount()));

				table.addCell(device.getAssignedAdmin() != null ? value(
						device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName()) : "");

				table.addCell(device.getAssignedUser() != null
						? value(device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName())
						: "");

				table.addCell(value(device.getOnline()));
			}

			document.add(table);
			document.close();

			return out.toByteArray();

		} catch (Exception e) {
			throw new BadRequestException ("Failed to export devices PDF", e);
		}
	}

	private String value(Object value) {
		return value != null ? value.toString() : "";
	}

	@Override
	public byte[] exportDevicesToCsv() {

		User loggedInUser = getLoggedInUser();

		List<Device> devices;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			devices = deviceRepository.findAll();

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {

			devices = deviceRepository.findByAssignedAdminId(loggedInUser.getId());

		} else if (loggedInUser.getRole() == RoleType.USER) {

			devices = deviceRepository.findByAssignedUserId(loggedInUser.getId());

		} else {

			throw new UnauthorizedException("Access Denied");
		}

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)) {

			csvPrinter.printRecord("deviceId", "deviceName", "macAddress", "serialNumber",

					"meterName", "sourceType", "technologyType", "status", "meterType", "application", "diameterSize",
					"literPerPulse", "ctRatio", "ptRatio", "voltageClass", "inverterType", "plantCapacity",
					"panelCount", "meterStartReading",

					"customerName", "customerAddress", "buildingOrWing", "area", "zone", "city", "state",
					"meterLocation",

					"wakeupTime", "dataSampleCount",

					"assignedAdmin", "assignedUser", "online");

			for (Device device : devices) {

				Meter meter = device.getMeter();

				csvPrinter.printRecord(device.getDeviceId(), device.getDeviceName(), device.getMacAddress(),
						device.getSerialNumber(),

						meter != null ? meter.getMeterName() : null, meter != null ? meter.getSourceType() : null,
						meter != null ? meter.getTechnologyType() : null, meter != null ? meter.getStatus() : null,
						meter != null ? meter.getMeterType() : null, meter != null ? meter.getApplication() : null,
						meter != null ? meter.getDiameterSize() : null, meter != null ? meter.getLiterPerPulse() : null,
						meter != null ? meter.getCtRatio() : null, meter != null ? meter.getPtRatio() : null,
						meter != null ? meter.getVoltageClass() : null, meter != null ? meter.getInverterType() : null,
						meter != null ? meter.getPlantCapacity() : null, meter != null ? meter.getPanelCount() : null,
						meter != null ? meter.getMeterStartReading() : null,

						device.getCustomerName(), device.getCustomerAddress(), device.getBuildingOrWing(),
						device.getArea(), device.getZone(), device.getCity(), device.getState(),
						device.getMeterLocation(),

						device.getWakeupTime(), device.getDataSampleCount(),

						device.getAssignedAdmin() != null
								? device.getAssignedAdmin().getFirstName() + " "
										+ device.getAssignedAdmin().getLastName()
								: "",

						device.getAssignedUser() != null
								? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
								: "",

						device.getOnline());
			}

			csvPrinter.flush();

			return out.toByteArray();

		} catch (Exception e) {

			throw new RuntimeException("Failed to export devices", e);
		}
	}
	// =====================================================
	// Water Module
	// =====================================================

	@Override
	public Page<InventoryResponseDto> getInventory(

	        int page,

	        int size,

	        String search,

	        SourceType sourceType,

	        DeviceStatus status,

	        String sortBy,

	        String direction) {
		
		validatePagination(
		        page,
		        size,
		        sortBy,
		        direction);

	    Sort sort = direction.equalsIgnoreCase("ASC")
	            ? Sort.by(sortBy).ascending()
	            : Sort.by(sortBy).descending();

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            sort);

	    Specification<Device> spec = (root, query, cb) -> cb.conjunction();

	    if (search != null && !search.isBlank()) {

	        spec = spec.and((root, query, cb) ->
	                cb.or(

	                        cb.like(
	                                cb.lower(root.get("deviceId")),
	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(
	                                cb.lower(root.get("deviceName")),
	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(
	                                cb.lower(root.get("serialNumber")),
	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(
	                                cb.lower(root.get("meterNumber")),
	                                "%" + search.toLowerCase() + "%")

	                ));
	    }

	    if (sourceType != null) {

	        spec = spec.and((root, query, cb) ->
	                cb.equal(
	                        root.get("sourceType"),
	                        sourceType));
	    }

	    if (status != null) {

	        spec = spec.and((root, query, cb) ->
	                cb.equal(
	                        root.get("status"),
	                        status));
	    }

	    return deviceRepository.findAll(
	            spec,
	            pageable)
	            .map(device -> InventoryResponseDto.builder()

	                    .deviceId(device.getDeviceId())

	                    .deviceName(device.getDeviceName())

	                    .serialNumber(device.getSerialNumber())

	                    .meterNumber(device.getMeterNumber())

	                    .imei(device.getImei())

	                    .source(device.getSourceType().name())

	                    .status(device.getStatus().name())

	                    .location(device.getLocation())

	                    .zone(device.getZone())

	                    .installationDate(device.getInstallationDate())

	                    .build());
	}
	@Override
	public Page<LocationResponseDto> getLocations(

	        int page,

	        int size,

	        String search,

	        String zone,

	        String location,

	        Boolean online,

	        String sortBy,

	        String direction) {
		
		validatePagination(
		        page,
		        size,
		        sortBy,
		        direction);

	    Sort sort = direction.equalsIgnoreCase("ASC")
	            ? Sort.by(sortBy).ascending()
	            : Sort.by(sortBy).descending();

	    Pageable pageable =
	            PageRequest.of(page, size, sort);

	    Specification<Device> spec =
	            (root, query, cb) -> cb.conjunction();

	    if (search != null && !search.isBlank()) {

	        spec = spec.and((root, query, cb) ->
	                cb.or(

	                        cb.like(
	                                cb.lower(root.get("deviceId")),
	                                "%" + search.toLowerCase() + "%"),

	                        cb.like(
	                                cb.lower(root.get("deviceName")),
	                                "%" + search.toLowerCase() + "%")

	                ));
	    }

	    if (zone != null && !zone.isBlank()) {

	        spec = spec.and((root, query, cb) ->
	                cb.equal(
	                        root.get("zone"),
	                        zone));
	    }

	    if (location != null && !location.isBlank()) {

	        spec = spec.and((root, query, cb) ->
	                cb.equal(
	                        root.get("location"),
	                        location));
	    }

	    if (online != null) {

	        spec = spec.and((root, query, cb) ->
	                cb.equal(
	                        root.get("online"),
	                        online));
	    }

	    return deviceRepository.findAll(spec, pageable)
	            .map(device -> LocationResponseDto.builder()

	                    .deviceId(device.getDeviceId())

	                    .deviceName(device.getDeviceName())

	                    .location(device.getLocation())

	                    .zone(device.getZone())

	                    .latitude(device.getLatitude())

	                    .longitude(device.getLongitude())

	                    .online(device.getOnline())

	                    .build());
	}
	@Override
	public Page<DeviceResponseDto> getDevicesByZone(

	        String zone,

	        int page,

	        int size,

	        String sortBy,

	        String direction) {
		
		validatePagination(
		        page,
		        size,
		        sortBy,
		        direction);

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by(
	                    direction.equalsIgnoreCase("ASC")
	                            ? Sort.Direction.ASC
	                            : Sort.Direction.DESC,
	                    sortBy));

	    Specification<Device> spec =
	            (root, query, cb) ->
	                    cb.equal(root.get("zone"), zone);

	    return deviceRepository.findAll(spec, pageable)
	            .map(this::mapToResponse);
	}
	@Override
	public Page<DeviceResponseDto> getDevicesByLocation(

	        String location,

	        int page,

	        int size,

	        String sortBy,

	        String direction) {
		
		validatePagination(
		        page,
		        size,
		        sortBy,
		        direction);

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by(
	                    direction.equalsIgnoreCase("ASC")
	                            ? Sort.Direction.ASC
	                            : Sort.Direction.DESC,
	                    sortBy));

	    Specification<Device> spec =
	            (root, query, cb) ->
	                    cb.equal(root.get("location"), location);

	    return deviceRepository.findAll(spec, pageable)
	            .map(this::mapToResponse);
	}
	@Override
	public Page<DeviceResponseDto> getDevicesBySource(

	        SourceType sourceType,

	        int page,

	        int size,

	        String sortBy,

	        String direction) {
		
		validatePagination(
		        page,
		        size,
		        sortBy,
		        direction);

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by(
	                    direction.equalsIgnoreCase("ASC")
	                            ? Sort.Direction.ASC
	                            : Sort.Direction.DESC,
	                    sortBy));

	    Specification<Device> spec =
	            (root, query, cb) ->
	                    cb.equal(root.get("sourceType"), sourceType);

	    return deviceRepository.findAll(spec, pageable)
	            .map(this::mapToResponse);
	}
	@Override
	public FirmwareResponseDto getFirmware(
	        String deviceId) {

	    Device device = deviceRepository.findByDeviceId(deviceId)
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Device not found"));

	    return FirmwareResponseDto.builder()

	            .deviceId(device.getDeviceId())

	            .firmwareVersion(device.getFirmwareVersion())

	            .latestVersion(device.getFirmwareVersion())

	            .updateAvailable(false)

	            .updateStatus("UP_TO_DATE")

	            .lastUpdatedAt(device.getUpdatedAt())

	            .build();
	}

	@Override
	public ConfigurationResponseDto getConfiguration(
	        String deviceId) {

	    Device device = deviceRepository.findByDeviceId(deviceId)
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Device not found"));

	    return ConfigurationResponseDto.builder()

	            .deviceId(device.getDeviceId())

	            .sampleCount(device.getSampleCount())

	            .wakeupTime(device.getWakeupTime())

	            .timezone(device.getTimezone())

	            .otaUpdatesEnabled(device.getOtaUpdatesEnabled())

	            .protocolType(device.getProtocolType().name())

	            .firmwareVersion(device.getFirmwareVersion())

	            .build();
	}
	@Override
	public ConfigurationResponseDto updateConfiguration(
	        String deviceId,
	        UpdateConfigurationRequestDto request) {

	    Device device = deviceRepository.findByDeviceId(deviceId)
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Device not found"));

	    device.setSampleCount(request.getSampleCount());

	    device.setWakeupTime(request.getWakeupTime());

	    device.setTimezone(request.getTimezone());

	    device.setOtaUpdatesEnabled(request.getOtaUpdatesEnabled());

	    if (request.getProtocolType() != null &&
	            !request.getProtocolType().isBlank()) {

	        device.setProtocolType(
	                ProtocolType.valueOf(request.getProtocolType()));
	    }

	    deviceRepository.save(device);

	    return ConfigurationResponseDto.builder()

	            .deviceId(device.getDeviceId())

	            .sampleCount(device.getSampleCount())

	            .wakeupTime(device.getWakeupTime())

	            .timezone(device.getTimezone())

	            .otaUpdatesEnabled(device.getOtaUpdatesEnabled())

	            .protocolType(device.getProtocolType().name())

	            .firmwareVersion(device.getFirmwareVersion())

	            .build();
	}
	
	@Override
	public List<FirmwareHistoryResponseDto> getFirmwareHistory(
	        String deviceId) {

	    return firmwareHistoryRepository
	            .findByDeviceIdOrderByUpdatedAtDesc(deviceId)
	            .stream()
	            .map(history -> FirmwareHistoryResponseDto.builder()

	                    .id(history.getId())

	                    .deviceId(history.getDeviceId())

	                    .firmwareVersion(history.getFirmwareVersion())

	                    .previousVersion(history.getPreviousVersion())

	                    .updateStatus(history.getUpdateStatus())

	                    .updatedBy(history.getUpdatedBy())

	                    .updatedAt(history.getUpdatedAt())

	                    .remarks(history.getRemarks())

	                    .build())

	            .toList();
	}
	@Override
	public FirmwareResponseDto updateFirmware(
	        String deviceId,
	        UpdateFirmwareRequestDto request) {

	    Device device = deviceRepository.findByDeviceId(deviceId)
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Device not found"));

	    String previousVersion =
	            device.getFirmwareVersion();

	    device.setFirmwareVersion(
	            request.getFirmwareVersion());

	    deviceRepository.save(device);

	    FirmwareHistory history =
	            FirmwareHistory.builder()

	                    .deviceId(device.getDeviceId())

	                    .previousVersion(previousVersion)

	                    .firmwareVersion(
	                            device.getFirmwareVersion())

	                    .updateStatus("SUCCESS")

	                    .updatedBy("SYSTEM")

	                    .updatedAt(LocalDateTime.now())

	                    .remarks(request.getRemarks())

	                    .build();

	    firmwareHistoryRepository.save(history);

	    return FirmwareResponseDto.builder()

	            .deviceId(device.getDeviceId())

	            .firmwareVersion(
	                    device.getFirmwareVersion())

	            .latestVersion(
	                    device.getFirmwareVersion())

	            .updateAvailable(false)

	            .updateStatus("SUCCESS")

	            .lastUpdatedAt(
	                    history.getUpdatedAt())

	            .build();
	}
	@Override
	public DeviceHealthResponseDto getDeviceHealth(
	        String deviceId) {

	    Device device = deviceRepository.findByDeviceId(deviceId)
	            .stream()
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Device not found"));

	    DeviceTelemetry telemetry =
	            deviceTelemetryRepository
	                    .findTopByDeviceIdOrderByReadingTimeDesc(deviceId)
	                    .orElseThrow(() ->
	                            new ResourceNotFoundException("Telemetry not found"));

	    int healthScore = 100;

	    if (telemetry.getBatteryLevel() != null
	            && telemetry.getBatteryLevel() < 20) {

	        healthScore -= 20;
	    }

	    if (telemetry.getSignalStrength() != null
	            && telemetry.getSignalStrength() < 30) {

	        healthScore -= 20;
	    }

	    if (Boolean.TRUE.equals(
	            telemetry.getLeakDetected())) {

	        healthScore -= 30;
	    }

	    if (Boolean.TRUE.equals(
	            telemetry.getTamperDetected())) {

	        healthScore -= 30;
	    }

	    String overallHealth;

	    if (healthScore >= 80) {

	        overallHealth = "GOOD";

	    } else if (healthScore >= 50) {

	        overallHealth = "WARNING";

	    } else {

	        overallHealth = "CRITICAL";
	    }

	    String recommendation;

	    switch (overallHealth) {

	        case "GOOD":
	            recommendation = "Device operating normally";
	            break;

	        case "WARNING":
	            recommendation = "Inspection recommended";
	            break;

	        default:
	            recommendation = "Immediate maintenance required";
	    }

	    return DeviceHealthResponseDto.builder()

	            .deviceId(device.getDeviceId())

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
	                    telemetry.getPumpStatus() == null
	                            ? null
	                            : telemetry.getPumpStatus().name())

	            .communicationStatus(
	                    telemetry.getDeviceOnline())

	            .firmwareVersion(
	                    device.getFirmwareVersion())

	            .lastCommunication(
	                    telemetry.getReadingTime())

	            .healthScore(
	                    healthScore)

	            .overallHealth(
	                    overallHealth)

	            .recommendation(
	                    recommendation)

	            .build();
	}

}