package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateDeviceOperationRequestDto;
import com.ami.dto.responses.DeviceOperationResponseDto;
import com.ami.entity.DeviceOperation;
import com.ami.repository.DeviceOperationRepository;
import com.ami.service.DeviceOperationService;

@Service
public class DeviceOperationServiceImpl implements DeviceOperationService {

	private final DeviceOperationRepository repository;

	public DeviceOperationServiceImpl(DeviceOperationRepository repository) {

		this.repository = repository;
	}

	@Override
	public DeviceOperationResponseDto createOperation(CreateDeviceOperationRequestDto request) {

		DeviceOperation operation = DeviceOperation.builder().deviceId(request.getDeviceId())
				.sourceType(request.getSourceType()).operationType(request.getOperationType()).title(request.getTitle())
				.description(request.getDescription()).severity(request.getSeverity()).status(request.getStatus())
				.assignedTo(request.getAssignedTo()).rootCause(request.getRootCause())
				.responseMessage(request.getResponseMessage()).latitude(request.getLatitude())
				.longitude(request.getLongitude()).resolved(request.getResolved())
				.executedAt(java.time.LocalDateTime.now()).acknowledgedBy(request.getAcknowledgedBy())
				.acknowledgedAt(java.time.LocalDateTime.now()).build();
		operation = repository.save(operation);

		return mapToResponse(operation);
	}

	@Override
	public List<DeviceOperationResponseDto> getAllOperations() {

		return repository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	public DeviceOperationResponseDto getOperationById(Long id) {

		DeviceOperation operation = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Operation not found"));

		return mapToResponse(operation);
	}

	@Override
	public List<DeviceOperationResponseDto> getByDeviceId(String deviceId) {

		return repository.findByDeviceId(deviceId).stream().map(this::mapToResponse).toList();
	}

	@Override
	public List<DeviceOperationResponseDto> getByOperationType(String operationType) {

		return repository.findByOperationType(operationType).stream().map(this::mapToResponse).toList();
	}

	@Override
	public String deleteOperation(Long id) {

		DeviceOperation operation = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Operation not found"));

		repository.delete(operation);

		return "Operation deleted successfully";
	}

	private DeviceOperationResponseDto mapToResponse(DeviceOperation operation) {

		return DeviceOperationResponseDto.builder().id(operation.getId()).deviceId(operation.getDeviceId())
				.sourceType(operation.getSourceType()).operationType(operation.getOperationType())
				.title(operation.getTitle()).description(operation.getDescription()).severity(operation.getSeverity())
				.status(operation.getStatus()).assignedTo(operation.getAssignedTo()).rootCause(operation.getRootCause())
				.latitude(operation.getLatitude()).longitude(operation.getLongitude()).resolved(operation.getResolved())
				.responseMessage(operation.getResponseMessage()).executedAt(operation.getExecutedAt())
				.acknowledgedBy(operation.getAcknowledgedBy()).acknowledgedAt(operation.getAcknowledgedAt()).build();
	}
}