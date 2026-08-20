package com.ami.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ami.entity.Device;
import com.ami.repository.DeviceRepository;
import com.ami.enums.DeviceHealthStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceHealthScheduler {

	private final DeviceRepository deviceRepository;

	@Scheduled(fixedRate = 300000) // 5 min
	@Transactional
	public void markOfflineDevices() {

		LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

		List<Device> devices = deviceRepository.findByOnlineTrueAndLastSyncTimeBefore(threshold);

		for (Device device : devices) {

			device.setOnline(false);
			device.setHealthStatus(DeviceHealthStatus.OFFLINE);
		}

		deviceRepository.saveAll(devices);
	}
}
