package com.ami.config;

import com.ami.entity.AttributeKey;
import com.ami.repository.AttributeKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttributeKeySeeder implements CommandLineRunner {

    private final AttributeKeyRepository attributeKeyRepository;

    @Override
    public void run(String... args) {

        // Firmware
        seedIfNotExists("Firmware Version", "", "Firmware");
        seedIfNotExists("Bootloader", "", "Firmware");
        seedIfNotExists("OTA Enabled", "", "Firmware");
        seedIfNotExists("Last Update", "", "Firmware");

        // Hardware
        seedIfNotExists("CPU", "", "Hardware");
        seedIfNotExists("RAM", "MB", "Hardware");
        seedIfNotExists("Storage", "GB", "Hardware");
        seedIfNotExists("Battery Health", "%", "Hardware");

        // Network
        seedIfNotExists("IP Address", "", "Network");
        seedIfNotExists("Signal", "dBm", "Network");
        seedIfNotExists("Protocol", "", "Network");
        seedIfNotExists("Latency", "ms", "Network");

        // Protocol
        seedIfNotExists("Encryption", "", "Protocol");
        seedIfNotExists("Keep Alive", "sec", "Protocol");
        seedIfNotExists("Packet Size", "bytes", "Protocol");
    }

    private void seedIfNotExists(String keyName,
                                 String unit,
                                 String category) {

        if (!attributeKeyRepository.existsByKeyNameIgnoreCase(keyName)) {

            AttributeKey key = AttributeKey.builder()
                    .keyName(keyName)
                    .unit(unit)
                    .category(category)
                    .active(true)
                    .build();

            attributeKeyRepository.save(key);
        }
    }
}