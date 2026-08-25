package com.ami.config;

import com.ami.dto.requests.TelemetryIngestRequest;
import com.ami.entity.Device;
import com.ami.entity.Meter;
import com.ami.entity.Payload;
import com.ami.entity.PrepaidBalance;
import com.ami.enums.BillingType;
import com.ami.enums.PayloadStatus;
import com.ami.enums.SensorStatus;
import com.ami.enums.SourceType;
import com.ami.enums.ValveStatus;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PayloadRepository;
import com.ami.repository.PrepaidBalanceRepository;
import com.ami.service.PayloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j 
@Component
@RequiredArgsConstructor
public class PayloadSimulatorScheduler {

	private final DeviceRepository deviceRepository;
	private final PayloadRepository payloadRepository;
	private final PayloadService payloadService;
	private final PrepaidBalanceRepository prepaidBalanceRepository;

	private final Random random = new Random();

	@Scheduled(fixedRate = 100000) // every 10 sec
	public void generatePayloadForActiveDevices() {

		List<Device> activeDevices = deviceRepository.findAllActiveDevices();

		for (Device device : activeDevices) {
			try {
				Meter meter = device.getMeter();

				if (meter == null || meter.getSourceType() == null) {
					continue;
				}

				TelemetryIngestRequest request = buildPayload(device, meter);

				payloadService.receivePayload(request);

				log.info("SIMULATED PAYLOAD SENT: {}", device.getDeviceId());

			} catch (Exception e) {
				log.error("SIMULATOR FAILED for device {}, reason: {}", device.getDeviceId(), e.getMessage(), e);
			}
		}
	}

	private TelemetryIngestRequest buildPayload(Device device, Meter meter) {

		Double previousReading = getPreviousReading(device, meter);

		Double consumption = generateConsumption(meter.getSourceType());

		Double startReading = previousReading;
		Double endReading = previousReading + consumption;

		TelemetryIngestRequest request = TelemetryIngestRequest.builder().deviceId(device.getDeviceId())
				.startReading(round(startReading)).endReading(round(endReading)).startBalance(1000.0)
				.endBalance(round(1000.0 - consumption)).batteryPercentage(generateBattery())
				.signalQuality(generateSignalQuality()).signalPower(generateSignalPower()).snr(generateSnr())
				.firmwareVersion("SIM-1.0.0").simNumber("SIMULATOR")
				.consumerNumber(device.getAssignedUser() != null ? device.getAssignedUser().getEmail() : "SIM-CONSUMER")
				.valveStatus(ValveStatus.ON).sensorStatus(SensorStatus.NORMAL)
				.rawPayload("SIMULATED_" + meter.getSourceType() + "_PAYLOAD").build();

		fillSourceTypePayload(request, meter.getSourceType(), consumption);

		return request;
	}

	private Double getPreviousReading(Device device, Meter meter) {

		if (device.getBillingType() == BillingType.PREPAID) {
			return getPrepaidPreviousReading(device, meter);
		}

		// EXISTING POSTPAID LOGIC — DO NOT CHANGE
		Optional<Payload> lastPayload = payloadRepository.findTopByDevice_IdAndStatusInOrderByReceivedAtDesc(
				device.getId(), List.of(PayloadStatus.SUCCESS, PayloadStatus.WARNING));

		if (lastPayload.isPresent() && lastPayload.get().getEndReading() != null) {
			return lastPayload.get().getEndReading();
		}

		if (meter.getMeterStartReading() != null) {
			return meter.getMeterStartReading();
		}

		return 0.0;
	}

	private Double getPrepaidPreviousReading(Device device, Meter meter) {

		Optional<PrepaidBalance> balance = prepaidBalanceRepository.findByDevice(device);

		if (balance.isPresent() && balance.get().getLastMeterReading() != null) {
			return balance.get().getLastMeterReading().doubleValue();
		}

		if (meter.getMeterStartReading() != null) {
			return meter.getMeterStartReading();
		}

		return 0.0;
	}

	private Double generateConsumption(SourceType sourceType) {

		return switch (sourceType) {
		case WATER -> randomBetween(0.01, 0.08); // liters/unit per 10 sec
		case ENERGY -> randomBetween(0.02, 0.15); // kWh per 10 sec
		case GAS -> randomBetween(0.005, 0.04); // gas units per 10 sec
		case SOLAR -> randomBetween(0.03, 0.20); // generated units per 10 sec
		default -> randomBetween(0.01, 0.05);
		};
	}

