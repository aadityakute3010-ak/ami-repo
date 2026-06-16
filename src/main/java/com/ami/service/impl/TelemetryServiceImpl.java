package com.ami.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.dto.requests.TelemetryRequestDto;
import com.ami.dto.responses.EnergyTelemetryResponseDto;
import com.ami.dto.responses.GasTelemetryResponseDto;
import com.ami.dto.responses.SolarTelemetryResponseDto;
import com.ami.dto.responses.TelemetryHistoryResponseDto;
import com.ami.dto.responses.TelemetryResponseDto;
import com.ami.dto.responses.WaterTelemetryResponseDto;
import com.ami.entity.Device;
import com.ami.entity.User;
import com.ami.entity.telemetry.EnergyTelemetry;
import com.ami.entity.telemetry.GasTelemetry;
import com.ami.entity.telemetry.SolarTelemetry;
import com.ami.entity.telemetry.WaterTelemetry;
import com.ami.enums.RoleType;
import com.ami.repository.DeviceRepository;
import com.ami.repository.EnergyTelemetryRepository;
import com.ami.repository.GasTelemetryRepository;
import com.ami.repository.SolarTelemetryRepository;
import com.ami.repository.UserRepository;
import com.ami.repository.WaterTelemetryRepository;
import com.ami.service.TelemetryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

	private final DeviceRepository deviceRepository;
	private final EnergyTelemetryRepository energyTelemetryRepository;
	private final WaterTelemetryRepository waterTelemetryRepository;
	private final GasTelemetryRepository gasTelemetryRepository;
	private final SolarTelemetryRepository solarTelemetryRepository;
	private final UserRepository userRepository;

	private Device getValidDevice(String deviceId) {
		Device device = deviceRepository.findByDeviceId(deviceId)
				.orElseThrow(() -> new RuntimeException("Device not found"));
		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device is inactive");
		}
		return device;
	}

	private User getLoggedInUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	private void validateDeviceAccess(Device device, User loggedInUser) {
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}
		if (loggedInUser.getRole() == RoleType.ADMIN) {
			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			return;
		}
		if (loggedInUser.getRole() == RoleType.USER) {
			if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			return;
		}
		throw new RuntimeException("Access Denied");
	}

	private void updateDeviceSync(Device device) {
		device.setOnline(true);
		device.setLastSyncTime(LocalDateTime.now());
		deviceRepository.save(device);
	}

	@Transactional
	@Override
	public void saveTelemetry(TelemetryRequestDto request) {

		Device device = getValidDevice(request.getDeviceId());

		if (device.getMeter() == null) {
			throw new RuntimeException("Meter not configured for device");
		}

		switch (device.getMeter().getSourceType()) {

		case ENERGY:
			saveEnergyTelemetry(device, request);
			break;

		case WATER:
			saveWaterTelemetry(device, request);
			break;

		case GAS:
			saveGasTelemetry(device, request);
			break;

		case SOLAR:
			saveSolarTelemetry(device, request);
			break;

		default:
			throw new RuntimeException("Unsupported source type");
		}

		updateDeviceSync(device);
	} 

	@Override
	@Transactional
	public void saveTelemetryFromIngest(TelemetryIngestRequest request) {

		Device device = getValidDevice(request.getDeviceId());

		if (device.getMeter() == null) {
		    throw new RuntimeException("Meter not configured for device");
		}

		switch (device.getMeter().getSourceType()) {

		case ENERGY:
			saveEnergyTelemetry(device, request);
			break;

		case WATER:
			saveWaterTelemetry(device, request);
			break;

		case GAS:
			saveGasTelemetry(device, request);
			break;

		case SOLAR:
			saveSolarTelemetry(device, request);
			break;

		default:
			throw new RuntimeException("Unsupported Source Type");
		}
	} 

	private void saveWaterTelemetry(Device device, TelemetryIngestRequest request) {

		WaterTelemetry telemetry = WaterTelemetry.builder()

				.device(device)

				.flowRate(request.getFlowRate())

				.pressure(request.getPressure())

				.totalConsumption(request.getEndReading())

				.batteryLevel(
						request.getBatteryPercentage() != null ? request.getBatteryPercentage().doubleValue() : null)

				.signalStrength(request.getSignalQuality() != null ? request.getSignalQuality().doubleValue() : null)

				.readingTime(LocalDateTime.now())

				.build();

		waterTelemetryRepository.save(telemetry);
	}

	private void saveEnergyTelemetry(Device device, TelemetryIngestRequest request) {

		EnergyTelemetry telemetry = EnergyTelemetry.builder()

				.device(device)

				.voltage(request.getVoltage()).current(request.getCurrent()).power(request.getPower())
				.frequency(request.getFrequency()).powerFactor(request.getPowerFactor())

				.energyConsumed(request.getEndReading())

				.batteryLevel(
						request.getBatteryPercentage() != null ? request.getBatteryPercentage().doubleValue() : null)

				.signalStrength(request.getSignalQuality() != null ? request.getSignalQuality().doubleValue() : null)

				.readingTime(LocalDateTime.now())

				.build();

		energyTelemetryRepository.save(telemetry);
	}

	private void saveGasTelemetry(Device device, TelemetryIngestRequest request) {

		GasTelemetry telemetry = GasTelemetry.builder()

				.device(device)

				.gasFlow(request.getGasFlow())

				.gasPressure(request.getGasPressure())

				.totalConsumption(request.getEndReading())

				.batteryLevel(
						request.getBatteryPercentage() != null ? request.getBatteryPercentage().doubleValue() : null)

				.signalStrength(request.getSignalQuality() != null ? request.getSignalQuality().doubleValue() : null)

				.readingTime(LocalDateTime.now())

				.build();

		gasTelemetryRepository.save(telemetry);
	}

	private void saveSolarTelemetry(Device device, TelemetryIngestRequest request) {

		SolarTelemetry telemetry = SolarTelemetry.builder()

				.device(device)

				.solarVoltage(request.getSolarVoltage())

				.solarCurrent(request.getSolarCurrent())

				.solarPower(request.getSolarPower())

				.energyGenerated(request.getEndReading())

				.batteryLevel(
						request.getBatteryPercentage() != null ? request.getBatteryPercentage().doubleValue() : null)

				.signalStrength(request.getSignalQuality() != null ? request.getSignalQuality().doubleValue() : null)

				.readingTime(LocalDateTime.now())

				.build();

		solarTelemetryRepository.save(telemetry);
	}

	private void saveEnergyTelemetry(Device device, TelemetryRequestDto request) {

		EnergyTelemetry telemetry = EnergyTelemetry.builder().device(device).voltage(request.getVoltage())
				.current(request.getCurrent()).power(request.getPower()).frequency(request.getFrequency())
				.powerFactor(request.getPowerFactor()).energyConsumed(request.getEnergyConsumed())
				.batteryLevel(request.getBatteryLevel()).signalStrength(request.getSignalStrength())
				.readingTime(LocalDateTime.now()).build();

		energyTelemetryRepository.save(telemetry);
	}

	private void saveWaterTelemetry(Device device, TelemetryRequestDto request) {

		WaterTelemetry telemetry = WaterTelemetry.builder().device(device).flowRate(request.getFlowRate())
				.pressure(request.getPressure()).totalConsumption(request.getTotalConsumption())
				.batteryLevel(request.getBatteryLevel()).signalStrength(request.getSignalStrength())
				.readingTime(LocalDateTime.now()).build();

		waterTelemetryRepository.save(telemetry);
	}

	private void saveGasTelemetry(Device device, TelemetryRequestDto request) {

		GasTelemetry telemetry = GasTelemetry.builder().device(device).gasFlow(request.getGasFlow())
				.gasPressure(request.getGasPressure()).totalConsumption(request.getTotalConsumption())
				.batteryLevel(request.getBatteryLevel()).signalStrength(request.getSignalStrength())
				.readingTime(LocalDateTime.now()).build();

		gasTelemetryRepository.save(telemetry);
	}

	private void saveSolarTelemetry(Device device, TelemetryRequestDto request) {

		SolarTelemetry telemetry = SolarTelemetry.builder().device(device).solarVoltage(request.getSolarVoltage())
				.solarCurrent(request.getSolarCurrent()).solarPower(request.getSolarPower())
				.energyGenerated(request.getEnergyGenerated()).batteryLevel(request.getBatteryLevel())
				.signalStrength(request.getSignalStrength()).readingTime(LocalDateTime.now()).build();

		solarTelemetryRepository.save(telemetry);
	}

	@Override
	public TelemetryResponseDto getLatestTelemetry(Long deviceId) {

		User loggedInUser = getLoggedInUser();
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device not found");
		}
		validateDeviceAccess(device, loggedInUser);

		Object telemetryData = null;

		if (device.getMeter() == null) {
		    throw new RuntimeException("Meter not configured for device");
		}

		switch (device.getMeter().getSourceType()) {

		case ENERGY:
			EnergyTelemetry energyTelemetry = energyTelemetryRepository.findTopByDeviceOrderByReadingTimeDesc(device)
					.orElse(null);

			telemetryData = energyTelemetry == null ? null
					: EnergyTelemetryResponseDto.builder().voltage(energyTelemetry.getVoltage())
							.current(energyTelemetry.getCurrent()).power(energyTelemetry.getPower())
							.frequency(energyTelemetry.getFrequency()).powerFactor(energyTelemetry.getPowerFactor())
							.energyConsumed(energyTelemetry.getEnergyConsumed())
							.batteryLevel(energyTelemetry.getBatteryLevel())
							.signalStrength(energyTelemetry.getSignalStrength())
							.readingTime(energyTelemetry.getReadingTime()).build();
			break;

		case WATER:
			WaterTelemetry waterTelemetry = waterTelemetryRepository.findTopByDeviceOrderByReadingTimeDesc(device)
					.orElse(null);

			telemetryData = waterTelemetry == null ? null
					: WaterTelemetryResponseDto.builder().flowRate(waterTelemetry.getFlowRate())
							.pressure(waterTelemetry.getPressure())
							.totalConsumption(waterTelemetry.getTotalConsumption())
							.batteryLevel(waterTelemetry.getBatteryLevel())
							.signalStrength(waterTelemetry.getSignalStrength())
							.readingTime(waterTelemetry.getReadingTime()).build();
			break;

		case GAS:

			GasTelemetry gasTelemetry = gasTelemetryRepository.findTopByDeviceOrderByReadingTimeDesc(device)
					.orElse(null);

			telemetryData = gasTelemetry == null ? null
					: GasTelemetryResponseDto.builder().gasFlow(gasTelemetry.getGasFlow())
							.gasPressure(gasTelemetry.getGasPressure())
							.totalConsumption(gasTelemetry.getTotalConsumption())
							.batteryLevel(gasTelemetry.getBatteryLevel())
							.signalStrength(gasTelemetry.getSignalStrength()).readingTime(gasTelemetry.getReadingTime())
							.build();

			break;

		case SOLAR:

			SolarTelemetry solarTelemetry = solarTelemetryRepository.findTopByDeviceOrderByReadingTimeDesc(device)
					.orElse(null);

			telemetryData = solarTelemetry == null ? null
					: SolarTelemetryResponseDto.builder().solarVoltage(solarTelemetry.getSolarVoltage())
							.solarCurrent(solarTelemetry.getSolarCurrent()).solarPower(solarTelemetry.getSolarPower())
							.energyGenerated(solarTelemetry.getEnergyGenerated())
							.batteryLevel(solarTelemetry.getBatteryLevel())
							.signalStrength(solarTelemetry.getSignalStrength())
							.readingTime(solarTelemetry.getReadingTime()).build();

			break;

		default:
			throw new RuntimeException("Unsupported source type");
		}
		return TelemetryResponseDto.builder().deviceId(device.getId()).deviceCode(device.getDeviceId())
				.sourceType(device.getMeter().getSourceType().name()).telemetryData(telemetryData).build();
	}

	@Override
	public TelemetryHistoryResponseDto getTelemetryHistory(Long deviceId, LocalDate from, LocalDate to) {
		if (from.isAfter(to)) {
			throw new RuntimeException("From date cannot be greater than To date");
		}
		User loggedInUser = getLoggedInUser();
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device not found");
		}
		validateDeviceAccess(device, loggedInUser);

		LocalDateTime startDateTime = from.atStartOfDay();
		LocalDateTime endDateTime = to.atTime(23, 59, 59);

		List<?> telemetryRecords;

		if (device.getMeter() == null) {
		    throw new RuntimeException("Meter not configured for device");
		}

		switch (device.getMeter().getSourceType()) {

		case WATER:
			List<WaterTelemetryResponseDto> waterHistory = waterTelemetryRepository
					.findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(device, startDateTime, endDateTime).stream()
					.map(water -> WaterTelemetryResponseDto.builder().flowRate(water.getFlowRate())
							.pressure(water.getPressure()).totalConsumption(water.getTotalConsumption())
							.batteryLevel(water.getBatteryLevel()).signalStrength(water.getSignalStrength())
							.readingTime(water.getReadingTime()).build())
					.toList();
			telemetryRecords = waterHistory;
			break;

		case ENERGY:
			List<EnergyTelemetryResponseDto> energyHistory = energyTelemetryRepository
					.findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(device, startDateTime, endDateTime).stream()
					.map(energy -> EnergyTelemetryResponseDto.builder().voltage(energy.getVoltage())
							.current(energy.getCurrent()).power(energy.getPower()).frequency(energy.getFrequency())
							.powerFactor(energy.getPowerFactor()).energyConsumed(energy.getEnergyConsumed())
							.batteryLevel(energy.getBatteryLevel()).signalStrength(energy.getSignalStrength())
							.readingTime(energy.getReadingTime()).build())
					.toList();
			telemetryRecords = energyHistory;
			break;

		case GAS:
			List<GasTelemetryResponseDto> gasHistory = gasTelemetryRepository
					.findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(device, startDateTime, endDateTime).stream()
					.map(gas -> GasTelemetryResponseDto.builder().gasFlow(gas.getGasFlow())
							.gasPressure(gas.getGasPressure()).totalConsumption(gas.getTotalConsumption())
							.batteryLevel(gas.getBatteryLevel()).signalStrength(gas.getSignalStrength())
							.readingTime(gas.getReadingTime()).build())
					.toList();
			telemetryRecords = gasHistory;
			break;

		case SOLAR:
			List<SolarTelemetryResponseDto> solarHistory = solarTelemetryRepository
					.findByDeviceAndReadingTimeBetweenOrderByReadingTimeAsc(device, startDateTime, endDateTime).stream()
					.map(solar -> SolarTelemetryResponseDto.builder().solarVoltage(solar.getSolarVoltage())
							.solarCurrent(solar.getSolarCurrent()).solarPower(solar.getSolarPower())
							.energyGenerated(solar.getEnergyGenerated()).batteryLevel(solar.getBatteryLevel())
							.signalStrength(solar.getSignalStrength()).readingTime(solar.getReadingTime()).build())
					.toList();
			telemetryRecords = solarHistory;
			break;
		default:
			throw new RuntimeException("Unsupported source type");
		}

		return TelemetryHistoryResponseDto.builder().deviceId(device.getId()).deviceCode(device.getDeviceId())
				.sourceType(device.getMeter().getSourceType().name()).telemetryRecords(telemetryRecords).build();
	}

}
