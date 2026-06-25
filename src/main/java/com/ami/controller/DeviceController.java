package com.ami.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ami.dto.requests.CreateDeviceRequestDto;
import com.ami.dto.responses.DeviceResponseDto;
import com.ami.service.DeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    } 

    @PostMapping("/createDevice")
    public DeviceResponseDto createDevice(@Valid @RequestBody CreateDeviceRequestDto request) {
        return deviceService.createDevice(request);
    } 
}