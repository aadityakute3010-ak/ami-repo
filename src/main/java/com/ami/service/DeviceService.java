package com.ami.service;

import java.util.List;

import com.ami.dto.responses.DeviceHealthResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.requests.UpdateConfigurationRequestDto;
import com.ami.dto.responses.ConfigurationResponseDto;
import com.ami.dto.responses.FirmwareResponseDto;
import com.ami.dto.responses.InventoryResponseDto;
import com.ami.dto.responses.LocationResponseDto;
import com.ami.dto.requests.CreateDevicesRequestDto;
import com.ami.dto.requests.UpdateDeviceRequestDto;
import com.ami.dto.requests.UpdateFirmwareRequestDto;
import com.ami.dto.responses.DashboardSummaryResponseDto;
import com.ami.dto.responses.DeviceAuditResponseDto;
import com.ami.dto.responses.DeviceBulkUploadResponseDto;
import com.ami.dto.responses.DeviceDashboardResponseDto;
import com.ami.dto.responses.DeviceDetailsResponseDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.dto.responses.DeviceUpdateFormResponseDto;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.FirmwareHistoryResponseDto;
import com.ami.dto.responses.PagedDeviceResponseDto;
import com.ami.enums.DeleteType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

public interface DeviceService {

	List<DeviceResponseDto> createDevices(CreateDevicesRequestDto request);

	PagedDeviceResponseDto getDevices(

	        int page,

	        int size,

	        String search,

	        DeviceStatus status,

	        SourceType sourceType,

	        TechnologyType technologyType,

	        String zone,

	        String location,

	        String sortBy,

	        String direction);

	DeviceResponseDto assignDeviceToUser(Long deviceId, Long userId);

	List<DeviceResponseDto> getAvailableDevicesForAssignment(Long userId);

	DashboardSummaryResponseDto getDashboardSummary();

	DeviceDetailsResponseDto getDeviceDetails(Long deviceId);

	String archiveDevice(
	        Long deviceId,
	        String archiveReason);
	
	String restoreDevice(
	        Long archivedDeviceId);

	DeviceResponseDto updateDevice(Long deviceId, UpdateDeviceRequestDto request);

	List<DeviceAuditResponseDto> getDeviceAudit(Long deviceId);

	DeviceUpdateFormResponseDto getDeviceForUpdate(Long deviceId);

	void assignAdminToDevice(Long deviceId, Long adminId);

	DeviceDashboardResponseDto getDeviceDashboard();

	DeviceBulkUploadResponseDto bulkUploadDevices(MultipartFile file);

	byte[] exportDevicesToCsv();
	
	ExportFileResponseDto exportDevices(String fileType); 
	
	// ==========================================
	// Water Module
	// ==========================================

	Page<InventoryResponseDto> getInventory(

	        int page,

	        int size,

	        String search,

	        SourceType sourceType,

	        DeviceStatus status,

	        String sortBy,

	        String direction);

	Page<LocationResponseDto> getLocations(

	        int page,

	        int size,

	        String search,

	        String zone,

	        String location,

	        Boolean online,

	        String sortBy,

	        String direction);

	Page<DeviceResponseDto> getDevicesByZone(

	        String zone,

	        int page,

	        int size,

	        String sortBy,

	        String direction);

	Page<DeviceResponseDto> getDevicesByLocation(

	        String location,

	        int page,

	        int size,

	        String sortBy,

	        String direction);

	Page<DeviceResponseDto> getDevicesBySource(

	        SourceType sourceType,

	        int page,

	        int size,

	        String sortBy,

	        String direction);

	FirmwareResponseDto getFirmware(
	        String deviceId);

	ConfigurationResponseDto getConfiguration(
	        String deviceId);

	ConfigurationResponseDto updateConfiguration(
	        String deviceId,
	        UpdateConfigurationRequestDto request);
	
	List<FirmwareHistoryResponseDto> getFirmwareHistory(
	        String deviceId);
	
	FirmwareResponseDto updateFirmware(
	        String deviceId,
	        UpdateFirmwareRequestDto request);
	
	DeviceHealthResponseDto getDeviceHealth(
	        String deviceId);
	
	

}