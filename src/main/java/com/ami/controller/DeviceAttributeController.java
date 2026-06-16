package com.ami.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ami.dto.requests.CreateAttributeKeyRequestDto;
import com.ami.dto.requests.CreateDeviceAttributeRequestDto;
import com.ami.dto.responses.DeviceAttributeResponseDto;
import com.ami.entity.AttributeKey;
import com.ami.service.DeviceAttributeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class DeviceAttributeController {

	private final DeviceAttributeService deviceAttributeService;

	// Dropdown 
	@GetMapping("/attributeKeys")
	public List<AttributeKey> getKeys() {
		return deviceAttributeService.getActiveAttributeKeys();
	}

	// Create attribute
	@PostMapping("/setAttribute/{deviceId}")
	public DeviceAttributeResponseDto set(@PathVariable Long deviceId,
			@RequestBody CreateDeviceAttributeRequestDto request) {
		return deviceAttributeService.createAttribute(deviceId, request);
	}  

	// Get attributes
	@GetMapping("/getAttributes/{deviceId}")
	public List<DeviceAttributeResponseDto> get(@PathVariable Long deviceId) {
		return deviceAttributeService.getDeviceAttributes(deviceId);
	}

	@PostMapping("/createKey")
	public AttributeKey createKey(@RequestBody CreateAttributeKeyRequestDto request) {
		return deviceAttributeService.createAttributeKey(request);
	} 

	@PutMapping("/updateKey/{id}")
	public AttributeKey updateKey(@PathVariable Long id, @RequestBody CreateAttributeKeyRequestDto request) {
		return deviceAttributeService.updateAttributeKey(id, request);
	}

	@DeleteMapping("/deleteKey/{id}")
	public void deleteKey(@PathVariable Long id) {
		deviceAttributeService.deleteAttributeKey(id);
	}
}