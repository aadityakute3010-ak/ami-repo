package com.ami.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.responses.ConsumptionTrendDTO;
import com.ami.dto.responses.DailyReadingResponseDTO;
import com.ami.dto.responses.DevicePayloadHistoryDTO;
import com.ami.dto.responses.HourlyReadingDTO;
import com.ami.dto.responses.PagedDevicePayloadHistoryDTO;
import com.ami.dto.responses.PagedPayloadResponseDto;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadExportResponse;
import com.ami.dto.responses.PayloadLogDTO;
import com.ami.dto.responses.PayloadSourceSummaryDTO;
import com.ami.dto.responses.PayloadStatsDTO;
import com.ami.entity.DailyConsumption;
import com.ami.entity.Device;
import com.ami.entity.Payload;
import com.ami.entity.User;
import com.ami.enums.DeviceHealthStatus;
import com.ami.enums.DeviceStatus;
import com.ami.enums.PayloadStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.PayloadMapper;
import com.ami.repository.DailyConsumptionRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PayloadRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.PayloadService;
import com.ami.service.PrepaidConsumptionService;
import com.ami.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import com.ami.entity.telemetry.EnergyTelemetry;
import com.ami.entity.telemetry.GasTelemetry;
import com.ami.entity.telemetry.SolarTelemetry;
import com.ami.entity.telemetry.WaterTelemetry;
import com.ami.repository.EnergyTelemetryRepository;
import com.ami.repository.GasTelemetryRepository;
import com.ami.repository.SolarTelemetryRepository;
import com.ami.repository.WaterTelemetryRepository;
import java.util.function.Function;
import com.ami.dto.responses.PayloadAnalyticsResponseDTO;
import com.ami.repository.projection.PayloadDailyAnalyticsProjection;
import com.ami.repository.projection.PayloadHourlyAnalyticsProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import com.ami.dto.responses.PayloadSummaryDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.jpa.domain.Specification;
import com.ami.specification.PayloadSpecification;
import com.ami.entity.PrepaidRecharge;
import com.ami.repository.PrepaidRechargeRepository;
import com.ami.enums.RechargeStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class PayloadServiceImpl implements PayloadService {

	private final PayloadRepository payloadRepository;

	private final DeviceRepository deviceRepository;

	private final PayloadMapper payloadMapper;

	private final SecurityUtils securityUtils;

	private final TelemetryService telemetryService;

	private final DailyConsumptionRepository dailyConsumptionRepository;

	private final EnergyTelemetryRepository energyTelemetryRepository;

	private final WaterTelemetryRepository waterTelemetryRepository;

	private final GasTelemetryRepository gasTelemetryRepository;

	private final SolarTelemetryRepository solarTelemetryRepository;

	private final ObjectMapper objectMapper;

	private final PrepaidConsumptionService prepaidConsumptionService;

	private final PrepaidRechargeRepository prepaidRechargeRepository;

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

	private LocalDate parseDate(String date, String fieldName) {

		if (date == null || date.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadStatsDTO getStats() {

		User loggedInUser = securityUtils.getLoggedInUser();

		LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

		long total;
		long success;
		long warning;
		long failed;

		long totalDevices;
		long online;
		long offline;

		long todayTotal;
		long todaySuccess;
		long todayWarning;
		long todayFailed;
		long todayOnline;
		long todayOffline;

		double averageBattery;
		double averageSignal;

		double todayConsumption;

		double todayRecharge;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			total = payloadRepository.count();

			success = payloadRepository.countByStatus(PayloadStatus.SUCCESS);

			warning = payloadRepository.countByStatus(PayloadStatus.WARNING);

			failed = payloadRepository.countByStatus(PayloadStatus.FAILED);

			totalDevices = payloadRepository.countTotalDevices();

			online = payloadRepository.countOnlineDevices(fifteenMinutesAgo);

			offline = Math.max(totalDevices - online, 0);

			todayTotal = payloadRepository.countByReceivedAtBetween(todayStart, todayEnd);

			todaySuccess = payloadRepository.countByStatusAndReceivedAtBetween(PayloadStatus.SUCCESS, todayStart,
					todayEnd);

			todayWarning = payloadRepository.countByStatusAndReceivedAtBetween(PayloadStatus.WARNING, todayStart,
					todayEnd);

			todayFailed = payloadRepository.countByStatusAndReceivedAtBetween(PayloadStatus.FAILED, todayStart,
					todayEnd);

			todayOnline = payloadRepository.countOnlineDevicesToday(todayStart, todayEnd);

			todayOffline = Math.max(totalDevices - todayOnline, 0);

			averageBattery = safeDouble(payloadRepository.findAverageBattery());

			averageSignal = safeDouble(payloadRepository.findAverageSignal());

			todayConsumption = safeDouble(payloadRepository.findTodayConsumption(todayStart, todayEnd));

			todayRecharge = safeDouble(
					prepaidRechargeRepository.findTotalRechargeBetween(RechargeStatus.SUCCESS, todayStart, todayEnd));

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {

			Long adminId = loggedInUser.getId();

			todayRecharge = safeDouble(prepaidRechargeRepository.findTotalRechargeByAdminBetween(adminId,
					RechargeStatus.SUCCESS, todayStart, todayEnd));

			total = payloadRepository.countByAssignedAdmin(adminId);

			success = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.SUCCESS);

			warning = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.WARNING);

			failed = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.FAILED);

			totalDevices = payloadRepository.countTotalDevicesByAdmin(adminId);

			online = payloadRepository.countOnlineDevicesByAdmin(adminId, fifteenMinutesAgo);

			offline = Math.max(totalDevices - online, 0);

			todayTotal = payloadRepository.countTodayByAssignedAdmin(adminId, todayStart, todayEnd);

			todaySuccess = payloadRepository.countTodayByAssignedAdminAndStatus(adminId, PayloadStatus.SUCCESS,
					todayStart, todayEnd);

			todayWarning = payloadRepository.countTodayByAssignedAdminAndStatus(adminId, PayloadStatus.WARNING,
					todayStart, todayEnd);

			todayFailed = payloadRepository.countTodayByAssignedAdminAndStatus(adminId, PayloadStatus.FAILED,
					todayStart, todayEnd);

			todayOnline = payloadRepository.countOnlineDevicesTodayByAdmin(adminId, todayStart, todayEnd);

			todayOffline = Math.max(totalDevices - todayOnline, 0);

			averageBattery = safeDouble(payloadRepository.findAverageBatteryByAdmin(adminId));

			averageSignal = safeDouble(payloadRepository.findAverageSignalByAdmin(adminId));

			todayConsumption = safeDouble(payloadRepository.findTodayConsumptionByAdmin(adminId, todayStart, todayEnd));

		} else {

			Long userId = loggedInUser.getId();

			todayRecharge = safeDouble(prepaidRechargeRepository.findTotalRechargeByUserBetween(userId,
					RechargeStatus.SUCCESS, todayStart, todayEnd));

			total = payloadRepository.countByAssignedUser(userId);

			success = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.SUCCESS);

			warning = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.WARNING);

			failed = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.FAILED);

			totalDevices = payloadRepository.countTotalDevicesByUser(userId);

			online = payloadRepository.countOnlineDevicesByUser(userId, fifteenMinutesAgo);

			offline = Math.max(totalDevices - online, 0);

			todayTotal = payloadRepository.countTodayByAssignedUser(userId, todayStart, todayEnd);

			todaySuccess = payloadRepository.countTodayByAssignedUserAndStatus(userId, PayloadStatus.SUCCESS,
					todayStart, todayEnd);

			todayWarning = payloadRepository.countTodayByAssignedUserAndStatus(userId, PayloadStatus.WARNING,
					todayStart, todayEnd);

			todayFailed = payloadRepository.countTodayByAssignedUserAndStatus(userId, PayloadStatus.FAILED, todayStart,
					todayEnd);

			todayOnline = payloadRepository.countOnlineDevicesTodayByUser(userId, todayStart, todayEnd);

			todayOffline = Math.max(totalDevices - todayOnline, 0);

			averageBattery = safeDouble(payloadRepository.findAverageBatteryByUser(userId));

			averageSignal = safeDouble(payloadRepository.findAverageSignalByUser(userId));

			todayConsumption = safeDouble(payloadRepository.findTodayConsumptionByUser(userId, todayStart, todayEnd));
		}

		return PayloadStatsDTO.builder()

				.totalPayloads(total)

				.successfulPayloads(success)

				.warningPayloads(warning)

				.failedPayloads(failed)

				.totalDevices(totalDevices)

				.onlineDevices(online)

				.offlineDevices(offline)

				.averageBattery(roundDouble(averageBattery))

				.averageSignal(roundDouble(averageSignal))

				.todayConsumption(roundDouble(todayConsumption))

				.todayRecharge(roundDouble(todayRecharge))

				.totalPayloadsTodayPercentage(calculatePercentage(todayTotal, total))

				.successfulPayloadsTodayPercentage(calculatePercentage(todaySuccess, success))

				.warningPayloadsTodayPercentage(calculatePercentage(todayWarning, warning))

				.failedPayloadsTodayPercentage(calculatePercentage(todayFailed, failed))

				.onlineDevicesTodayPercentage(calculatePercentage(todayOnline, totalDevices))

				.offlineDevicesTodayPercentage(calculatePercentage(todayOffline, totalDevices))

				.build();
	}

	private double safeDouble(Double value) {

		return value != null ? value : 0.0;
	}

	private Double safeDouble(BigDecimal value) {
		return value != null ? value.doubleValue() : 0.0;
	}

	private double roundDouble(double value) {

		return Math.round(value * 100.0) / 100.0;
	}

	private double calculatePercentage(long todayCount, long totalCount) {
		if (totalCount == 0) {
			return 0.0;
		}
		return Math.round((todayCount * 100.0 / totalCount) * 100.0) / 100.0;
	}

	@Override
	@Transactional(readOnly = true)
	public PagedPayloadResponseDto getPayloads(PayloadFilterRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (request.getPage() < 0) {
			request.setPage(0);
		}

		if (request.getSize() <= 0) {
			request.setSize(10);
		}

		if (request.getSize() > 100) {
			request.setSize(100);
		}

		if (request.getMinBattery() != null && (request.getMinBattery() < 0 || request.getMinBattery() > 100)) {

			throw new IllegalArgumentException("minBattery must be between 0 and 100");
		}

		if (request.getMaxBattery() != null && (request.getMaxBattery() < 0 || request.getMaxBattery() > 100)) {

			throw new IllegalArgumentException("maxBattery must be between 0 and 100");
		}

		if (request.getMinBattery() != null && request.getMaxBattery() != null
				&& request.getMinBattery() > request.getMaxBattery()) {

			throw new IllegalArgumentException("minBattery cannot be greater than maxBattery");
		}

		if (request.getMinSignal() != null && request.getMaxSignal() != null
				&& request.getMinSignal() > request.getMaxSignal()) {

			throw new IllegalArgumentException("minSignal cannot be greater than maxSignal");
		}

		if (request.getFrom() != null && request.getTo() != null && request.getFrom().isAfter(request.getTo())) {

			throw new IllegalArgumentException("from date cannot be after to date");
		}

		LocalDateTime from = request.getFrom() != null ? request.getFrom().atStartOfDay() : null;

		LocalDateTime to = request.getTo() != null ? request.getTo().atTime(LocalTime.MAX) : null;

		String sortBy = resolvePayloadSortField(request.getSortBy());

		Sort.Direction sortDirection = request.getSortDirection() != null ? request.getSortDirection()
				: Sort.Direction.DESC;

		String deviceId = normalizeFilterValue(request.getDeviceId());

		String consumer = normalizeFilterValue(request.getConsumer());

		String meterNumber = normalizeFilterValue(request.getMeterNumber());

		String macAddress = normalizeFilterValue(request.getMacAddress());

		String search = normalizeFilterValue(request.getSearch());

		SourceType sourceType = request.getSourceType();

		if (sourceType == SourceType.ALL) {
			sourceType = null;
		}

		request.setDeviceId(deviceId);
		request.setConsumer(consumer);
		request.setMeterNumber(meterNumber);
		request.setMacAddress(macAddress);
		request.setSearch(search);
		request.setSourceType(sourceType);

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortDirection, sortBy));

		Specification<Payload> specification = PayloadSpecification.build(request, loggedInUser, from, to);

		Page<Payload> payloadPage = payloadRepository.findAll(specification, pageable);

		return PagedPayloadResponseDto.builder()

				.payloads(payloadPage.getContent().stream().map(payloadMapper::toSummary).toList())
				.currentPage(payloadPage.getNumber()).pageSize(payloadPage.getSize())
				.totalPages(payloadPage.getTotalPages()).totalElements(payloadPage.getTotalElements())
				.currentElements(payloadPage.getNumberOfElements()).first(payloadPage.isFirst())
				.last(payloadPage.isLast()).hasNext(payloadPage.hasNext()).hasPrevious(payloadPage.hasPrevious())
				.sortBy(request.getSortBy() != null && !request.getSortBy().isBlank() ? request.getSortBy()
						: "timestamp")
				.sortDirection(request.getSortDirection() != null ? request.getSortDirection().name()
						: Sort.Direction.DESC.name())
				.build();
	}

	private String resolvePayloadSortField(String sortBy) {

		if (sortBy == null || sortBy.isBlank()) {
			return "receivedAt";
		}

		return switch (sortBy.trim()) {

		case "id" -> "id";

		case "timestamp" -> "receivedAt";

		case "receivedAt" -> "receivedAt";

		case "status" -> "status";

		case "startReading" -> "startReading";

		case "endReading" -> "endReading";

		case "consumption" -> "consumption";

		case "batteryPercentage" -> "batteryPercentage";

		case "signalQuality" -> "signalQuality";

		case "signalPower" -> "signalPower";

		case "snr" -> "snr";

		default -> "receivedAt";
		};
	}

	private String normalizeFilterValue(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadDetailDTO getPayloadDetail(Long payloadId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Payload payload = findPayloadOrThrow(payloadId);

		validatePayloadAccess(payload, loggedInUser);

		EnergyTelemetry energyTelemetry = null;

		WaterTelemetry waterTelemetry = null;

		GasTelemetry gasTelemetry = null;

		SolarTelemetry solarTelemetry = null;

		if (payload.getDevice() != null && payload.getDevice().getMeter() != null
				&& payload.getDevice().getMeter().getSourceType() != null) {

			switch (payload.getDevice().getMeter().getSourceType()) {

			case ENERGY:

				energyTelemetry = energyTelemetryRepository.findByPayload(payload).orElse(null);

				break;

			case WATER:

				waterTelemetry = waterTelemetryRepository.findByPayload(payload).orElse(null);

				break;

			case GAS:

				gasTelemetry = gasTelemetryRepository.findByPayload(payload).orElse(null);

				break;

			case SOLAR:

				solarTelemetry = solarTelemetryRepository.findByPayload(payload).orElse(null);

				break;

			default:

				break;
			}
		}

		PayloadDetailDTO detail = payloadMapper.toDetail(payload, energyTelemetry, waterTelemetry, gasTelemetry,
				solarTelemetry);

		Double rechargeAmount = null;

		if (payload.getDevice() != null && payload.getDevice().getDeviceId() != null
				&& payload.getReceivedAt() != null) {

			rechargeAmount = prepaidRechargeRepository
					.findTopByDeviceAndStatusAndRechargeDateLessThanEqualOrderByRechargeDateDesc(payload.getDevice(),
							RechargeStatus.SUCCESS, payload.getReceivedAt())
					.map(PrepaidRecharge::getAmount).map(BigDecimal::doubleValue).orElse(null);
		}

		detail.setRechargeAmount(rechargeAmount);

		return detail;
	}

	@Override
	@Transactional(readOnly = true)
	public DailyReadingResponseDTO get24HourReadings(Long deviceId, String date) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found"));

		validateDeviceAccess(device, loggedInUser);

		LocalDate parsedDate = parseDate(date, "date");

		LocalDateTime from = parsedDate.atStartOfDay();
		LocalDateTime to = parsedDate.atTime(LocalTime.MAX);

		List<Payload> payloads = payloadRepository.find24HourReadings(deviceId, from, to);

		Map<Integer, Payload> latestPayloadByHour = new HashMap<>();

		for (Payload payload : payloads) {

			int hour = payload.getReceivedAt().getHour();

			Payload existingPayload = latestPayloadByHour.get(hour);

			if (existingPayload == null || payload.getReceivedAt().isAfter(existingPayload.getReceivedAt())) {

				latestPayloadByHour.put(hour, payload);
			}
		}

		List<HourlyReadingDTO> hourlyReadings = new ArrayList<>();

		for (int hour = 0; hour < 24; hour++) {

			Payload payload = latestPayloadByHour.get(hour);

			hourlyReadings.add(HourlyReadingDTO.builder().label("R" + hour).hour(hour)
					.timestamp(payload != null ? payload.getReceivedAt() : parsedDate.atTime(hour, 0))
					.reading(payload != null ? payload.getEndReading() : null)
					.consumption(payload != null ? payload.getConsumption() : null).build());
		}

		DailyConsumption daily = dailyConsumptionRepository.findByDeviceAndReadingDate(device, parsedDate).orElse(null);

		return DailyReadingResponseDTO.builder().deviceId(device.getId()).deviceCode(device.getDeviceId())
				.deviceName(device.getDeviceName()).date(parsedDate)
				.openingReading(daily != null ? daily.getOpeningReading() : null)
				.closingReading(daily != null ? daily.getClosingReading() : null)
				.totalReading(daily != null ? daily.getTotalReading() : null)
				.dailyConsumption(daily != null ? daily.getDailyConsumption() : null).readings(hourlyReadings).build();
	}

	private void updateDailyConsumption(Device device, Payload payload) {

		LocalDate readingDate = payload.getReceivedAt().toLocalDate();

		DailyConsumption daily = dailyConsumptionRepository.findByDeviceAndReadingDate(device, readingDate)
				.orElse(null);

		if (daily == null) {

			daily = DailyConsumption.builder().device(device).readingDate(readingDate)
					.openingReading(payload.getStartReading()).closingReading(payload.getEndReading())
					.totalReading(payload.getEndReading())
					.dailyConsumption(payload.getEndReading() - payload.getStartReading())
					.firstPayloadTime(payload.getReceivedAt()).lastPayloadTime(payload.getReceivedAt())
					.successPayloadCount(1L).failedPayloadCount(0L).createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now()).build();

		} else {

			if (daily.getFirstPayloadTime() == null || payload.getReceivedAt().isBefore(daily.getFirstPayloadTime())) {

				daily.setOpeningReading(payload.getStartReading());
				daily.setFirstPayloadTime(payload.getReceivedAt());
			}

			if (daily.getLastPayloadTime() == null || payload.getReceivedAt().isAfter(daily.getLastPayloadTime())) {

				daily.setClosingReading(payload.getEndReading());
				daily.setTotalReading(payload.getEndReading());
				daily.setLastPayloadTime(payload.getReceivedAt());
			}

			if (daily.getOpeningReading() != null && daily.getClosingReading() != null) {
				daily.setDailyConsumption(daily.getClosingReading() - daily.getOpeningReading());
			}

			daily.setSuccessPayloadCount(
					daily.getSuccessPayloadCount() == null ? 1L : daily.getSuccessPayloadCount() + 1);

			daily.setUpdatedAt(LocalDateTime.now());
		}

		dailyConsumptionRepository.save(daily);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsumptionTrendDTO> getConsumptionTrend(Long deviceId, String fromDate, String toDate) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found"));

		validateDeviceAccess(device, loggedInUser);

		LocalDate parsedFromDate = parseDate(fromDate, "fromDate");
		LocalDate parsedToDate = parseDate(toDate, "toDate");

		if (parsedFromDate.isAfter(parsedToDate)) {
			throw new IllegalArgumentException("fromDate cannot be after toDate");
		}

		List<DailyConsumption> dailyConsumptions = dailyConsumptionRepository.findTrendByDeviceAndDateRange(deviceId,
				parsedFromDate, parsedToDate);

		Map<LocalDate, DailyConsumption> dailyMap = dailyConsumptions.stream()
				.collect(Collectors.toMap(DailyConsumption::getReadingDate, daily -> daily));

		List<ConsumptionTrendDTO> response = new ArrayList<>();

		LocalDate currentDate = parsedFromDate;

		while (!currentDate.isAfter(parsedToDate)) {

			DailyConsumption daily = dailyMap.get(currentDate);

			if (daily != null) {

				response.add(ConsumptionTrendDTO.builder().date(currentDate).openingReading(daily.getOpeningReading())
						.closingReading(daily.getClosingReading()).totalReading(daily.getTotalReading())
						.consumption(daily.getDailyConsumption()).build());

			} else {

				response.add(ConsumptionTrendDTO.builder().date(currentDate).openingReading(null).closingReading(null)
						.totalReading(null).consumption(0.0).build());
			}

			currentDate = currentDate.plusDays(1);
		}

		return response;
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
		if (device.getMeter().getStatus() == DeviceStatus.INACTIVE) {
			throw new RuntimeException("Inactive device cannot send telemetry");
		}

		PayloadStatus status = PayloadStatus.SUCCESS;
		String failureReason = null;

		// =====================================================
		// Fatal validation
		// =====================================================

		if (request.getStartReading() == null) {

			status = PayloadStatus.FAILED;
			failureReason = "Start reading is missing";

		} else if (request.getEndReading() == null) {

			status = PayloadStatus.FAILED;
			failureReason = "End reading is missing";

		} else if (request.getEndReading() < request.getStartReading()) {

			status = PayloadStatus.FAILED;
			failureReason = "End reading cannot be less than start reading";

		} else if (request.getBatteryPercentage() != null
				&& (request.getBatteryPercentage() < 0 || request.getBatteryPercentage() > 100)) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid battery percentage";

		} else if (request.getSignalQuality() != null && request.getSignalQuality() < 0) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid signal quality";

		} else if (request.getSignalPower() != null && request.getSignalPower() > 0) {

			status = PayloadStatus.FAILED;
			failureReason = "Invalid signal power";
		}

		// =====================================================
		// Warning validation
		// Only evaluate warnings when there is no fatal error
		// =====================================================

		if (status != PayloadStatus.FAILED) {

			if (request.getBatteryPercentage() == null) {

				status = PayloadStatus.WARNING;
				failureReason = "Battery percentage is missing";

			} else if (request.getBatteryPercentage() < 20) {

				status = PayloadStatus.WARNING;
				failureReason = "Battery percentage is critically low";

			} else if (request.getSignalQuality() == null) {

				status = PayloadStatus.WARNING;
				failureReason = "Signal quality is missing";

			} else if (request.getSignalQuality() < 20) {

				status = PayloadStatus.WARNING;
				failureReason = "Signal quality is critically low";

			} else if (request.getSignalPower() == null) {

				status = PayloadStatus.WARNING;
				failureReason = "Signal power is missing";

			} else if (request.getSnr() == null) {

				status = PayloadStatus.WARNING;
				failureReason = "SNR is missing";
			}
		}

		// =====================================================
		// Consumption Calculation
		// =====================================================

		Double consumption = null;
		if (status == PayloadStatus.SUCCESS || status == PayloadStatus.WARNING) {
			consumption = request.getEndReading() - request.getStartReading();
		}

		String rawPayload = serializeRawPayload(request);

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
				.rawPayload(rawPayload)

				.build();

		Payload savedPayload;

		if (status == PayloadStatus.SUCCESS || status == PayloadStatus.WARNING) {

			BigDecimal startReading = BigDecimal.valueOf(request.getStartReading());

			BigDecimal endReading = BigDecimal.valueOf(request.getEndReading());

			boolean alreadyProcessed = payloadRepository.existsByDevice_IdAndStartReadingAndEndReadingAndStatusIn(
					device.getId(), request.getStartReading(), request.getEndReading(),
					List.of(PayloadStatus.SUCCESS, PayloadStatus.WARNING));

			if (alreadyProcessed) {
				throw new IllegalStateException("Duplicate telemetry detected for device " + device.getDeviceId()
						+ " with reading interval " + startReading + " -> " + endReading);
			}

			savedPayload = payloadRepository.save(payload);

			try {

				prepaidConsumptionService.deductConsumption(device, startReading, endReading);

			} catch (IllegalStateException | IllegalArgumentException | ResourceNotFoundException prepaidException) {

				// Prepaid deduction failed (blocked / insufficient balance / invalid reading /
				// balance not yet created for this device). deductConsumption() runs in its
				// own transaction (REQUIRES_NEW), so this failure has already rolled back on
				// its own — it has NOT affected the Payload row we just saved in this (outer)
				// transaction. Mark the payload as FAILED so the failure is visible instead
				// of silently disappearing.

				savedPayload.setStatus(PayloadStatus.FAILED);
				savedPayload.setFailureReason("Prepaid deduction failed: " + prepaidException.getMessage());

				payloadRepository.save(savedPayload);

				return;
			}

			telemetryService.saveTelemetryFromIngest(request, savedPayload);

			updateDailyConsumption(device, savedPayload);

			device.setOnline(true);
			device.setLastSyncTime(LocalDateTime.now());
			device.setHealthStatus(determineHealth(request.getBatteryPercentage(), request.getSignalQuality()));

			deviceRepository.save(device);

		} else {
			savedPayload = payloadRepository.save(payload);
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

			deletePayloadTelemetry(payload);

			payloadRepository.delete(payload);

			return;
		}

		// Admin
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (payload.getDevice().getAssignedAdmin() == null
					|| !payload.getDevice().getAssignedAdmin().getId().equals(loggedInUser.getId())) {

				throw new RuntimeException("You are not authorized to delete this payload");
			}

			deletePayloadTelemetry(payload);

			payloadRepository.delete(payload);

			return;
		}

		throw new RuntimeException("Users are not allowed to delete payloads");
	}

	private void deletePayloadTelemetry(Payload payload) {

		energyTelemetryRepository.findByPayload(payload).ifPresent(energyTelemetryRepository::delete);

		waterTelemetryRepository.findByPayload(payload).ifPresent(waterTelemetryRepository::delete);

		gasTelemetryRepository.findByPayload(payload).ifPresent(gasTelemetryRepository::delete);

		solarTelemetryRepository.findByPayload(payload).ifPresent(solarTelemetryRepository::delete);
	}

	private List<ConsumptionTrendDTO> buildAnalyticsTrend(LocalDate fromDate, LocalDate toDate,
			List<PayloadDailyAnalyticsProjection> projections) {

		Map<LocalDate, PayloadDailyAnalyticsProjection> projectionMap = projections.stream()
				.collect(Collectors.toMap(PayloadDailyAnalyticsProjection::getReadingDate, Function.identity()));

		List<ConsumptionTrendDTO> trend = new ArrayList<>();

		LocalDate currentDate = fromDate;

		while (!currentDate.isAfter(toDate)) {

			PayloadDailyAnalyticsProjection projection = projectionMap.get(currentDate);

			trend.add(ConsumptionTrendDTO.builder()

					.date(currentDate)

					.openingReading(projection != null ? safeDouble(projection.getOpeningReading()) : 0.0)

					.closingReading(projection != null ? safeDouble(projection.getClosingReading()) : 0.0)

					.totalReading(projection != null ? safeDouble(projection.getTotalReading()) : 0.0)

					.consumption(projection != null ? safeDouble(projection.getConsumption()) : 0.0)

					.build());

			currentDate = currentDate.plusDays(1);
		}

		return trend;
	}

	private List<HourlyReadingDTO> buildHourlyAnalytics(LocalDate date,
			List<PayloadHourlyAnalyticsProjection> projections) {

		Map<Integer, PayloadHourlyAnalyticsProjection> projectionMap = projections.stream()
				.collect(Collectors.toMap(PayloadHourlyAnalyticsProjection::getReadingHour, Function.identity()));

		List<HourlyReadingDTO> hourlyReadings = new ArrayList<>();

		for (int hour = 0; hour < 24; hour++) {

			PayloadHourlyAnalyticsProjection projection = projectionMap.get(hour);

			hourlyReadings.add(HourlyReadingDTO.builder()

					.label("R" + hour)

					.hour(hour)

					.timestamp(projection != null ? projection.getLastPayloadTime() : date.atTime(hour, 0))

					.reading(projection != null ? safeDouble(projection.getTotalReading()) : 0.0)

					.consumption(projection != null ? safeDouble(projection.getConsumption()) : 0.0)

					.build());
		}

		return hourlyReadings;
	}

	private double calculateRate(long count, long total) {

		if (total == 0) {
			return 0.0;
		}

		return roundDouble(count * 100.0 / total);
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadAnalyticsResponseDTO getPayloadAnalytics() {

		User loggedInUser = securityUtils.getLoggedInUser();

		PayloadStatsDTO stats = getStats();

		LocalDate today = LocalDate.now();

		LocalDate trendFromDate = today.minusDays(6);

		LocalDateTime todayStart = today.atStartOfDay();

		LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

		List<PayloadStatus> validStatuses = List.of(PayloadStatus.SUCCESS, PayloadStatus.WARNING);

		long energyPayloads;

		long waterPayloads;

		long gasPayloads;

		long solarPayloads;

		double averageSignalPower;

		LocalDateTime lastPayloadTime;

		List<PayloadDailyAnalyticsProjection> trendProjections;

		List<PayloadHourlyAnalyticsProjection> hourlyProjections;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			energyPayloads = payloadRepository.countBySourceType(SourceType.ENERGY);

			waterPayloads = payloadRepository.countBySourceType(SourceType.WATER);

			gasPayloads = payloadRepository.countBySourceType(SourceType.GAS);

			solarPayloads = payloadRepository.countBySourceType(SourceType.SOLAR);

			averageSignalPower = safeDouble(payloadRepository.findAverageSignalPower());

			lastPayloadTime = payloadRepository.findLastPayloadTime();

			trendProjections = dailyConsumptionRepository.findAnalyticsTrend(trendFromDate, today);

			hourlyProjections = payloadRepository.findHourlyAnalytics(todayStart, todayEnd, validStatuses);
		}

		else if (loggedInUser.getRole() == RoleType.ADMIN) {

			Long adminId = loggedInUser.getId();

			energyPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.ENERGY);

			waterPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.WATER);

			gasPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.GAS);

			solarPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.SOLAR);

			averageSignalPower = safeDouble(payloadRepository.findAverageSignalPowerByAdmin(adminId));

			lastPayloadTime = payloadRepository.findLastPayloadTimeByAdmin(adminId);

			trendProjections = dailyConsumptionRepository.findAnalyticsTrendByAdmin(adminId, trendFromDate, today);

			hourlyProjections = payloadRepository.findHourlyAnalyticsByAdmin(adminId, todayStart, todayEnd,
					validStatuses);
		}

		else {

			Long userId = loggedInUser.getId();

			energyPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.ENERGY);

			waterPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.WATER);

			gasPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.GAS);

			solarPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.SOLAR);

			averageSignalPower = safeDouble(payloadRepository.findAverageSignalPowerByUser(userId));

			lastPayloadTime = payloadRepository.findLastPayloadTimeByUser(userId);

			trendProjections = dailyConsumptionRepository.findAnalyticsTrendByUser(userId, trendFromDate, today);

			hourlyProjections = payloadRepository.findHourlyAnalyticsByUser(userId, todayStart, todayEnd,
					validStatuses);
		}

		List<ConsumptionTrendDTO> consumptionTrend = buildAnalyticsTrend(trendFromDate, today, trendProjections);

		List<HourlyReadingDTO> hourlyReadings = buildHourlyAnalytics(today, hourlyProjections);

		long totalPayloads = stats.getTotalPayloads();

		long successfulPayloads = stats.getSuccessfulPayloads();

		long failedPayloads = stats.getFailedPayloads();

		long warningPayloads = stats.getWarningPayloads();

		return PayloadAnalyticsResponseDTO.builder()

				.totalPayloads(totalPayloads)

				.successfulPayloads(successfulPayloads)

				.failedPayloads(failedPayloads)

				.warningPayloads(warningPayloads)

				/*
				 * PayloadStatus currently has no PENDING.
				 */
				.pendingPayloads(0L)

				.onlineDevices(stats.getOnlineDevices())

				.offlineDevices(stats.getOfflineDevices())

				.successRate(calculateRate(successfulPayloads, totalPayloads))

				.failureRate(calculateRate(failedPayloads, totalPayloads))

				.consumptionTrend(consumptionTrend)

				.hourlyReadings(hourlyReadings)

				.energyPayloads(energyPayloads)

				.waterPayloads(waterPayloads)

				.gasPayloads(gasPayloads)

				.solarPayloads(solarPayloads)

				.averageBattery(stats.getAverageBattery())

				.averageSignalQuality(stats.getAverageSignal())

				.averageSignalPower(roundDouble(averageSignalPower))

				.lastPayloadTime(lastPayloadTime)

				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadSourceSummaryDTO getPayloadSourceSummary() {

		User loggedInUser = securityUtils.getLoggedInUser();

		LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

		List<PayloadStatus> consumptionStatuses = List.of(PayloadStatus.SUCCESS, PayloadStatus.WARNING);

		long totalPayloads;

		long successfulPayloads;

		long warningPayloads;

		long failedPayloads;

		long pendingPayloads;

		long waterPayloads;

		long energyPayloads;

		long gasPayloads;

		long solarPayloads;

		long totalDevices;

		long onlineDevices;

		double totalConsumption;

		double averageConsumption;

		double averageBattery;

		double averageSignalQuality;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			totalPayloads = payloadRepository.count();

			successfulPayloads = payloadRepository.countByStatus(PayloadStatus.SUCCESS);

			warningPayloads = payloadRepository.countByStatus(PayloadStatus.WARNING);

			failedPayloads = payloadRepository.countByStatus(PayloadStatus.FAILED);

			pendingPayloads = payloadRepository.countByStatus(PayloadStatus.PENDING);

			waterPayloads = payloadRepository.countBySourceType(SourceType.WATER);

			energyPayloads = payloadRepository.countBySourceType(SourceType.ENERGY);

			gasPayloads = payloadRepository.countBySourceType(SourceType.GAS);

			solarPayloads = payloadRepository.countBySourceType(SourceType.SOLAR);

			totalDevices = payloadRepository.countTotalDevices();

			onlineDevices = payloadRepository.countOnlineDevices(fifteenMinutesAgo);

			totalConsumption = safeDouble(payloadRepository.findTotalConsumptionByStatuses(consumptionStatuses));

			averageConsumption = safeDouble(payloadRepository.findAverageConsumptionByStatuses(consumptionStatuses));

			averageBattery = safeDouble(payloadRepository.findAverageBattery());

			averageSignalQuality = safeDouble(payloadRepository.findAverageSignal());
		}

		else if (loggedInUser.getRole() == RoleType.ADMIN) {

			Long adminId = loggedInUser.getId();

			totalPayloads = payloadRepository.countByAssignedAdmin(adminId);

			successfulPayloads = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.SUCCESS);

			warningPayloads = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.WARNING);

			failedPayloads = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.FAILED);

			pendingPayloads = payloadRepository.countByAssignedAdminAndStatus(adminId, PayloadStatus.PENDING);

			waterPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.WATER);

			energyPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.ENERGY);

			gasPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.GAS);

			solarPayloads = payloadRepository.countByAssignedAdminAndSourceType(adminId, SourceType.SOLAR);

			totalDevices = payloadRepository.countTotalDevicesByAdmin(adminId);

			onlineDevices = payloadRepository.countOnlineDevicesByAdmin(adminId, fifteenMinutesAgo);

			totalConsumption = safeDouble(
					payloadRepository.findTotalConsumptionByAdminAndStatuses(adminId, consumptionStatuses));

			averageConsumption = safeDouble(
					payloadRepository.findAverageConsumptionByAdminAndStatuses(adminId, consumptionStatuses));

			averageBattery = safeDouble(payloadRepository.findAverageBatteryByAdmin(adminId));

			averageSignalQuality = safeDouble(payloadRepository.findAverageSignalByAdmin(adminId));
		}

		else {

			Long userId = loggedInUser.getId();

			totalPayloads = payloadRepository.countByAssignedUser(userId);

			successfulPayloads = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.SUCCESS);

			warningPayloads = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.WARNING);

			failedPayloads = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.FAILED);

			pendingPayloads = payloadRepository.countByAssignedUserAndStatus(userId, PayloadStatus.PENDING);

			waterPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.WATER);

			energyPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.ENERGY);

			gasPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.GAS);

			solarPayloads = payloadRepository.countByAssignedUserAndSourceType(userId, SourceType.SOLAR);

			totalDevices = payloadRepository.countTotalDevicesByUser(userId);

			onlineDevices = payloadRepository.countOnlineDevicesByUser(userId, fifteenMinutesAgo);

			totalConsumption = safeDouble(
					payloadRepository.findTotalConsumptionByUserAndStatuses(userId, consumptionStatuses));

			averageConsumption = safeDouble(
					payloadRepository.findAverageConsumptionByUserAndStatuses(userId, consumptionStatuses));

			averageBattery = safeDouble(payloadRepository.findAverageBatteryByUser(userId));

			averageSignalQuality = safeDouble(payloadRepository.findAverageSignalByUser(userId));
		}

		long offlineDevices = Math.max(totalDevices - onlineDevices, 0);

		return PayloadSourceSummaryDTO.builder()

				/*
				 * The endpoint has no source parameter. It summarizes all source types visible
				 * to the logged-in user.
				 */
				.source(SourceType.ALL.name())

				.totalPayloads(totalPayloads)

				.successfulPayloads(successfulPayloads)

				.warningPayloads(warningPayloads)

				.failedPayloads(failedPayloads)

				.pendingPayloads(pendingPayloads)

				.waterPayloads(waterPayloads)

				.energyPayloads(energyPayloads)

				.gasPayloads(gasPayloads)

				.solarPayloads(solarPayloads)

				.totalDevices(totalDevices)

				.onlineDevices(onlineDevices)

				.offlineDevices(offlineDevices)

				.totalConsumption(roundDouble(totalConsumption))

				.averageConsumption(roundDouble(averageConsumption))

				.averageBattery(roundDouble(averageBattery))

				.averageSignalQuality(roundDouble(averageSignalQuality))

				.successRate(calculateRate(successfulPayloads, totalPayloads))

				.failureRate(calculateRate(failedPayloads, totalPayloads))

				.build();
	}

	private Map<Long, EnergyTelemetry> getEnergyTelemetryMap(List<Long> payloadIds) {

		if (payloadIds == null || payloadIds.isEmpty()) {
			return Collections.emptyMap();
		}

		return energyTelemetryRepository.findByPayloadIds(payloadIds).stream()
				.filter(telemetry -> telemetry.getPayload() != null && telemetry.getPayload().getId() != null)
				.collect(Collectors.toMap(telemetry -> telemetry.getPayload().getId(), Function.identity(),
						(existing, replacement) -> replacement));
	}

	private Map<Long, WaterTelemetry> getWaterTelemetryMap(List<Long> payloadIds) {

		if (payloadIds == null || payloadIds.isEmpty()) {
			return Collections.emptyMap();
		}

		return waterTelemetryRepository.findByPayloadIds(payloadIds).stream()
				.filter(telemetry -> telemetry.getPayload() != null && telemetry.getPayload().getId() != null)
				.collect(Collectors.toMap(telemetry -> telemetry.getPayload().getId(), Function.identity(),
						(existing, replacement) -> replacement));
	}

	private Map<Long, GasTelemetry> getGasTelemetryMap(List<Long> payloadIds) {

		if (payloadIds == null || payloadIds.isEmpty()) {
			return Collections.emptyMap();
		}

		return gasTelemetryRepository.findByPayloadIds(payloadIds).stream()
				.filter(telemetry -> telemetry.getPayload() != null && telemetry.getPayload().getId() != null)
				.collect(Collectors.toMap(telemetry -> telemetry.getPayload().getId(), Function.identity(),
						(existing, replacement) -> replacement));
	}

	private Map<Long, SolarTelemetry> getSolarTelemetryMap(List<Long> payloadIds) {

		if (payloadIds == null || payloadIds.isEmpty()) {
			return Collections.emptyMap();
		}

		return solarTelemetryRepository.findByPayloadIds(payloadIds).stream()
				.filter(telemetry -> telemetry.getPayload() != null && telemetry.getPayload().getId() != null)
				.collect(Collectors.toMap(telemetry -> telemetry.getPayload().getId(), Function.identity(),
						(existing, replacement) -> replacement));
	}

	private BigDecimal findRechargeAmountAtPayloadTime(List<PrepaidRecharge> recharges, LocalDateTime payloadTime) {

		if (payloadTime == null || recharges == null || recharges.isEmpty()) {

			return null;
		}

		PrepaidRecharge applicableRecharge = null;

		for (PrepaidRecharge recharge : recharges) {

			if (recharge.getRechargeDate() == null) {
				continue;
			}

			if (recharge.getRechargeDate().isAfter(payloadTime)) {
				break;
			}

			applicableRecharge = recharge;
		}

		return applicableRecharge != null ? applicableRecharge.getAmount() : null;
	}

	@Override
	@Transactional(readOnly = true)
	public PagedDevicePayloadHistoryDTO getDevicePayloadHistory(Long deviceId, int page, int size) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		validateDeviceAccess(device, loggedInUser);

		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 10;
		}

		if (size > 100) {
			size = 100;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt"));

		Page<Payload> payloadPage = payloadRepository.findPayloadHistoryByDeviceId(deviceId, pageable);

		List<Payload> payloads = payloadPage.getContent();

		if (payloads.isEmpty()) {

			return PagedDevicePayloadHistoryDTO.builder().history(Collections.emptyList())
					.currentPage(payloadPage.getNumber()).pageSize(payloadPage.getSize())
					.totalPages(payloadPage.getTotalPages()).totalElements(payloadPage.getTotalElements())
					.currentElements(payloadPage.getNumberOfElements()).first(payloadPage.isFirst())
					.last(payloadPage.isLast()).hasNext(payloadPage.hasNext()).hasPrevious(payloadPage.hasPrevious())
					.build();
		}

		List<Long> payloadIds = payloads.stream().map(Payload::getId).toList();

		Map<Long, EnergyTelemetry> energyTelemetryMap = getEnergyTelemetryMap(payloadIds);

		Map<Long, WaterTelemetry> waterTelemetryMap = getWaterTelemetryMap(payloadIds);

		Map<Long, GasTelemetry> gasTelemetryMap = getGasTelemetryMap(payloadIds);

		Map<Long, SolarTelemetry> solarTelemetryMap = getSolarTelemetryMap(payloadIds);

		List<PrepaidRecharge> recharges = prepaidRechargeRepository.findByDeviceAndStatusOrderByRechargeDateAsc(device,
				RechargeStatus.SUCCESS);

		List<DevicePayloadHistoryDTO> history = new ArrayList<>();

		for (Payload payload : payloads) {

			Long payloadId = payload.getId();

			EnergyTelemetry energyTelemetry = energyTelemetryMap.get(payloadId);

			WaterTelemetry waterTelemetry = waterTelemetryMap.get(payloadId);

			GasTelemetry gasTelemetry = gasTelemetryMap.get(payloadId);

			SolarTelemetry solarTelemetry = solarTelemetryMap.get(payloadId);

			BigDecimal rechargeAmount = findRechargeAmountAtPayloadTime(recharges, payload.getReceivedAt());

			DevicePayloadHistoryDTO historyDTO = DevicePayloadHistoryDTO.builder()

					.payloadId(payload.getId())

					.receivedAt(payload.getReceivedAt())

					.status(payload.getStatus())

					.sourceType(payload.getDevice() != null && payload.getDevice().getMeter() != null
							? payload.getDevice().getMeter().getSourceType()
							: null)

					.failureReason(payload.getFailureReason())

					.startReading(payload.getStartReading())

					.endReading(payload.getEndReading())

					.consumption(payload.getConsumption())

					.rechargeAmount(rechargeAmount != null ? rechargeAmount.doubleValue() : null)

					.batteryPercentage(payload.getBatteryPercentage())

					.signalQuality(payload.getSignalQuality())

					.signalPower(payload.getSignalPower())

					.snr(payload.getSnr())

					.valveStatus(payload.getValveStatus())

					.sensorStatus(payload.getSensorStatus())

					// Energy telemetry
					.voltage(energyTelemetry != null ? energyTelemetry.getVoltage() : null)

					.current(energyTelemetry != null ? energyTelemetry.getCurrent() : null)

					.power(energyTelemetry != null ? energyTelemetry.getPower() : null)

					.frequency(energyTelemetry != null ? energyTelemetry.getFrequency() : null)

					.powerFactor(energyTelemetry != null ? energyTelemetry.getPowerFactor() : null)

					.energyConsumed(energyTelemetry != null ? energyTelemetry.getEnergyConsumed() : null)

					.activePower(energyTelemetry != null ? energyTelemetry.getActivePower() : null)

					.reactivePower(energyTelemetry != null ? energyTelemetry.getReactivePower() : null)

					.apparentPower(energyTelemetry != null ? energyTelemetry.getApparentPower() : null)

					.load(energyTelemetry != null ? energyTelemetry.getLoad() : null)

					.demand(energyTelemetry != null ? energyTelemetry.getDemand() : null)

					// Water telemetry
					.flowRate(waterTelemetry != null ? waterTelemetry.getFlowRate() : null)

					.pressure(waterTelemetry != null ? waterTelemetry.getPressure() : null)

					.tankLevel(waterTelemetry != null ? waterTelemetry.getTankLevel() : null)

					.pumpStatus(waterTelemetry != null ? waterTelemetry.getPumpStatus() : null)

					.leakDetected(waterTelemetry != null ? waterTelemetry.getLeakDetected() : null)

					// Gas telemetry
					.gasFlow(gasTelemetry != null ? gasTelemetry.getGasFlow() : null)

					.gasPressure(gasTelemetry != null ? gasTelemetry.getGasPressure() : null)

					.gasVolume(gasTelemetry != null ? gasTelemetry.getGasVolume() : null)

					.temperature(gasTelemetry != null ? gasTelemetry.getTemperature() : null)

					.pipelineHealth(gasTelemetry != null ? gasTelemetry.getPipelineHealth() : null)

					// Solar telemetry
					.solarVoltage(solarTelemetry != null ? solarTelemetry.getSolarVoltage() : null)

					.solarCurrent(solarTelemetry != null ? solarTelemetry.getSolarCurrent() : null)

					.solarPower(solarTelemetry != null ? solarTelemetry.getSolarPower() : null)

					.energyGenerated(solarTelemetry != null ? solarTelemetry.getEnergyGenerated() : null)

					.solarGeneration(solarTelemetry != null ? solarTelemetry.getSolarGeneration() : null)

					.solarConsumption(solarTelemetry != null ? solarTelemetry.getSolarConsumption() : null)

					.panelTemperature(solarTelemetry != null ? solarTelemetry.getPanelTemperature() : null)

					.irradiance(solarTelemetry != null ? solarTelemetry.getIrradiance() : null)

					.inverterStatus(solarTelemetry != null ? solarTelemetry.getInverterStatus() : null)

					.batteryStorage(solarTelemetry != null ? solarTelemetry.getBatteryStorage() : null)

					.gridImport(solarTelemetry != null ? solarTelemetry.getGridImport() : null)

					.gridExport(solarTelemetry != null ? solarTelemetry.getGridExport() : null)

					.efficiency(solarTelemetry != null ? solarTelemetry.getEfficiency() : null)

					.build();

			history.add(historyDTO);
		}

		return PagedDevicePayloadHistoryDTO.builder().history(history).currentPage(payloadPage.getNumber())
				.pageSize(payloadPage.getSize()).totalPages(payloadPage.getTotalPages())
				.totalElements(payloadPage.getTotalElements()).currentElements(payloadPage.getNumberOfElements())
				.first(payloadPage.isFirst()).last(payloadPage.isLast()).hasNext(payloadPage.hasNext())
				.hasPrevious(payloadPage.hasPrevious()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayloadLogDTO> getPayloadLogs(Long payloadId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Payload payload = findPayloadOrThrow(payloadId);

		validatePayloadAccess(payload, loggedInUser);

		Device device = payload.getDevice();

		String message;

		if (payload.getStatus() == PayloadStatus.SUCCESS) {

			message = "Payload processed successfully";

		} else if (payload.getStatus() == PayloadStatus.WARNING) {

			message = "Payload processed with warning";

		} else if (payload.getStatus() == PayloadStatus.FAILED) {

			message = "Payload processing failed";

		} else {

			message = "Payload processing pending";
		}

		PayloadLogDTO log = PayloadLogDTO.builder()

				.payloadId(payload.getId())

				.timestamp(payload.getReceivedAt())

				.deviceId(device != null ? device.getId() : null)

				.deviceCode(device != null ? device.getDeviceId() : null)

				.deviceName(device != null ? device.getDeviceName() : null)

				.meterNumber(device != null ? device.getDeviceId() : null)

				.status(payload.getStatus())

				.message(message)

				.failureReason(payload.getFailureReason())

				.batteryPercentage(payload.getBatteryPercentage())

				.signalQuality(payload.getSignalQuality())

				.signalPower(payload.getSignalPower())

				.snr(payload.getSnr())

				.startReading(payload.getStartReading())

				.endReading(payload.getEndReading())

				.consumption(payload.getConsumption())

				.rawPayload(payload.getRawPayload())

				.build();

		return List.of(log);
	}

	private String serializeRawPayload(TelemetryIngestRequest request) {

		try {

			return objectMapper.writeValueAsString(request);

		} catch (JsonProcessingException exception) {

			throw new IllegalStateException("Unable to serialize telemetry request", exception);
		}
	}

	private TelemetryIngestRequest deserializeRawPayload(String rawPayload) {

		if (rawPayload == null || rawPayload.isBlank()) {

			throw new IllegalStateException("Stored raw payload is missing");
		}

		try {

			return objectMapper.readValue(rawPayload, TelemetryIngestRequest.class);

		} catch (JsonProcessingException exception) {

			throw new IllegalStateException("Stored raw payload is invalid and cannot be retried", exception);
		}
	}

	@Override
	@Transactional
	public void retryPayload(Long payloadId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Payload originalPayload = findPayloadOrThrow(payloadId);

		validatePayloadAccess(originalPayload, loggedInUser);

		if (originalPayload.getStatus() != PayloadStatus.FAILED) {

			throw new IllegalStateException("Only failed payloads can be retried");
		}

		TelemetryIngestRequest retryRequest = deserializeRawPayload(originalPayload.getRawPayload());

		if (retryRequest.getDeviceId() == null || retryRequest.getDeviceId().isBlank()) {

			throw new IllegalStateException("Stored raw payload does not contain a device id");
		}

		if (originalPayload.getDevice() == null || originalPayload.getDevice().getDeviceId() == null) {

			throw new IllegalStateException("Original payload is not linked to a valid device");
		}

		if (!originalPayload.getDevice().getDeviceId().equals(retryRequest.getDeviceId())) {

			throw new IllegalStateException("Stored raw payload device does not match the original payload device");
		}

		/*
		 * Keep the original meter reading time. receivePayload() will create a new
		 * retry-attempt record.
		 */
		receivePayload(retryRequest);
	}

	private List<Payload> getFilteredPayloadsForExport(PayloadFilterRequest request) {

		if (request == null) {
			request = new PayloadFilterRequest();
		}

		User loggedInUser = securityUtils.getLoggedInUser();

		// =====================================================
		// Filter validation
		// =====================================================

		if (request.getMinBattery() != null && (request.getMinBattery() < 0 || request.getMinBattery() > 100)) {

			throw new IllegalArgumentException("minBattery must be between 0 and 100");
		}

		if (request.getMaxBattery() != null && (request.getMaxBattery() < 0 || request.getMaxBattery() > 100)) {

			throw new IllegalArgumentException("maxBattery must be between 0 and 100");
		}

		if (request.getMinBattery() != null && request.getMaxBattery() != null
				&& request.getMinBattery() > request.getMaxBattery()) {

			throw new IllegalArgumentException("minBattery cannot be greater than maxBattery");
		}

		if (request.getMinSignal() != null && request.getMaxSignal() != null
				&& request.getMinSignal() > request.getMaxSignal()) {

			throw new IllegalArgumentException("minSignal cannot be greater than maxSignal");
		}

		if (request.getFrom() != null && request.getTo() != null && request.getFrom().isAfter(request.getTo())) {

			throw new IllegalArgumentException("from date cannot be after to date");
		}

		// =====================================================
		// Normalize date range
		// =====================================================

		LocalDateTime from = request.getFrom() != null ? request.getFrom().atStartOfDay() : null;

		LocalDateTime to = request.getTo() != null ? request.getTo().atTime(LocalTime.MAX) : null;

		// =====================================================
		// Normalize filters
		// =====================================================

		String deviceId = normalizeFilterValue(request.getDeviceId());

		String consumer = normalizeFilterValue(request.getConsumer());

		String meterNumber = normalizeFilterValue(request.getMeterNumber());

		String macAddress = normalizeFilterValue(request.getMacAddress());

		String search = normalizeFilterValue(request.getSearch());

		SourceType sourceType = request.getSourceType();

		if (sourceType == SourceType.ALL) {
			sourceType = null;
		}

		// =====================================================
		// Sorting
		// =====================================================

		String sortBy = resolvePayloadSortField(request.getSortBy());

		Sort.Direction sortDirection = request.getSortDirection() != null ? request.getSortDirection()
				: Sort.Direction.DESC;

		request.setDeviceId(deviceId);
		request.setConsumer(consumer);
		request.setMeterNumber(meterNumber);
		request.setMacAddress(macAddress);
		request.setSearch(search);
		request.setSourceType(sourceType);

		Specification<Payload> specification = PayloadSpecification.build(request, loggedInUser, from, to);

		return payloadRepository.findAll(specification, Sort.by(sortDirection, sortBy));
	}

	@Override
	@Transactional(readOnly = true)
	public PayloadExportResponse exportPayloads(PayloadFilterRequest request, String format) {

		if (format == null || format.isBlank()) {
			throw new IllegalArgumentException("Export format is required");
		}

		List<Payload> payloads = getFilteredPayloadsForExport(request);

		String normalizedFormat = format.trim().toLowerCase(Locale.ROOT);

		return switch (normalizedFormat) {

		case "csv" -> new PayloadExportResponse(generatePayloadCsv(payloads), "payloads.csv", "text/csv");

		case "excel", "xlsx" -> new PayloadExportResponse(generatePayloadExcel(payloads), "payloads.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		case "pdf" -> new PayloadExportResponse(generatePayloadPdf(payloads), "payloads.pdf", "application/pdf");

		default -> throw new IllegalArgumentException("Unsupported export format: " + format);
		};
	}

	private byte[] generatePayloadCsv(List<Payload> payloads) {

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

				PrintWriter writer = new PrintWriter(outputStreamWriter)) {

			writer.write('\uFEFF');

			writer.println(String.join(",", "Payload ID", "Timestamp", "Status", "Failure Reason", "Device PK ID",
					"Device ID", "Device Name", "Meter Number", "Meter Name", "Consumer Number", "MAC Address",
					"Source Type", "Technology Type", "Network Type", "Online", "Device Health", "Last Sync Time",
					"Start Reading", "End Reading", "Consumption", "Battery Percentage", "Signal Quality",
					"Signal Power", "SNR", "Firmware Version", "SIM Number"));

			for (Payload payload : payloads) {

				PayloadSummaryDTO summary = payloadMapper.toSummary(payload);

				writer.println(String.join(",", toCsvValue(summary.getId()),

						toCsvValue(summary.getTimestamp()),

						toCsvValue(summary.getStatus()),

						toCsvValue(summary.getFailureReason()),

						toCsvValue(summary.getDevicePkId()),

						toCsvValue(summary.getDeviceId()),

						toCsvValue(summary.getDeviceName()),

						toCsvValue(summary.getMeterNumber()),

						toCsvValue(summary.getMeterName()),

						toCsvValue(summary.getConsumerNumber()),

						toCsvValue(summary.getMacAddress()),

						toCsvValue(summary.getSourceType()),

						toCsvValue(summary.getTechnologyType()),

						toCsvValue(summary.getNetworkType()),

						toCsvValue(summary.getOnline()),

						toCsvValue(summary.getDeviceHealth()),

						toCsvValue(summary.getLastSyncTime()),

						toCsvValue(summary.getStartReading()),

						toCsvValue(summary.getEndReading()),

						toCsvValue(summary.getConsumption()),

						toCsvValue(summary.getBatteryPercentage()),

						toCsvValue(summary.getSignalQuality()),

						toCsvValue(summary.getSignalPower()),

						toCsvValue(summary.getSnr()),

						toCsvValue(summary.getFirmwareVersion()),

						toCsvValue(summary.getSimNumber())));
			}

			writer.flush();

			return outputStream.toByteArray();

		} catch (IOException exception) {

			throw new IllegalStateException("Unable to generate payload CSV export", exception);
		}
	}

	private String toCsvValue(Object value) {

		if (value == null) {
			return "";
		}

		String text = String.valueOf(value);

		boolean requiresQuotes = text.contains(",") || text.contains("\"") || text.contains("\n")
				|| text.contains("\r");

		if (text.contains("\"")) {

			text = text.replace("\"", "\"\"");
		}

		return requiresQuotes ? "\"" + text + "\"" : text;
	}

	private byte[] generatePayloadExcel(List<Payload> payloads) {

		try (XSSFWorkbook workbook = new XSSFWorkbook();

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			XSSFSheet sheet = workbook.createSheet("Payloads");

			CellStyle headerStyle = workbook.createCellStyle();

			Font headerFont = workbook.createFont();

			headerFont.setBold(true);

			headerStyle.setFont(headerFont);

			CellStyle dateTimeStyle = workbook.createCellStyle();

			CreationHelper creationHelper = workbook.getCreationHelper();

			dateTimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

			CellStyle decimalStyle = workbook.createCellStyle();

			decimalStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0.00"));

			String[] headers = { "Payload ID", "Timestamp", "Status", "Failure Reason", "Device PK ID", "Device ID",
					"Device Name", "Meter Number", "Meter Name", "Consumer Number", "MAC Address", "Source Type",
					"Technology Type", "Network Type", "Online", "Device Health", "Last Sync Time", "Start Reading",
					"End Reading", "Consumption", "Battery Percentage", "Signal Quality", "Signal Power", "SNR",
					"Firmware Version", "SIM Number" };

			Row headerRow = sheet.createRow(0);

			for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {

				Cell cell = headerRow.createCell(columnIndex);

				cell.setCellValue(headers[columnIndex]);

				cell.setCellStyle(headerStyle);
			}

			int rowIndex = 1;

			for (Payload payload : payloads) {

				PayloadSummaryDTO summary = payloadMapper.toSummary(payload);

				Row row = sheet.createRow(rowIndex++);

				int columnIndex = 0;

				setExcelCell(row, columnIndex++, summary.getId());

				setExcelCell(row, columnIndex++, summary.getTimestamp(), dateTimeStyle);

				setExcelCell(row, columnIndex++, summary.getStatus());

				setExcelCell(row, columnIndex++, summary.getFailureReason());

				setExcelCell(row, columnIndex++, summary.getDevicePkId());

				setExcelCell(row, columnIndex++, summary.getDeviceId());

				setExcelCell(row, columnIndex++, summary.getDeviceName());

				setExcelCell(row, columnIndex++, summary.getMeterNumber());

				setExcelCell(row, columnIndex++, summary.getMeterName());

				setExcelCell(row, columnIndex++, summary.getConsumerNumber());

				setExcelCell(row, columnIndex++, summary.getMacAddress());

				setExcelCell(row, columnIndex++, summary.getSourceType());

				setExcelCell(row, columnIndex++, summary.getTechnologyType());

				setExcelCell(row, columnIndex++, summary.getNetworkType());

				setExcelCell(row, columnIndex++, summary.getOnline());

				setExcelCell(row, columnIndex++, summary.getDeviceHealth());

				setExcelCell(row, columnIndex++, summary.getLastSyncTime(), dateTimeStyle);

				setExcelCell(row, columnIndex++, summary.getStartReading(), decimalStyle);

				setExcelCell(row, columnIndex++, summary.getEndReading(), decimalStyle);

				setExcelCell(row, columnIndex++, summary.getConsumption(), decimalStyle);

				setExcelCell(row, columnIndex++, summary.getBatteryPercentage());

				setExcelCell(row, columnIndex++, summary.getSignalQuality());

				setExcelCell(row, columnIndex++, summary.getSignalPower());

				setExcelCell(row, columnIndex++, summary.getSnr());

				setExcelCell(row, columnIndex++, summary.getFirmwareVersion());

				setExcelCell(row, columnIndex, summary.getSimNumber());
			}

			for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {

				sheet.autoSizeColumn(columnIndex);

				int currentWidth = sheet.getColumnWidth(columnIndex);

				int paddedWidth = currentWidth + 1000;

				sheet.setColumnWidth(columnIndex, Math.min(paddedWidth, 255 * 256));
			}

			workbook.write(outputStream);

			return outputStream.toByteArray();

		} catch (IOException exception) {

			throw new IllegalStateException("Unable to generate payload Excel export", exception);
		}
	}

	private void setExcelCell(Row row, int columnIndex, Object value) {

		setExcelCell(row, columnIndex, value, null);
	}

	private void setExcelCell(Row row, int columnIndex, Object value, CellStyle cellStyle) {

		Cell cell = row.createCell(columnIndex);

		if (value == null) {
			cell.setBlank();
			return;
		}

		if (value instanceof Number number) {

			cell.setCellValue(number.doubleValue());

		} else if (value instanceof Boolean booleanValue) {

			cell.setCellValue(booleanValue);

		} else if (value instanceof LocalDateTime dateTime) {

			cell.setCellValue(dateTime);

		} else if (value instanceof Enum<?> enumValue) {

			cell.setCellValue(enumValue.name());

		} else {

			cell.setCellValue(String.valueOf(value));
		}

		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
	}

	private byte[] generatePayloadPdf(List<Payload> payloads) {

		Document document = null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

			PdfWriter.getInstance(document, outputStream);

			document.open();

			com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

			Paragraph title = new Paragraph("Payload Export", titleFont);

			title.setAlignment(Element.ALIGN_CENTER);

			title.setSpacingAfter(12);

			document.add(title);

			String[] headers = { "ID", "Timestamp", "Status", "Device ID", "Device Name", "Meter", "Consumer", "Source",
					"Technology", "Network", "Start", "End", "Consumption", "Battery", "Signal", "Failure Reason" };

			PdfPTable table = new PdfPTable(headers.length);

			table.setWidthPercentage(100);

			table.setHeaderRows(1);

			table.setWidths(new float[] { 1.1f, 2.5f, 1.4f, 1.8f, 2.0f, 1.8f, 1.8f, 1.4f, 1.6f, 1.6f, 1.3f, 1.3f, 1.6f,
					1.2f, 1.2f, 2.8f });

			com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

			com.lowagie.text.Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

			for (String header : headers) {

				PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));

				cell.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				cell.setPadding(4);

				table.addCell(cell);
			}

			for (Payload payload : payloads) {

				PayloadSummaryDTO summary = payloadMapper.toSummary(payload);

				addPdfCell(table, summary.getId(), valueFont);

				addPdfCell(table, summary.getTimestamp(), valueFont);

				addPdfCell(table, summary.getStatus(), valueFont);

				addPdfCell(table, summary.getDeviceId(), valueFont);

				addPdfCell(table, summary.getDeviceName(), valueFont);

				addPdfCell(table, summary.getMeterNumber(), valueFont);

				addPdfCell(table, summary.getConsumerNumber(), valueFont);

				addPdfCell(table, summary.getSourceType(), valueFont);

				addPdfCell(table, summary.getTechnologyType(), valueFont);

				addPdfCell(table, summary.getNetworkType(), valueFont);

				addPdfCell(table, summary.getStartReading(), valueFont);

				addPdfCell(table, summary.getEndReading(), valueFont);

				addPdfCell(table, summary.getConsumption(), valueFont);

				addPdfCell(table, summary.getBatteryPercentage(), valueFont);

				addPdfCell(table, summary.getSignalQuality(), valueFont);

				addPdfCell(table, summary.getFailureReason(), valueFont);
			}

			document.add(table);

			document.close();

			document = null;

			return outputStream.toByteArray();

		} catch (DocumentException | IOException exception) {

			throw new IllegalStateException("Unable to generate payload PDF export", exception);

		} finally {

			if (document != null && document.isOpen()) {

				document.close();
			}
		}
	}

	private void addPdfCell(PdfPTable table, Object value, com.lowagie.text.Font font) {

		String text = value == null ? "" : String.valueOf(value);

		PdfPCell cell = new PdfPCell(new Phrase(text, font));

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell.setPadding(3);

		table.addCell(cell);
	}

	@Override
	@Transactional
	public void bulkDeletePayloads(List<Long> payloadIds) {

		User loggedInUser = securityUtils.getLoggedInUser();

		List<Long> uniquePayloadIds = payloadIds.stream().filter(Objects::nonNull).distinct().toList();

		if (uniquePayloadIds.isEmpty()) {
			throw new IllegalArgumentException("At least one valid payload id is required");
		}

		List<Payload> payloads = payloadRepository.findAllById(uniquePayloadIds);

		/*
		 * Validate that every requested payload exists before deleting anything.
		 */
		Set<Long> foundPayloadIds = payloads.stream().map(Payload::getId).collect(Collectors.toSet());

		List<Long> missingPayloadIds = uniquePayloadIds.stream().filter(id -> !foundPayloadIds.contains(id)).toList();

		if (!missingPayloadIds.isEmpty()) {
			throw new ResourceNotFoundException("Payloads not found: " + missingPayloadIds);
		}

		/*
		 * SUPER_ADMIN can delete all payloads. ADMIN can delete only payloads belonging
		 * to devices assigned to that admin.
		 */
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			List<Long> unauthorizedPayloadIds = payloads.stream()
					.filter(payload -> payload.getDevice() == null || payload.getDevice().getAssignedAdmin() == null
							|| !loggedInUser.getId().equals(payload.getDevice().getAssignedAdmin().getId()))
					.map(Payload::getId).toList();

			if (!unauthorizedPayloadIds.isEmpty()) {
				throw new AccessDeniedException("You are not authorized to delete payloads: " + unauthorizedPayloadIds);
			}
		}

		deleteBulkPayloadTelemetry(uniquePayloadIds);

		payloadRepository.deleteAllInBatch(payloads);
	}

	private void deleteBulkPayloadTelemetry(List<Long> payloadIds) {

		var energyTelemetry = energyTelemetryRepository.findByPayloadIds(payloadIds);

		if (!energyTelemetry.isEmpty()) {
			energyTelemetryRepository.deleteAllInBatch(energyTelemetry);
		}

		var waterTelemetry = waterTelemetryRepository.findByPayloadIds(payloadIds);

		if (!waterTelemetry.isEmpty()) {
			waterTelemetryRepository.deleteAllInBatch(waterTelemetry);
		}

		var gasTelemetry = gasTelemetryRepository.findByPayloadIds(payloadIds);

		if (!gasTelemetry.isEmpty()) {
			gasTelemetryRepository.deleteAllInBatch(gasTelemetry);
		}

		var solarTelemetry = solarTelemetryRepository.findByPayloadIds(payloadIds);

		if (!solarTelemetry.isEmpty()) {
			solarTelemetryRepository.deleteAllInBatch(solarTelemetry);
		}
	}

}