	private void fillSourceTypePayload(TelemetryIngestRequest request, SourceType sourceType, Double consumption) {

		switch (sourceType) {

		case WATER -> {

			Double flowRate = randomBetween(5.0, 25.0);

			Double pressure = randomBetween(1.0, 4.0);

			Double tankLevel = randomBetween(20.0, 100.0);

			Boolean leakDetected = random.nextDouble() < 0.03;

			String pumpStatus = tankLevel < 35.0 ? "ON" : "OFF";

			request.setFlowRate(round(flowRate));

			request.setPressure(round(pressure));

			request.setTankLevel(round(tankLevel));

			request.setPumpStatus(pumpStatus);

			request.setLeakDetected(leakDetected);
		}

		case ENERGY -> {

			Double voltage = randomBetween(220.0, 240.0);
			Double current = randomBetween(1.0, 10.0);
			Double apparentPower = (voltage * current) / 1000.0;
			Double powerFactor = randomBetween(0.85, 0.99);
			Double activePower = apparentPower * powerFactor;
			Double reactivePower = Math
					.sqrt(Math.max((apparentPower * apparentPower) - (activePower * activePower), 0.0));
			Double load = randomBetween(20.0, 95.0);
			Double demand = activePower * randomBetween(0.85, 1.15);
			request.setVoltage(round(voltage));
			request.setCurrent(round(current));
			request.setPower(round(activePower));
			request.setFrequency(round(randomBetween(49.5, 50.5)));
			request.setPowerFactor(round(powerFactor));
			request.setActivePower(round(activePower));
			request.setReactivePower(round(reactivePower));
			request.setApparentPower(round(apparentPower));
			request.setLoad(round(load));
			request.setDemand(round(demand));
		}

		case GAS -> {

			Double gasFlow = randomBetween(0.5, 5.0);

			Double gasPressure = randomBetween(1.0, 3.0);

			Double gasVolume = randomBetween(0.01, 0.20);

			Double temperature = randomBetween(15.0, 40.0);

			String pipelineHealth;

			if (gasPressure > 2.8 || temperature > 37.0) {

				pipelineHealth = "CRITICAL";
			}

			else if (gasPressure > 2.4 || temperature > 32.0) {

				pipelineHealth = "WARNING";
			}

			else {

				pipelineHealth = "HEALTHY";
			}

			request.setGasFlow(round(gasFlow));

			request.setGasPressure(round(gasPressure));

			request.setGasVolume(round(gasVolume));

			request.setTemperature(round(temperature));

			request.setPipelineHealth(pipelineHealth);
		}

		case SOLAR -> {

			Double solarVoltage = randomBetween(300.0, 600.0);

			Double solarCurrent = randomBetween(2.0, 15.0);

			Double solarPower = (solarVoltage * solarCurrent) / 1000.0;

			Double solarGeneration = randomBetween(0.03, 0.20);

			Double solarConsumption = randomBetween(0.01, 0.12);

			Double gridImport = solarConsumption > solarGeneration ? solarConsumption - solarGeneration : 0.0;

			Double gridExport = solarGeneration > solarConsumption ? solarGeneration - solarConsumption : 0.0;

			request.setSolarVoltage(round(solarVoltage));

			request.setSolarCurrent(round(solarCurrent));

			request.setSolarPower(round(solarPower));

			request.setSolarGeneration(round(solarGeneration));

			request.setSolarConsumption(round(solarConsumption));

			request.setPanelTemperature(round(randomBetween(25.0, 65.0)));

			request.setIrradiance(round(randomBetween(200.0, 1000.0)));

			request.setInverterStatus("ONLINE");

			request.setBatteryStorage(round(randomBetween(20.0, 100.0)));

			request.setGridImport(round(gridImport));

			request.setGridExport(round(gridExport));

			request.setEfficiency(round(randomBetween(80.0, 98.0)));
		}

		default -> {
			// no extra fields
		}
		}
	}

	private Integer generateBattery() {
		return 60 + random.nextInt(40);
	}

	private Integer generateSignalQuality() {
		return 20 + random.nextInt(80);
	}

	private Integer generateSignalPower() {
		return -90 + random.nextInt(40);
	}

	private Integer generateSnr() {
		return 10 + random.nextInt(20);
	}

	private Double randomBetween(Double min, Double max) {
		return min + (max - min) * random.nextDouble();
	}

	private Double round(Double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}