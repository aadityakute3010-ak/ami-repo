package com.ami.mapper;

import org.springframework.stereotype.Component;
import com.ami.dto.responses.PayloadDetailDTO;
import com.ami.dto.responses.PayloadSummaryDTO;
import com.ami.entity.Device;
import com.ami.entity.Meter;
import com.ami.entity.Payload;
import com.ami.entity.User;
import com.ami.entity.telemetry.EnergyTelemetry;
import com.ami.entity.telemetry.GasTelemetry;
import com.ami.entity.telemetry.SolarTelemetry;
import com.ami.entity.telemetry.WaterTelemetry;

@Component
public class PayloadMapper {

	/**
	 * Used for Payload Monitoring Table
	 */
	public PayloadSummaryDTO toSummary(Payload payload) {

		Device device = payload.getDevice();

		Meter meter = device != null ? device.getMeter() : null;

		return PayloadSummaryDTO.builder()

				.id(payload.getId())

				// =====================================================
				// Payload Information
				// =====================================================

				.timestamp(payload.getReceivedAt()).status(payload.getStatus())
				.failureReason(payload.getFailureReason())

				// =====================================================
				// Device Information
				// =====================================================

				.devicePkId(device != null ? device.getId() : null)

				.deviceId(device != null ? device.getDeviceId() : null)

				.deviceName(device != null ? device.getDeviceName() : null)

				/*
				 * Business rule: meterNumber is the same as Device.deviceId.
				 */
				.meterNumber(device != null ? device.getDeviceId() : null)

				.meterName(meter != null ? meter.getMeterName() : null)

				.consumerNumber(payload.getConsumerNumber())

				.macAddress(device != null ? device.getMacAddress() : null)

				.sourceType(meter != null ? meter.getSourceType() : null)

				.technologyType(payload.getDevice() != null && payload.getDevice().getMeter() != null
						? payload.getDevice().getMeter().getTechnologyType()
						: null)

				.networkType(payload.getDevice() != null && payload.getDevice().getMeter() != null
						&& payload.getDevice().getMeter().getTechnologyType() != null
								? payload.getDevice().getMeter().getTechnologyType().name()
								: null)

				.online(device != null ? device.getOnline() : null)

				.deviceHealth(device != null ? device.getHealthStatus() : null)

				.lastSyncTime(device != null ? device.getLastSyncTime() : null)

				// =====================================================
				// Reading Information
				// =====================================================

				.startReading(payload.getStartReading()).endReading(payload.getEndReading())
				.consumption(payload.getConsumption())

				// =====================================================
				// Communication Information
				// =====================================================

				.batteryPercentage(payload.getBatteryPercentage()).signalQuality(payload.getSignalQuality())
				.signalPower(payload.getSignalPower()).snr(payload.getSnr())
				.firmwareVersion(payload.getFirmwareVersion()).simNumber(payload.getSimNumber())

				.build();
	}

	/**
	 * Used for Payload Detail Modal
	 */
	public PayloadDetailDTO toDetail(Payload payload) {

		return toDetail(payload, null, null, null, null);
	}

