package com.ami.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ami.repository.ArchivedDeviceOperationRepository;
import com.ami.repository.ArchivedDeviceRepository;
import com.ami.repository.ArchivedDeviceTelemetryRepository;

@Component
public class ArchiveCleanupScheduler {

    private final ArchivedDeviceRepository archivedDeviceRepository;

    private final ArchivedDeviceOperationRepository archivedDeviceOperationRepository;

    private final ArchivedDeviceTelemetryRepository archivedDeviceTelemetryRepository;

    public ArchiveCleanupScheduler(
            ArchivedDeviceRepository archivedDeviceRepository,
            ArchivedDeviceOperationRepository archivedDeviceOperationRepository,
            ArchivedDeviceTelemetryRepository archivedDeviceTelemetryRepository) {

        this.archivedDeviceRepository = archivedDeviceRepository;
        this.archivedDeviceOperationRepository = archivedDeviceOperationRepository;
        this.archivedDeviceTelemetryRepository = archivedDeviceTelemetryRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupArchivedData() {

        LocalDateTime expiryDate =
                LocalDateTime.now().minusYears(3);

        archivedDeviceRepository.deleteByArchivedAtBefore(
                expiryDate);

        archivedDeviceOperationRepository.deleteByArchivedAtBefore(
                expiryDate);

        archivedDeviceTelemetryRepository.deleteByArchivedAtBefore(
                expiryDate);

        System.out.println(
                "Archive cleanup completed at "
                        + LocalDateTime.now());
    }
}