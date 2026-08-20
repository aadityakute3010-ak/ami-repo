package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateGasDeviceRequestDto;
import com.ami.dto.requests.UpdateGasDeviceRequestDto;
import com.ami.dto.responses.GasDeviceResponseDto;
import com.ami.entity.GasDevice;
import com.ami.repository.GasDeviceRepository;
import com.ami.service.GasDeviceService;

@Service
public class GasDeviceServiceImpl
        implements GasDeviceService {

    private final GasDeviceRepository repository;

    public GasDeviceServiceImpl(
            GasDeviceRepository repository) {

        this.repository = repository;
    }

    @Override
    public GasDeviceResponseDto createDevice(
            CreateGasDeviceRequestDto request) {

        GasDevice device =
                GasDevice.builder()
                        .deviceId(
                                request.getDeviceId())
                        .deviceName(
                                request.getDeviceName())
                        .serialNumber(
                                request.getSerialNumber())
                        .deviceType(
                                request.getDeviceType())
                        .manufacturer(
                                request.getManufacturer())
                        .firmwareVersion(
                                request.getFirmwareVersion())
                        .location(
                                request.getLocation())
                        .zoneName(
                                request.getZoneName())
                        .latitude(
                                request.getLatitude())
                        .longitude(
                                request.getLongitude())
                        .status(
                                request.getStatus())
                        .active(
                                request.getActive())
                        .build();

        device = repository.save(device);

        return mapToResponse(device);
    }

    @Override
    public List<GasDeviceResponseDto>
    getAllDevices() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public GasDeviceResponseDto
    getDeviceById(
            Long id) {

        GasDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Gas Device not found"));

        return mapToResponse(device);
    }

    @Override
    public GasDeviceResponseDto
    updateDevice(
            Long id,
            UpdateGasDeviceRequestDto request) {

        GasDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Gas Device not found"));

        device.setDeviceName(
                request.getDeviceName());

        device.setSerialNumber(
                request.getSerialNumber());

        device.setDeviceType(
                request.getDeviceType());

        device.setManufacturer(
                request.getManufacturer());

        device.setFirmwareVersion(
                request.getFirmwareVersion());

        device.setLocation(
                request.getLocation());

        device.setZoneName(
                request.getZoneName());

        device.setLatitude(
                request.getLatitude());

        device.setLongitude(
                request.getLongitude());

        device.setStatus(
                request.getStatus());

        device.setActive(
                request.getActive());

        device = repository.save(device);

        return mapToResponse(device);
    }

    @Override
    public String deleteDevice(
            Long id) {

        GasDevice device =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Gas Device not found"));

        repository.delete(device);

        return "Gas Device deleted successfully";
    }

    private GasDeviceResponseDto
    mapToResponse(
            GasDevice device) {

        return GasDeviceResponseDto
                .builder()
                .id(device.getId())
                .deviceId(
                        device.getDeviceId())
                .deviceName(
                        device.getDeviceName())
                .serialNumber(
                        device.getSerialNumber())
                .deviceType(
                        device.getDeviceType())
                .manufacturer(
                        device.getManufacturer())
                .firmwareVersion(
                        device.getFirmwareVersion())
                .location(
                        device.getLocation())
                .zoneName(
                        device.getZoneName())
                .latitude(
                        device.getLatitude())
                .longitude(
                        device.getLongitude())
                .status(
                        device.getStatus())
                .active(
                        device.getActive())
                .build();
    }
}