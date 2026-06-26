package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateGasDeviceRequestDto;
import com.ami.dto.requests.UpdateGasDeviceRequestDto;
import com.ami.dto.responses.GasDeviceResponseDto;

public interface GasDeviceService {

    GasDeviceResponseDto createDevice(
            CreateGasDeviceRequestDto request);

    List<GasDeviceResponseDto> getAllDevices();

    GasDeviceResponseDto getDeviceById(
            Long id);

    GasDeviceResponseDto updateDevice(
            Long id,
            UpdateGasDeviceRequestDto request);

    String deleteDevice(
            Long id);
}