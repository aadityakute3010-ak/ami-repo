package com.ami.service;

import java.util.List;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceLocationRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

public interface DeviceService {

	List<DeviceResponseDto> createDevices(CreateDevicesRequestDto request);

	PagedDeviceResponseDto getDevices(int page, int size, String search, DeviceStatus status, SourceType sourceType,
			TechnologyType technologyType);

	DeviceResponseDto assignDeviceToUser(Long deviceId, Long userId);

	List<DeviceResponseDto> getAvailableDevicesForAssignment(Long userId);

	DashboardSummaryResponseDto getDashboardSummary();

	DeviceDetailsResponseDto getDeviceDetails(Long deviceId);

	void softDeleteDevice(Long deviceId);

	void hardDeleteDevice(Long deviceId);

	PagedDeviceResponseDto getDeletedDevices(int page, int size);

	void restoreDevice(Long deviceId);

	DeviceResponseDto updateDevice(Long deviceId, UpdateDeviceRequestDto request);

	DeviceResponseDto updateDeviceLocation(Long deviceId, UpdateDeviceLocationRequestDto request);

	List<DeviceAuditResponseDto> getDeviceAudit(Long deviceId);

	DeviceUpdateFormResponseDto getDeviceForUpdate(Long deviceId);
	
	void assignAdminToDevice(Long deviceId, Long adminId);

}