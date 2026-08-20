package com.ami.service;

import java.util.List;

import com.ami.dto.requests.AssignDeviceTariffRequest;
import com.ami.dto.responses.DeviceTariffAssignmentResponseDto;
import com.ami.dto.responses.TariffResponseDto;

public interface DeviceTariffAssignmentService {

	List<TariffResponseDto> getApplicableTariffs(Long deviceId);

	DeviceTariffAssignmentResponseDto assignTariff(Long deviceId, AssignDeviceTariffRequest request);

	DeviceTariffAssignmentResponseDto getAssignedTariff(Long deviceId);

	DeviceTariffAssignmentResponseDto updateAssignedTariff(Long deviceId, AssignDeviceTariffRequest request);

	void removeAssignedTariff(Long deviceId);
}