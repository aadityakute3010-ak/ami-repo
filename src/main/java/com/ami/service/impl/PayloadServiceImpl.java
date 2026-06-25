package com.ami.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.responses.HourlyReadingDTO;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadStatsDTO;
import com.ami.dto.responses.PayloadSummaryDTO;
import com.ami.entity.Device;
import com.ami.entity.Payload;
import com.ami.entity.User;
import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.PayloadStatus;
import com.ami.enums.RoleType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.PayloadMapper;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PayloadRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.PayloadService;
import com.ami.service.TelemetryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PayloadServiceImpl implements PayloadService {

	private final PayloadRepository payloadRepository;

	private final DeviceRepository deviceRepository;

	private final PayloadMapper payloadMapper;

	private final SecurityUtils securityUtils;

	private final TelemetryService telemetryService;

	private Payload findPayloadOrThrow(Long payloadId) {

		return payloadRepository.findById(payloadId)
				.orElseThrow(() -> new ResourceNotFoundException("Payload not found with id : " + payloadId));
	}

	private void validatePayloadAccess(Payload payload, User loggedInUser) {

		RoleType role = loggedInUser.getRole();

		// Super Admin
		if (role == RoleType.SUPER_ADMIN) {
			return;
		}

		// Admin
		if (role == RoleType.ADMIN) {

			if (payload.getDevice().getAssignedAdmin() == null
					|| !payload.getDevice().getAssignedAdmin().getId().equals(loggedInUser.getId())) {

				throw new RuntimeException("You are not authorized to access this payload");
			}

			return;
		}

		// User
		if (payload.getDevice().getAssignedUser() == null
				|| !payload.getDevice().getAssignedUser().getId().equals(loggedInUser.getId())) {

			throw new RuntimeException("You are not authorized to access this payload");
		}
	}

	private void validateDeviceAccess(Device device, User loggedInUser) {

		RoleType role = loggedInUser.getRole();

		if (role == RoleType.SUPER_ADMIN) {
			return;
		}

		if (role == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {

				throw new RuntimeException("You are not authorized to access this device");
			}

			return;
		}

		if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {

			throw new RuntimeException("You are not authorized to access this device");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadStatsDTO getStats() {
		User loggedInUser = securityUtils.getLoggedInUser();
		LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
		long total;
		long success;
		long failed;
		long online;

		// SUPER ADMIN
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			total = payloadRepository.count();
			success = payloadRepository.countByStatus(PayloadStatus.SUCCESS);
			failed = payloadRepository.countByStatus(PayloadStatus.FAILED);
			online = payloadRepository.countOnlineDevices(fifteenMinutesAgo);
		}

		// ADMIN
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			total = payloadRepository.countByAssignedAdmin(loggedInUser.getId());
			success = payloadRepository.countByAssignedAdminAndStatus(loggedInUser.getId(), PayloadStatus.SUCCESS);
			failed = payloadRepository.countByAssignedAdminAndStatus(loggedInUser.getId(), PayloadStatus.FAILED);
			online = payloadRepository.countOnlineDevicesByAdmin(loggedInUser.getId(), fifteenMinutesAgo);
		}

		// USER
		else {
			total = payloadRepository.countByAssignedUser(loggedInUser.getId());
			success = payloadRepository.countByAssignedUserAndStatus(loggedInUser.getId(), PayloadStatus.SUCCESS);
			failed = payloadRepository.countByAssignedUserAndStatus(loggedInUser.getId(), PayloadStatus.FAILED);
			online = payloadRepository.countOnlineDevicesByUser(loggedInUser.getId(), fifteenMinutesAgo);
		}
		return PayloadStatsDTO.builder().totalPayloads(total).successfulPayloads(success).failedPayloads(failed)
				.onlineDevices(online).build();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PayloadSummaryDTO> getPayloads(PayloadFilterRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();
		LocalDateTime from = request.getFrom() != null ? request.getFrom().atStartOfDay() : null;
		LocalDateTime to = request.getTo() != null ? request.getTo().atTime(LocalTime.MAX) : null;

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.DESC, "receivedAt"));

		Page<Payload> payloads;

		// SUPER ADMIN
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			payloads = payloadRepository.findWithFiltersForSuperAdmin(request.getDeviceId(), request.getConsumer(),
					request.getStatus(), from, to, request.getSearch(), pageable);
		}

		// ADMIN
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			payloads = payloadRepository.findWithFiltersForAdmin(loggedInUser.getId(), request.getDeviceId(),
					request.getConsumer(), request.getStatus(), from, to, request.getSearch(), pageable);
		}

		// USER
		else {
			payloads = payloadRepository.findWithFiltersForUser(loggedInUser.getId(), request.getDeviceId(),
					request.getConsumer(), request.getStatus(), from, to, request.getSearch(), pageable);
		}
		return payloads.map(payloadMapper::toSummary);
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadDetailDTO getPayloadDetail(Long payloadId) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Payload payload = findPayloadOrThrow(payloadId);
		validatePayloadAccess(payload, loggedInUser);
		return payloadMapper.toDetail(payload);
	}

	@Override
	@Transactional(readOnly = true)
	public List<HourlyReadingDTO> get24HourReadings(Long deviceId, LocalDate date) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found"));
		validateDeviceAccess(device, loggedInUser);
		LocalDateTime from = date.atStartOfDay();
		LocalDateTime to = date.atTime(LocalTime.MAX);

		return payloadRepository.find24HourReadings(deviceId, from, to).stream()
				.map(payload -> HourlyReadingDTO.builder().timestamp(payload.getReceivedAt())
						.reading(payload.getEndReading()).consumption(payload.getConsumption()).build())
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<HourlyReadingDTO> getConsumptionTrend(Long deviceId, LocalDate fromDate, LocalDate toDate) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found"));
		validateDeviceAccess(device, loggedInUser);

		return payloadRepository
				.findForConsumptionTrend(deviceId, fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)).stream()
				.map(payload -> HourlyReadingDTO.builder().timestamp(payload.getReceivedAt())
						.reading(payload.getEndReading()).consumption(payload.getConsumption()).build())
				.toList();
	}

	@Override
	@Transactional
	public void receivePayload(TelemetryIngestRequest request) {

		if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
			throw new RuntimeException("Device Id is missing");
		}

		Device device = deviceRepository.findByDeviceId(request.getDeviceId()).orElseThrow(
				() -> new ResourceNotFoundException("Device not found with deviceId : " + request.getDeviceId()));

		if (device.getMeter() == null) {
			throw new RuntimeException("Meter not configured for device");
		}

		PayloadStatus status = PayloadStatus.SUCCESS;
		String failureReason = null;

		// =====================================================
		// Business Validations
		// =====================================================

		if (request.getStartReading() == null) {
			status = PayloadStatus.FAILED;
			failureReason = "Start reading is missing";
		}

		else if (request.getEndReading() == null) {
			status = PayloadStatus.FAILED;
			failureReason = "End reading is missing";
		}

		else if (request.getEndReading() < request.getStartReading()) {
			status = PayloadStatus.FAILED;
			failureReason = "End reading cannot be less than start reading";
		}

		else if (request.getBatteryPercentage() != null
				&& (request.getBatteryPercentage() < 0 || request.getBatteryPercentage() > 100)) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid battery percentage";
		}

		else if (request.getSignalQuality() != null && request.getSignalQuality() < 0) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid signal quality";
		}

		else if (request.getSignalPower() != null && request.getSignalPower() > 0) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid signal power";
		}

		else if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
			status = PayloadStatus.FAILED;
			failureReason = "Device Id is missing";
		}

		// =====================================================
		// Consumption Calculation
		// =====================================================

		Double consumption = null;

		if (status == PayloadStatus.SUCCESS) {
			consumption = request.getEndReading() - request.getStartReading();
		}

		Payload payload = Payload.builder()

				// Device
				.device(device)
				// Meter Data
				.startReading(request.getStartReading()).endReading(request.getEndReading()).consumption(consumption)
				.startBalance(request.getStartBalance()).endBalance(request.getEndBalance())
				// Communication Data
				.batteryPercentage(request.getBatteryPercentage()).signalQuality(request.getSignalQuality())
				.signalPower(request.getSignalPower()).snr(request.getSnr())
				// Device Snapshot
				.firmwareVersion(request.getFirmwareVersion()).simNumber(request.getSimNumber())
				.consumerNumber(request.getConsumerNumber())
				// Status
				.status(status).failureReason(failureReason)
				// Device State
				.valveStatus(request.getValveStatus()).sensorStatus(request.getSensorStatus())

				// Timeline
				.receivedAt(LocalDateTime.now())

				// Raw Payload
				.rawPayload(request.getRawPayload())

				.build();

		payloadRepository.save(payload);
		if (status == PayloadStatus.SUCCESS) {

			telemetryService.saveTelemetryFromIngest(request);

			device.setOnline(true);
			device.setLastSyncTime(LocalDateTime.now());
			device.setHealthStatus(determineHealth(request.getBatteryPercentage(), request.getSignalQuality()));
			deviceRepository.save(device);
		}
	}

	private DeviceHealthStatus determineHealth(Integer batteryLevel, Integer signalStrength) {

		if (batteryLevel == null || signalStrength == null) {
			return DeviceHealthStatus.WARNING;
		}

		if (batteryLevel < 20 || signalStrength < 20) {
			return DeviceHealthStatus.CRITICAL;
		}

		if (batteryLevel < 50 || signalStrength < 50) {
			return DeviceHealthStatus.WARNING;
		}

		return DeviceHealthStatus.HEALTHY;
	}

	@Override
	@Transactional
	public void deletePayload(Long payloadId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Payload payload = findPayloadOrThrow(payloadId);

		// Super Admin
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			payloadRepository.delete(payload);
			return;
		}

		// Admin
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (payload.getDevice().getAssignedAdmin() == null
					|| !payload.getDevice().getAssignedAdmin().getId().equals(loggedInUser.getId())) {

				throw new RuntimeException("You are not authorized to delete this payload");
			}

			payloadRepository.delete(payload);
			return;
		}

		throw new RuntimeException("Users are not allowed to delete payloads");
	}

}