	public PayloadDetailDTO toDetail(Payload payload, EnergyTelemetry energyTelemetry, WaterTelemetry waterTelemetry,
			GasTelemetry gasTelemetry, SolarTelemetry solarTelemetry) {

		Device device = payload.getDevice();

		Meter meter = device != null ? device.getMeter() : null;

		return PayloadDetailDTO.builder()

				.id(payload.getId())

				// =====================================================
				// Device Information
				// =====================================================

				.devicePkId(device != null ? device.getId() : null)

				.deviceId(device != null ? device.getDeviceId() : null)

				.deviceName(device != null ? device.getDeviceName() : null)

				/*
				 * Business rule: meterNumber is the same as Device.deviceId.
				 */
				.meterNumber(device != null ? device.getDeviceId() : null)

				.meterName(meter != null ? meter.getMeterName() : null)

				.consumerNumber(payload.getConsumerNumber())

				.macAddress(device != null ? device.getMacAddress() : null)

				.firmwareVersion(payload.getFirmwareVersion()).simNumber(payload.getSimNumber())

				// =====================================================
				// Customer and Location Information
				// =====================================================

				.customerName(device != null ? device.getCustomerName() : null)

				.customerAddress(device != null ? device.getCustomerAddress() : null)

				.buildingOrWing(device != null ? device.getBuildingOrWing() : null)

				.area(device != null ? device.getArea() : null)

				.zone(device != null ? device.getZone() : null)

				.city(device != null ? device.getCity() : null)

				.state(device != null ? device.getState() : null)

				.meterLocation(device != null ? device.getMeterLocation() : null)

				// =====================================================
				// Meter Information
				// =====================================================

				.sourceType(meter != null ? meter.getSourceType() : null)

				.technologyType(meter != null ? meter.getTechnologyType() : null)

				.technologyName(meter != null && meter.getTechnologyType() != null ? meter.getTechnologyType().name() : null)

				.networkType(meter != null && meter.getTechnologyType() != null ? meter.getTechnologyType().name() : null) 

				.meterType(meter != null ? meter.getMeterType() : null)

				.application(meter != null ? meter.getApplication() : null)

				.meterStatus(meter != null && meter.getStatus() != null ? meter.getStatus().name() : null)

				.diameterSize(meter != null ? meter.getDiameterSize() : null)

				.literPerPulse(meter != null ? meter.getLiterPerPulse() : null)

				.ctRatio(meter != null ? meter.getCtRatio() : null)

				.ptRatio(meter != null ? meter.getPtRatio() : null)

				.voltageClass(meter != null ? meter.getVoltageClass() : null)

				.inverterType(meter != null ? meter.getInverterType() : null)

				.plantCapacity(meter != null ? meter.getPlantCapacity() : null)

				.panelCount(meter != null ? meter.getPanelCount() : null)

				.meterStartReading(meter != null ? meter.getMeterStartReading() : null)

				// =====================================================
				// Reading and Balance Information
				// =====================================================

				.startReading(payload.getStartReading()).endReading(payload.getEndReading())
				.consumption(payload.getConsumption()).startBalance(payload.getStartBalance())
				.endBalance(payload.getEndBalance())

				// =====================================================
				// Communication Information
				// =====================================================

				.batteryPercentage(payload.getBatteryPercentage()).signalQuality(payload.getSignalQuality())
				.signalPower(payload.getSignalPower()).snr(payload.getSnr())

				// =====================================================
				// Device Runtime Information
				// =====================================================

				.online(device != null ? device.getOnline() : null)

				.lastSyncTime(device != null ? device.getLastSyncTime() : null)

				.dataSampleCount(device != null ? device.getDataSampleCount() : null)

				.wakeupTime(device != null ? device.getWakeupTime() : null)

				.deviceHealth(device != null ? device.getHealthStatus() : null)

				// =====================================================
				// Energy Telemetry
				// =====================================================

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

				// =====================================================
				// Water Telemetry
				// =====================================================

				.flowRate(waterTelemetry != null ? waterTelemetry.getFlowRate() : null)

				.pressure(waterTelemetry != null ? waterTelemetry.getPressure() : null)

				.tankLevel(waterTelemetry != null ? waterTelemetry.getTankLevel() : null)

				.pumpStatus(waterTelemetry != null ? waterTelemetry.getPumpStatus() : null)

				.leakDetected(waterTelemetry != null ? waterTelemetry.getLeakDetected() : null)

				// =====================================================
				// Gas Telemetry
				// =====================================================

				.gasFlow(gasTelemetry != null ? gasTelemetry.getGasFlow() : null)

				.gasPressure(gasTelemetry != null ? gasTelemetry.getGasPressure() : null)

				.gasVolume(gasTelemetry != null ? gasTelemetry.getGasVolume() : null)

				.temperature(gasTelemetry != null ? gasTelemetry.getTemperature() : null)

				.pipelineHealth(gasTelemetry != null ? gasTelemetry.getPipelineHealth() : null)

				// =====================================================
				// Solar Telemetry
				// =====================================================

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

				// =====================================================
				// Device State
				// =====================================================

				.valveStatus(payload.getValveStatus()).sensorStatus(payload.getSensorStatus())

				// =====================================================
				// Payload Status
				// =====================================================

				.status(payload.getStatus()).failureReason(payload.getFailureReason())

				// =====================================================
				// Ownership Information
				// =====================================================

				.createdBy(device != null ? getUserDisplayName(device.getCreatedBy()) : null)

				.assignedAdmin(device != null ? getUserDisplayName(device.getAssignedAdmin()) : null)

				.assignedUser(device != null ? getUserDisplayName(device.getAssignedUser()) : null)

				// =====================================================
				// Timeline
				// =====================================================

				.receivedAt(payload.getReceivedAt()).createdAt(payload.getCreatedAt()).updatedAt(payload.getUpdatedAt())

				// =====================================================
				// Raw Payload
				// =====================================================

				.rawPayload(payload.getRawPayload())

				.build();
	}

	private String getUserDisplayName(User user) {

		if (user == null) {
			return null;
		}

		String firstName = user.getFirstName();

		String lastName = user.getLastName();

		if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {

			return firstName.trim() + " " + lastName.trim();
		}

		if (firstName != null && !firstName.isBlank()) {
			return firstName.trim();
		}

		if (lastName != null && !lastName.isBlank()) {
			return lastName.trim();
		}

		if (user.getUserName() != null && !user.getUserName().isBlank()) {

			return user.getUserName().trim();
		}

		return user.getEmail();
	}

}