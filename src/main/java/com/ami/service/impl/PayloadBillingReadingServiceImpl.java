package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.responses.PayloadBillingReadingResponseDto;
import com.ami.entity.DailyConsumption;
import com.ami.entity.Device;
import com.ami.enums.DeviceStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.DailyConsumptionRepository;
import com.ami.repository.DeviceRepository;
import com.ami.service.PayloadBillingReadingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayloadBillingReadingServiceImpl implements PayloadBillingReadingService {

	private final DailyConsumptionRepository dailyConsumptionRepository;

	private final DeviceRepository deviceRepository;

	@Override
	@Transactional(readOnly = true)
	public PayloadBillingReadingResponseDto getBillingReadings(Long deviceId, LocalDate billingPeriodFrom,
			LocalDate billingPeriodTo) {

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		if (device.getMeter() == null) {
			throw new IllegalStateException("Meter not configured for device");
		}

		if (device.getMeter().getStatus() == DeviceStatus.INACTIVE) {
			throw new IllegalStateException("Billing cannot be calculated for inactive device");
		}

		if (billingPeriodFrom == null || billingPeriodTo == null) {
			throw new IllegalArgumentException("Billing period from and to dates are required");
		}

		if (billingPeriodFrom.isAfter(billingPeriodTo)) {
			throw new IllegalArgumentException("Billing period from date cannot be after to date");
		}

		List<DailyConsumption> readings = dailyConsumptionRepository.findBillingReadingsByDeviceAndDateRange(deviceId,
				billingPeriodFrom, billingPeriodTo);

		if (readings.isEmpty()) {
			throw new IllegalStateException("No payload readings found for selected billing period");
		}

		DailyConsumption firstReading = readings.get(0);
		DailyConsumption lastReading = readings.get(readings.size() - 1);

		if (firstReading.getOpeningReading() == null || lastReading.getClosingReading() == null) {
			throw new IllegalStateException("Incomplete readings found for selected billing period");
		}

		BigDecimal previousReading = BigDecimal.valueOf(firstReading.getOpeningReading());
		BigDecimal currentReading = BigDecimal.valueOf(lastReading.getClosingReading());

		BigDecimal totalConsumption = readings.stream().map(DailyConsumption::getDailyConsumption)
				.filter(value -> value != null).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);

		if (currentReading.compareTo(previousReading) < 0) {
			throw new IllegalStateException("Invalid readings found. Current reading is less than previous reading");
		}

		return PayloadBillingReadingResponseDto.builder().deviceId(device.getId()).deviceCode(device.getDeviceId())
				.deviceName(device.getDeviceName()).billingPeriodFrom(billingPeriodFrom)
				.billingPeriodTo(billingPeriodTo).previousReading(previousReading).currentReading(currentReading)
				.totalConsumption(totalConsumption).totalReadingDays((long) readings.size()).build();
	}
}