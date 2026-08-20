package com.ami.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.ami.dto.requests.AssignBillingTypeRequestDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceBulkUploadResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceMapMarkerDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.enums.DeleteType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

public interface DeviceService {

	List<DeviceResponseDto> createDevices(CreateDevicesRequestDto request);

	PagedDeviceResponseDto getDevices(int page, int size, String search, DeviceStatus status, SourceType sourceType,
			TechnologyType technologyType, String fromDate, String toDate);

	DeviceResponseDto assignDeviceToUser(Long deviceId, Long userId);

	List<DeviceResponseDto> getAvailableDevicesForAssignment(Long userId);

	DashboardSummaryResponseDto getDashboardSummary();

	DeviceDetailsResponseDto getDeviceDetails(Long deviceId);

	String deleteDevice(Long deviceId, DeleteType deleteType, DeviceStatus status);

	DeviceResponseDto updateDevice(Long deviceId, UpdateDeviceRequestDto request);

	List<DeviceAuditResponseDto> getDeviceAudit(Long deviceId);

	DeviceUpdateFormResponseDto getDeviceForUpdate(Long deviceId);

	void assignAdminToDevice(Long deviceId, Long adminId);

	DeviceDashboardResponseDto getDeviceDashboard();

	DeviceBulkUploadResponseDto bulkUploadDevices(MultipartFile file);

	ExportFileResponseDto exportDevices(String fileType, String search, DeviceStatus status, SourceType sourceType,
			TechnologyType technologyType, String fromDate, String toDate);

	List<SourceType> getAssignedSourceTypes();
	
	List<DeviceMapMarkerDto> getDeviceMapMarkers();
	
	DeviceResponseDto assignBillingType(Long deviceId, AssignBillingTypeRequestDto request);

} 