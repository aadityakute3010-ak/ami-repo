package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.AssignDeviceTariffRequest;
import com.ami.dto.responses.DeviceTariffAssignmentResponseDto;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.entity.Device;
import com.ami.entity.DeviceTariffAssignment;
import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.TariffStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.TariffMapper;
import com.ami.repository.DeviceRepository;
import com.ami.repository.DeviceTariffAssignmentRepository;
import com.ami.repository.TariffRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.DeviceTariffAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceTariffAssignmentServiceImpl implements DeviceTariffAssignmentService {

	private final DeviceTariffAssignmentRepository assignmentRepository;

	private final DeviceRepository deviceRepository;

	private final TariffRepository tariffRepository;

	private final TariffMapper tariffMapper;

	private final SecurityUtils securityUtils;

	@Override
	@Transactional(readOnly = true)
	public List<TariffResponseDto> getApplicableTariffs(Long deviceId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = findDevice(deviceId);

		validateManagementAccess(device, loggedInUser);

		if (device.getMeter() == null || device.getMeter().getSourceType() == null) {

			throw new IllegalStateException("Device source type is not configured");
		}

		return tariffRepository
				.findBySourceAndStatusOrderByNameAsc(device.getMeter().getSourceType(), TariffStatus.ACTIVE).stream()
				.map(tariffMapper::toResponseDto).toList();
	}

	@Override
	@Transactional
	public DeviceTariffAssignmentResponseDto assignTariff(Long deviceId, AssignDeviceTariffRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = findDevice(deviceId);

		validateManagementAccess(device, loggedInUser);

		if (assignmentRepository.existsByDeviceId(deviceId)) {
			throw new IllegalArgumentException("A tariff is already assigned to this device");
		}

		Tariff tariff = findAndValidateTariff(request.getTariffId(), device);

		DeviceTariffAssignment assignment = DeviceTariffAssignment.builder().device(device).tariff(tariff).active(true)
				.assignedBy(loggedInUser).build();

		return mapToResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional(readOnly = true)
	public DeviceTariffAssignmentResponseDto getAssignedTariff(Long deviceId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = findDevice(deviceId);

		validateViewAccess(device, loggedInUser);

		DeviceTariffAssignment assignment = assignmentRepository.findByDeviceIdAndActiveTrue(deviceId).orElseThrow(
				() -> new ResourceNotFoundException("No active tariff is assigned to device: " + device.getDeviceId()));

		return mapToResponse(assignment);
	}

	@Override
	@Transactional
	public DeviceTariffAssignmentResponseDto updateAssignedTariff(Long deviceId, AssignDeviceTariffRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = findDevice(deviceId);

		validateManagementAccess(device, loggedInUser);

		DeviceTariffAssignment assignment = assignmentRepository.findByDeviceId(deviceId).orElseThrow(
				() -> new ResourceNotFoundException("No tariff assignment found for device: " + device.getDeviceId()));

		Tariff tariff = findAndValidateTariff(request.getTariffId(), device);

		assignment.setTariff(tariff);
		assignment.setActive(true);
		assignment.setAssignedBy(loggedInUser);

		return mapToResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional
	public void removeAssignedTariff(Long deviceId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = findDevice(deviceId);

		validateManagementAccess(device, loggedInUser);

		DeviceTariffAssignment assignment = assignmentRepository.findByDeviceId(deviceId).orElseThrow(
				() -> new ResourceNotFoundException("No tariff assignment found for device: " + device.getDeviceId()));

		assignment.setActive(false);

		assignmentRepository.save(assignment);
	}

	private Device findDevice(Long deviceId) {

		return deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));
	}

	private Tariff findAndValidateTariff(Long tariffId, Device device) {

		Tariff tariff = tariffRepository.findById(tariffId)
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + tariffId));

		if (tariff.getStatus() != TariffStatus.ACTIVE) {
			throw new IllegalArgumentException("Only an active tariff can be assigned");
		}

		if (device.getMeter() == null || device.getMeter().getSourceType() == null) {

			throw new IllegalStateException("Device source type is not configured");
		}

		if (tariff.getSource() != device.getMeter().getSourceType()) {

			throw new IllegalArgumentException("Tariff source " + tariff.getSource() + " does not match device source "
					+ device.getMeter().getSourceType());
		}

		return tariff;
	}

	private void validateManagementAccess(Device device, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN && device.getAssignedAdmin() != null
				&& device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {

			return;
		}

		throw new SecurityException("You are not allowed to configure billing for this device");
	}

	private void validateViewAccess(Device device, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN && device.getAssignedAdmin() != null
				&& device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {

			return;
		}

		if (loggedInUser.getRole() == RoleType.USER && device.getAssignedUser() != null
				&& device.getAssignedUser().getId().equals(loggedInUser.getId())) {

			return;
		}

		throw new SecurityException("You do not have access to this device");
	}

	private DeviceTariffAssignmentResponseDto mapToResponse(DeviceTariffAssignment assignment) {

		Device device = assignment.getDevice();

		Tariff tariff = assignment.getTariff();

		User assignedBy = assignment.getAssignedBy();

		return DeviceTariffAssignmentResponseDto.builder().assignmentId(assignment.getId()).deviceId(device.getId())
				.deviceNumber(device.getDeviceId()).deviceName(device.getDeviceName())
				.meterName(device.getMeter() != null ? device.getMeter().getMeterName() : null)
				.sourceType(device.getMeter() != null ? device.getMeter().getSourceType() : null)
				.billingType(device.getBillingType()).tariffId(tariff.getId()).tariffName(tariff.getName())
				.tariffCategory(tariff.getCategory()).unit(tariff.getUnit()).baseRate(tariff.getRate())
				.fixedCharge(tariff.getFixedCharge()).tax(tariff.getTax()).tariffStatus(tariff.getStatus())
				.active(assignment.getActive()).assignedById(assignedBy.getId())
				.assignedByName(assignedBy.getFirstName() + " " + assignedBy.getLastName())
				.createdAt(assignment.getCreatedAt()).updatedAt(assignment.getUpdatedAt()).build();
	}
}