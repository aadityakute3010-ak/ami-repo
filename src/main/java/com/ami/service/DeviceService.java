package com.ami.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateDeviceRequestDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.entity.Device;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.repository.DeviceRepository;
import com.ami.repository.UserRepository;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository,UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }
    
    private String generateDeviceId() {
        return "DV-" + System.currentTimeMillis();
    }
    
    public DeviceResponseDto createDevice(CreateDeviceRequestDto request) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    	String loggedInEmail = authentication.getName();

    	User superAdmin = userRepository
    	        .findByEmail(loggedInEmail)
    	        .orElseThrow(() -> new RuntimeException("User not found"));
    	
    	if (superAdmin.getRole() != RoleType.SUPER_ADMIN) {
    	    throw new RuntimeException("Only Super Admin can create device");
    	} 
    	
    	User assignedAdmin = userRepository.findById(request.getAssignedAdminId())
    	                     .orElseThrow(() -> new RuntimeException("Assigned admin not found"));
    	
    	if (assignedAdmin.getRole() != RoleType.ADMIN) {
    	    throw new RuntimeException("Device can only be assigned to ADMIN");
    	} 
    	
    	if (!assignedAdmin.getAssignedSources().contains(request.getSourceType())) {
    	    throw new RuntimeException("Admin does not have source access");
    	} 
    	
    	if (deviceRepository.existsByMacAddress(request.getMacAddress())) {
    	    throw new RuntimeException("MAC Address already exists");
    	} 
    	
    	if (deviceRepository.existsBySerialNumber(request.getSerialNumber())) {
    	    throw new RuntimeException("Serial Number already exists");
    	}
    	
    	Device device = Device.builder()
    	        .deviceId(generateDeviceId())
    	        .deviceName(request.getDeviceName())
    	        .TechnologyType(request.getTechnologyType())
    	        .sourceType(request.getSourceType())
    	        .macAddress(request.getMacAddress())
    	        .serialNumber(request.getSerialNumber())
    	        .timezone(request.getTimezone())
    	        .sampleCount(request.getSampleCount())
    	        .wakeupTime(request.getWakeupTime())
    	        .firmwareVersion(request.getFirmwareVersion())
    	        .protocolType(request.getProtocolType())
    	        .otaUpdatesEnabled(request.getOtaUpdatesEnabled())
    	        .status(request.getStatus())
    	        .active(true)
    	        .online(false)
    	        .createdBy(superAdmin)
    	        .assignedAdmin(assignedAdmin)
    	        .assignedUser(null)
    	        .build(); 
    	
    	Device savedDevice = deviceRepository.save(device);
    	return mapToResponse(savedDevice); 
    }
    
    private DeviceResponseDto mapToResponse(Device device) { 

        return DeviceResponseDto.builder()
                .id(device.getId())
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .technologyType(device.getTechnologyType())
                .sourceType(device.getSourceType())
                .macAddress(device.getMacAddress())
                .serialNumber(device.getSerialNumber())   
                .timezone(device.getTimezone())
                .sampleCount(device.getSampleCount())
                .wakeupTime(device.getWakeupTime())
                .firmwareVersion(device.getFirmwareVersion())
                .protocolType(device.getProtocolType())
                .otaUpdatesEnabled(device.getOtaUpdatesEnabled())
                .status(device.getStatus())
                .active(device.getActive())
                .online(device.getOnline())
                .assignedAdminName(device.getAssignedAdmin().getFirstName()+ " "
                                 + device.getAssignedAdmin().getLastName())
                .assignedUserName(device.getAssignedUser() != null ?
                                  device.getAssignedUser().getFirstName() + " "
                                + device.getAssignedUser().getLastName() : null)
                .createdAt(device.getCreatedAt())
                .build(); 
    }
    
}