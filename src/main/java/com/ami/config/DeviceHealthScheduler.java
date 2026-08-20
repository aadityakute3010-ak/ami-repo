package com.ami.config;

import com.ami.entity.Device;
import com.ami.enums.DeviceHealthStatus;
import com.ami.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceHealthScheduler {

    private final DeviceRepository deviceRepository;

    private static final long OFFLINE_AFTER_SECONDS = 40;

    @Scheduled(fixedRate = 10000) // check every 10 sec
    @Transactional
    public void markOfflineDevices() {

        LocalDateTime threshold = LocalDateTime.now().minusSeconds(OFFLINE_AFTER_SECONDS);

        List<Device> devices =
                deviceRepository.findByOnlineTrueAndLastSyncTimeBefore(threshold);

        for (Device device : devices) {
            device.setOnline(false);
            device.setHealthStatus(DeviceHealthStatus.OFFLINE);

            System.out.println("DEVICE MARKED OFFLINE: " + device.getDeviceId());
        }

        deviceRepository.saveAll(devices);
    }
}