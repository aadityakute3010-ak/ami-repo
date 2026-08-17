package com.ami.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceListResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.OfflineDeviceDto;
import com.ami.entity.ArchivedDevice;
import com.ami.entity.Device;
import com.ami.entity.Meter;
import com.ami.entity.User;

@Component
public class DeviceMapper {
	
	public DeviceListResponseDto mapToDeviceListResponse(Device device) {

		Meter meter = device.getMeter();
		DeviceListResponseDto dto = new DeviceListResponseDto();
		dto.setId(device.getId());
		dto.setDeviceId(device.getDeviceId());
		dto.setDeviceName(device.getDeviceName());
		dto.setSourceType(meter != null ? meter.getSourceType() : null);
		dto.setTechnologyType(meter != null ? meter.getTechnologyType() : null);
		dto.setSerialNumber(device.getSerialNumber());
		dto.setMacAddress(device.getMacAddress());
		dto.setStatus(meter != null ? meter.getStatus() : null);
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
	
	public DeviceDetailsResponseDto mapToDeviceDetailsResponse(Device device) {

		Meter meter = device.getMeter();

		return DeviceDetailsResponseDto.builder().id(device.getId()).deviceId(device.getDeviceId())
				.deviceName(device.getDeviceName()).meterName(meter != null ? meter.getMeterName() : null)

				// Device Information
				.sourceType(meter != null ? meter.getSourceType() : null)
				.technologyType(meter != null ? meter.getTechnologyType() : null)
				.status(meter != null ? meter.getStatus() : null)

				// Runtime
				.online(device.getOnline()).lastSyncTime(device.getLastSyncTime())

				// Device Identity
				.macAddress(device.getMacAddress()).serialNumber(device.getSerialNumber())

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
				.meterStartReading(meter != null ? meter.getMeterStartReading() : null)
				.meterType(meter != null ? meter.getMeterType() : null)
				.application(meter != null ? meter.getApplication() : null)
				.diameterSize(meter != null ? meter.getDiameterSize() : null)
				.literPerPulse(meter != null ? meter.getLiterPerPulse() : null)
				.ctRatio(meter != null ? meter.getCtRatio() : null).ptRatio(meter != null ? meter.getPtRatio() : null)
				.voltageClass(meter != null ? meter.getVoltageClass() : null)
				.inverterType(meter != null ? meter.getInverterType() : null)
				.plantCapacity(meter != null ? meter.getPlantCapacity() : null)
				.panelCount(meter != null ? meter.getPanelCount() : null)
				// Communication
				.wakeupTime(device.getWakeupTime()).dataSampleCount(device.getDataSampleCount()).build();
	}
	

	public DeviceUpdateFormResponseDto mapToDeviceUpdateFormResponse(Device device) {
		Meter meter = device.getMeter();
		return DeviceUpdateFormResponseDto.builder().deviceName(device.getDeviceName())
				.meterName(meter != null ? meter.getMeterName() : null).customerName(device.getCustomerName())
				.customerAddress(device.getCustomerAddress()).buildingOrWing(device.getBuildingOrWing())
				.area(device.getArea()).zone(device.getZone()).city(device.getCity()).state(device.getState())
				.meterLocation(device.getMeterLocation()).wakeupTime(device.getWakeupTime())
				.dataSampleCount(device.getDataSampleCount()).sourceType(meter != null ? meter.getSourceType() : null)
				.technologyType(meter != null ? meter.getTechnologyType() : null)
				.meterType(meter != null ? meter.getMeterType() : null)
				.application(meter != null ? meter.getApplication() : null)
				.diameterSize(meter != null ? meter.getDiameterSize() : null)
				.literPerPulse(meter != null ? meter.getLiterPerPulse() : null)
				.ctRatio(meter != null ? meter.getCtRatio() : null).ptRatio(meter != null ? meter.getPtRatio() : null)
				.voltageClass(meter != null ? meter.getVoltageClass() : null)
				.inverterType(meter != null ? meter.getInverterType() : null)
				.plantCapacity(meter != null ? meter.getPlantCapacity() : null)
				.panelCount(meter != null ? meter.getPanelCount() : null)
				.meterStartReading(meter != null ? meter.getMeterStartReading() : null).build();
	}
	

	public OfflineDeviceDto mapOfflineDevice(Device device) {

		return OfflineDeviceDto.builder().deviceId(device.getDeviceId()).deviceName(device.getDeviceName())
				.offlineSince(getTimeAgo(device.getLastSyncTime())).build();
	}
	private String getTimeAgo(LocalDateTime lastSyncTime) {

		if (lastSyncTime == null) {
			return "Unknown";
		}

		long minutes = Duration.between(lastSyncTime, LocalDateTime.now()).toMinutes();

		if (minutes < 60) {
			return minutes + " mins ago";
		}

		long hours = minutes / 60;

		if (hours < 24) {
			return hours + " hrs ago";
		}
		long days = hours / 24;
		return days + " days ago";
	}

	public ArchivedDevice mapToArchivedDevice(
	        Device device,
	        User archivedBy,
	        String archiveReason) {

	    ArchivedDevice archivedDevice = new ArchivedDevice();

	    BeanUtils.copyProperties(device, archivedDevice);

	    archivedDevice.setOriginalDeviceId(device.getId());

	    archivedDevice.setArchivedAt(LocalDateTime.now());

	    archivedDevice.setArchivedBy(archivedBy);

	    archivedDevice.setArchiveReason(archiveReason);

	    return archivedDevice;
	}
	

	public Device mapToDevice(
	        ArchivedDevice archivedDevice) {

	    Device device = new Device();

	    BeanUtils.copyProperties(
	            archivedDevice,
	            device);

	    device.setId(null);

	    return device;
	}
}