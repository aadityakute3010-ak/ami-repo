package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateGasDeviceRequestDto;
import com.ami.dto.requests.UpdateGasDeviceRequestDto;
import com.ami.dto.responses.GasDeviceResponseDto;
import com.ami.service.GasDeviceService;

@RestController
@RequestMapping("/api/gas/devices")
public class GasDeviceController {

    private final GasDeviceService service;

    public GasDeviceController(
            GasDeviceService service) {

        this.service = service;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public GasDeviceResponseDto createDevice(
            @RequestBody CreateGasDeviceRequestDto request) {

        return service.createDevice(
                request);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public List<GasDeviceResponseDto> getAllDevices() {

        return service.getAllDevices();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{id}")
    public GasDeviceResponseDto getDeviceById(
            @PathVariable Long id) {

        return service.getDeviceById(id);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public GasDeviceResponseDto updateDevice(
            @PathVariable Long id,
            @RequestBody UpdateGasDeviceRequestDto request) {

        return service.updateDevice(
                id,
                request);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteDevice(
            @PathVariable Long id) {

        return service.deleteDevice(id);
    }
}