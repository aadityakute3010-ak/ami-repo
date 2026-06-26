package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;

public interface DeviceOperationService {

    DeviceOperationResponseDto createOperation(
            CreateDeviceOperationRequestDto request);

    List<DeviceOperationResponseDto>
    getAllOperations();

    DeviceOperationResponseDto
    getOperationById(
            Long id);

    List<DeviceOperationResponseDto>
    getByDeviceId(
            String deviceId);

    List<DeviceOperationResponseDto>
    getByOperationType(
            String operationType);

    String deleteOperation(
            Long id);
}