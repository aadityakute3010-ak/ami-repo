package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ami.dto.requests.CreateDeviceRequestDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceLocationRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceListResponseDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.entity.Device;
import com.ami.entity.DeviceAudit;
import com.ami.entity.Meter;
import com.ami.entity.User;
import com.ami.enums.DeviceStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.DeviceAuditRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.UserRepository;
import com.ami.service.DeviceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

	private final DeviceRepository deviceRepository;

	private final UserRepository userRepository;

	private final DeviceAuditRepository deviceAuditRepository;

	private User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
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
				throw new RuntimeException("Duplicate deviceId in request: " + deviceId);
			}

			if (!macs.add(macAddress)) {
				throw new RuntimeException("Duplicate macAddress in request: " + macAddress);
			}

			if (!serials.add(serialNumber)) {
				throw new RuntimeException("Duplicate serialNumber in request: " + serialNumber);
			}
		}
	}

	private User resolveUser(Long id) {
		if (id == null)
			return null;
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found: " + id));
	}

	private void validateDeviceExists(CreateDeviceRequestDto dto) {

		String deviceId = dto.getDevice().getDeviceId();
		String macAddress = dto.getDevice().getMacAddress();
		String serialNumber = dto.getDevice().getSerialNumber();

		if (deviceRepository.existsByDeviceId(deviceId)) {
			throw new RuntimeException("DeviceId already exists: " + deviceId);
		}

		if (deviceRepository.existsByMacAddress(macAddress)) {
			throw new RuntimeException("MAC already exists: " + macAddress);
		}

		if (deviceRepository.existsBySerialNumber(serialNumber)) {
			throw new RuntimeException("Serial already exists: " + serialNumber);
		}
	}

	private Device buildDevice(CreateDeviceRequestDto request, User assignedAdmin, User assignedUser, User superAdmin) {

		Meter meter = Meter.builder().meterName(request.getMeter().getMeterName())
				.sourceType(request.getMeter().getSourceType()).technologyType(request.getMeter().getTechnologyType())

				.applicationOfAmi(request.getMeter().getApplicationOfAmi())
				.amiApplicationType(request.getMeter().getAmiApplicationType())

				.diameterSize(request.getMeter().getDiameterSize())

				.literPerPulse(request.getMeter().getLiterPerPulse())

				.meterStartReading(request.getMeter().getMeterStartReading())

				.status(DeviceStatus.ACTIVE).build();

		Device device = Device.builder()

				.deviceId(request.getDevice().getDeviceId())

				.deviceName(request.getDevice().getDeviceName())

				.macAddress(request.getDevice().getMacAddress())

				.serialNumber(request.getDevice().getSerialNumber())

				.billingType(request.getBillingType())

				.customerName(request.getCustomer().getCustomerName())

				.customerAddress(request.getCustomer().getCustomerAddress())

				.buildingOrWing(request.getCustomer().getBuildingOrWing())

				.area(request.getCustomer().getArea())

				.zone(request.getCustomer().getZone())

				.city(request.getCustomer().getCity())

				.state(request.getCustomer().getState())

				.meterLocation(request.getCustomer().getMeterLocation())

				.active(true)

				.online(false)

				.createdBy(superAdmin)

				.assignedAdmin(assignedAdmin)

				.assignedUser(assignedUser)

				.meter(meter)

				.build();

		meter.setDevice(device);

		return device;
	}

	@Override
	@Transactional
	public List<DeviceResponseDto> createDevices(CreateDevicesRequestDto request) {

		User superAdmin = getLoggedInUser();

		if (superAdmin.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can create devices");
		}

		User assignedAdmin = resolveUser(request.getAssignedAdminId());
		User assignedUser = resolveUser(request.getAssignedUserId());

		validateDuplicateDevicesInRequest(request.getDevices());

		List<Device> devicesToSave = new ArrayList<>();

		for (CreateDeviceRequestDto dto : request.getDevices()) {

			validateDeviceExists(dto);

			devicesToSave.add(buildDevice(dto, assignedAdmin, assignedUser, superAdmin));
		}

		List<Device> savedDevices = deviceRepository.saveAll(devicesToSave);

		return savedDevices.stream().map(this::mapToResponse).toList();
	}

	private DeviceResponseDto mapToResponse(Device device) {

		return DeviceResponseDto.builder().id(device.getId()).deviceId(device.getDeviceId())
				.deviceName(device.getDeviceName())
				.meterName(device.getMeter() != null ? device.getMeter().getMeterName() : null)

				// Device Information
				.technologyType(device.getMeter() != null ? device.getMeter().getTechnologyType() : null)
				.sourceType(device.getMeter() != null ? device.getMeter().getSourceType() : null)
				.macAddress(device.getMacAddress()).serialNumber(device.getSerialNumber())
				.billingType(device.getBillingType())

				// Status
				.status(device.getMeter() != null ? device.getMeter().getStatus() : null).active(device.getActive())
				.online(device.getOnline())

				// Customer Information
				.customerName(device.getCustomerName()).customerAddress(device.getCustomerAddress())
				.buildingOrWing(device.getBuildingOrWing()).area(device.getArea()).zone(device.getZone())
				.city(device.getCity()).state(device.getState()).meterLocation(device.getMeterLocation())

				// Meter Information
				.applicationOfAmi(device.getMeter() != null ? device.getMeter().getApplicationOfAmi() : null)
				.amiApplicationType(device.getMeter() != null ? device.getMeter().getAmiApplicationType() : null)
				.diameterSize(device.getMeter() != null ? device.getMeter().getDiameterSize() : null)
				.literPerPulse(device.getMeter() != null ? device.getMeter().getLiterPerPulse() : null)
				.meterStartReading(device.getMeter() != null ? device.getMeter().getMeterStartReading() : null)
				.lastSyncTime(device.getLastSyncTime())

				.assignedAdminName(device.getAssignedAdmin() != null
						? device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName()
						: null)

				.assignedUserName(device.getAssignedUser() != null
						? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
						: null)

				.createdAt(device.getCreatedAt()).build();
	}

	@Override
	public PagedDeviceResponseDto getDevices(int page, int size, String search, DeviceStatus status,
			SourceType sourceType, TechnologyType technologyType) {

		User loggedInUser = getLoggedInUser();
		Pageable pageable = PageRequest.of(page, size);

		Long adminId = null;
		Long userId = null;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			// SUPER ADMIN can see all devices
			adminId = null;
			userId = null;

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {
			// ADMIN can see only devices assigned to him
			adminId = loggedInUser.getId();

		} else if (loggedInUser.getRole() == RoleType.USER) {
			// USER can see only devices assigned to him
			userId = loggedInUser.getId();

		} else {
			throw new RuntimeException("Access Denied");
		}

		Page<Device> devicePage = deviceRepository.findDevicesWithFilters(adminId, userId, search, status, sourceType,
				technologyType, pageable);

		PagedDeviceResponseDto response = new PagedDeviceResponseDto();
		response.setDevices(devicePage.getContent().stream().map(this::mapToDeviceListResponse).toList());
		response.setCurrentPage(devicePage.getNumber());
		response.setTotalPages(devicePage.getTotalPages());
		response.setTotalElements(devicePage.getTotalElements());
		return response;
	}

	private DeviceListResponseDto mapToDeviceListResponse(Device device) {

		Meter meter = device.getMeter();
		DeviceListResponseDto dto = new DeviceListResponseDto();
		dto.setId(device.getId());
		dto.setDeviceId(device.getDeviceId());
		dto.setDeviceName(device.getDeviceName());
		dto.setSourceType(meter != null ? meter.getSourceType() : null);
		dto.setTechnologyType(meter != null ? meter.getTechnologyType() : null);
		dto.setSerialNumber(device.getSerialNumber());
		dto.setMacAddress(device.getMacAddress());
		dto.setBillingType(device.getBillingType());
		dto.setStatus(meter != null ? meter.getStatus() : null);
		dto.setActive(device.getActive());
		dto.setOnline(device.getOnline());
		if (device.getAssignedAdmin() != null) {
			dto.setAssignedAdmin(
					device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName());
		}
		if (device.getAssignedUser() != null) {
			dto.setAssignedUser(device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName());
		}
		return dto;
	}

	@Override
	public DeviceResponseDto assignDeviceToUser(Long deviceId, Long userId) {

		User loggedInUser = getLoggedInUser();
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Cannot assign inactive device");
		}

		User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// ONLY ADMIN
		RoleType role = loggedInUser.getRole();
		if (role != RoleType.SUPER_ADMIN && role != RoleType.ADMIN) {
			throw new RuntimeException("Access Denied");
		}

		// ADMIN RULES
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot assign this device");
			}

			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot assign device to this user");
			}
		}

		// DEVICE ALREADY ASSIGNED CHECK
		if (device.getAssignedUser() != null) {
			throw new RuntimeException(
					"Device is already assigned to user: " + device.getAssignedUser().getFirstName());
		}

		// Checks if the User have the source of that Device Type
		Meter meter = device.getMeter();
		if (meter == null) {
			throw new RuntimeException("Meter not configured for device");
		}
		SourceType sourceType = meter.getSourceType();
		if (!targetUser.getAssignedSources().contains(sourceType)) {
			throw new RuntimeException("User does not have access to source: " + sourceType);
		}
		device.setAssignedUser(targetUser);
		Device updatedDevice = deviceRepository.save(device);
		return mapToResponse(updatedDevice);
	}

	public List<DeviceResponseDto> getAvailableDevicesForAssignment(Long userId) {

		User loggedInAdmin = getLoggedInUser();
		RoleType role = loggedInAdmin.getRole();
		if (role != RoleType.SUPER_ADMIN && role != RoleType.ADMIN) {
			throw new RuntimeException("Access Denied");
		}

		User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// USER MUST BELONG TO ADMIN
		if (role == RoleType.ADMIN) {
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInAdmin.getId())) {
				throw new RuntimeException("You cannot access this user");
			}
		}

		List<Device> availableDevices;

		if (role == RoleType.SUPER_ADMIN) {
			availableDevices = deviceRepository.findAvailableDevicesForSuperAdmin(targetUser.getAssignedSources());
		} else {
			availableDevices = deviceRepository.findAvailableDevicesForUser(loggedInAdmin.getId(),
					targetUser.getAssignedSources());
		}
		return availableDevices.stream().filter(device -> Boolean.TRUE.equals(device.getActive()))
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
			throw new RuntimeException("Access Denied");
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
		return response;
	}

	@Override
	public DeviceDetailsResponseDto getDeviceDetails(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device not found");
		}

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			// allowed

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}

		} else if (loggedInUser.getRole() == RoleType.USER) {
			if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}

		} else {
			throw new RuntimeException("Access Denied");
		}

		return mapToDeviceDetailsResponse(device);
	}

	private DeviceDetailsResponseDto mapToDeviceDetailsResponse(Device device) {

		Meter meter = device.getMeter();

		return DeviceDetailsResponseDto.builder().id(device.getId()).deviceId(device.getDeviceId())
				.deviceName(device.getDeviceName()).meterName(meter != null ? meter.getMeterName() : null)

				// Device Information
				.sourceType(meter != null ? meter.getSourceType() : null)
				.technologyType(meter != null ? meter.getTechnologyType() : null)
				.status(meter != null ? meter.getStatus() : null)

				// Runtime
				.online(device.getOnline()).active(device.getActive()).lastSyncTime(device.getLastSyncTime())

				// Device Identity
				.macAddress(device.getMacAddress()).serialNumber(device.getSerialNumber())

				.billingType(device.getBillingType())

				// Assignment
				.assignedAdmin(device.getAssignedAdmin() != null
						? device.getAssignedAdmin().getFirstName() + " " + device.getAssignedAdmin().getLastName()
						: null)

				.assignedUser(device.getAssignedUser() != null
						? device.getAssignedUser().getFirstName() + " " + device.getAssignedUser().getLastName()
						: null)

				// Customer Information
				.customerName(device.getCustomerName()).customerAddress(device.getCustomerAddress())
				.buildingOrWing(device.getBuildingOrWing()).area(device.getArea()).zone(device.getZone())
				.city(device.getCity()).state(device.getState()).meterLocation(device.getMeterLocation())

				// Meter Configuration
				.applicationOfAmi(meter != null ? meter.getApplicationOfAmi() : null)
				.amiApplicationType(meter != null ? meter.getAmiApplicationType() : null)
				.diameterSize(meter != null ? meter.getDiameterSize() : null)
				.literPerPulse(meter != null ? meter.getLiterPerPulse() : null)
				.meterStartReading(meter != null ? meter.getMeterStartReading() : null).build();
	}

	@Override
	public void softDeleteDevice(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device already deleted");
		}

		// SUPER ADMIN
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			device.setActive(false);
		}

		// ADMIN
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot delete this device");
			}
			device.setActive(false);
		} else {
			throw new RuntimeException("Access Denied");
		}
		deviceRepository.save(device);
	}

	@Override
	public void hardDeleteDevice(Long deviceId) {

		User loggedInUser = getLoggedInUser();
		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can permanently delete devices");
		}

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		deviceRepository.delete(device);
	}

	@Override
	public PagedDeviceResponseDto getDeletedDevices(int page, int size) {

		User loggedInUser = getLoggedInUser();
		Pageable pageable = PageRequest.of(page, size);
		Page<Device> devicePage;

		// SUPER ADMIN -> all deleted devices
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			devicePage = deviceRepository.findByActiveFalse(pageable);
		}
		// ADMIN -> only his deleted devices
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			devicePage = deviceRepository.findByAssignedAdminIdAndActiveFalse(loggedInUser.getId(), pageable);
		} else {
			throw new RuntimeException("Access Denied");
		}

		PagedDeviceResponseDto response = new PagedDeviceResponseDto();
		response.setDevices(devicePage.getContent().stream().map(this::mapToDeviceListResponse).toList());
		response.setCurrentPage(devicePage.getNumber());
		response.setTotalPages(devicePage.getTotalPages());
		response.setTotalElements(devicePage.getTotalElements());

		return response;
	}

	@Override
	public void restoreDevice(Long deviceId) {
		User loggedInUser = getLoggedInUser();
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			device.setActive(true);
			saveAudit(device, "DEVICE_RESTORED", "Device restored from recycle bin",
					loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
		} else if (loggedInUser.getRole() == RoleType.ADMIN) {
			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			device.setActive(true);
			saveAudit(device, "DEVICE_RESTORED", "Device restored from recycle bin",
					loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
		} else {
			throw new RuntimeException("Access Denied");
		}
		deviceRepository.save(device);
	}

	@Override
	public DeviceUpdateFormResponseDto getDeviceForUpdate(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
		}

		Meter meter = device.getMeter();
		if (meter == null) {
			throw new RuntimeException("Meter not configured for device");
		}

		return DeviceUpdateFormResponseDto.builder()

				// Device Information
				.deviceName(device.getDeviceName()).meterName(meter.getMeterName()).billingType(device.getBillingType())

				// Customer Information
				.customerName(device.getCustomerName()).customerAddress(device.getCustomerAddress())
				.buildingOrWing(device.getBuildingOrWing()).area(device.getArea()).zone(device.getZone())
				.city(device.getCity()).state(device.getState()).meterLocation(device.getMeterLocation())

				// Meter Configuration
				.applicationOfAmi(meter.getApplicationOfAmi()).amiApplicationType(meter.getAmiApplicationType())
				.diameterSize(meter.getDiameterSize()).literPerPulse(meter.getLiterPerPulse()).build();
	}

	@Override
	public DeviceResponseDto updateDevice(Long deviceId, UpdateDeviceRequestDto request) {

		User loggedInUser = getLoggedInUser();

		if (loggedInUser.getRole() != RoleType.ADMIN && loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Admin or Super Admin can update device");
		}

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (!device.getActive()) {
			throw new RuntimeException("Cannot update a deleted device");
		}

		// ADMIN can update only his devices
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You can update only your own devices");
			}
		}

		Meter meter = device.getMeter();
		if (meter == null) {
			throw new RuntimeException("Meter not configured for device");
		}

		// Device Information
		device.setDeviceName(request.getDeviceName());

		device.setBillingType(request.getBillingType());

		// Customer Information
		device.setCustomerName(request.getCustomerName());
		device.setCustomerAddress(request.getCustomerAddress());

		device.setBuildingOrWing(request.getBuildingOrWing());
		device.setArea(request.getArea());
		device.setZone(request.getZone());

		device.setCity(request.getCity());
		device.setState(request.getState());

		device.setMeterLocation(request.getMeterLocation());

		meter.setMeterName(request.getMeterName());

		meter.setApplicationOfAmi(request.getApplicationOfAmi());

		meter.setAmiApplicationType(request.getAmiApplicationType());

		meter.setDiameterSize(request.getDiameterSize());

		meter.setLiterPerPulse(request.getLiterPerPulse());

		Device updatedDevice = deviceRepository.save(device);

		saveAudit(updatedDevice, "DEVICE_UPDATED", "Device configuration updated",
				loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

		return mapToResponse(updatedDevice);
	}

	@Override
	public DeviceResponseDto updateDeviceLocation(Long deviceId, UpdateDeviceLocationRequestDto request) {

		User loggedInUser = getLoggedInUser();
		if (loggedInUser.getRole() != RoleType.USER) {
			throw new RuntimeException("Only User can update location");
		}

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
			throw new RuntimeException("You can update only your own device");
		}

		device.setMeterLocation(request.getMeterLocation());
		device.setBuildingOrWing(request.getBuildingOrWing());
		device.setArea(request.getArea());
		device.setZone(request.getZone());
		Device updatedDevice = deviceRepository.save(device);
		saveAudit(device, "DEVICE_UPDATED", "Device configuration updated",
				loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
		return mapToResponse(updatedDevice);
	}

	private void saveAudit(Device device, String action, String description, String performedBy) {
		DeviceAudit audit = DeviceAudit.builder().device(device).action(action).description(description)
				.performedBy(performedBy).actionTime(LocalDateTime.now()).build();

		deviceAuditRepository.save(audit);
	}

	@Override
	public List<DeviceAuditResponseDto> getDeviceAudit(Long deviceId) {

		User loggedInUser = getLoggedInUser();
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		// Access Validation
		if (loggedInUser.getRole() == RoleType.ADMIN
				&& !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
			throw new RuntimeException("Access Denied");
		}
		if (loggedInUser.getRole() == RoleType.USER && (device.getAssignedUser() == null
				|| !device.getAssignedUser().getId().equals(loggedInUser.getId()))) {
			throw new RuntimeException("Access Denied");
		}
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
		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can assign admin");
		}
		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found"));

		if (device.getAssignedAdmin() != null) {
			throw new RuntimeException("Device is already assigned to an admin");
		}

		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
		if (admin.getRole() != RoleType.ADMIN) {
			throw new RuntimeException("Selected user is not an admin");
		}
		validateAdminSourceAccess(device, admin);
		device.setAssignedAdmin(admin);
		deviceRepository.save(device);
	}

	private void validateAdminSourceAccess(Device device, User admin) {

		if (device.getMeter() == null) {
			throw new RuntimeException("Meter not configured for device");
		}
		SourceType sourceType = device.getMeter().getSourceType();
		if (admin.getAssignedSources() == null || !admin.getAssignedSources().contains(sourceType)) {
			throw new RuntimeException("Admin is not assigned to source type " + sourceType);
		}
	}

}