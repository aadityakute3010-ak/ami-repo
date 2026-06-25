package com.ami.service.impl;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.UpdateSettingsRequestDto;
import com.ami.dto.responses.SettingsResponseDto;
import com.ami.entity.AiSettings;
import com.ami.repository.AiSettingsRepository;
import com.ami.service.SettingsService;

@Service
public class SettingsServiceImpl
        implements SettingsService {

    private final AiSettingsRepository repository;

    public SettingsServiceImpl(
            AiSettingsRepository repository) {

        this.repository = repository;
    }

    @Override
    public SettingsResponseDto getSettings() {

        AiSettings settings =
                repository.findAll()
                        .stream()
                        .findFirst()
                        .orElseGet(() -> {

                            AiSettings defaultSettings =
                                    AiSettings.builder()
                                            .provider("GROQ")
                                            .model(
                                                    "llama-3.3-70b-versatile")
                                            .temperature(0.7)
                                            .maxTokens(2048)
                                            .enabled(true)
                                            .build();

                            return repository.save(
                                    defaultSettings);
                        });

        return mapToResponse(settings);
    }

    @Override
    public SettingsResponseDto updateSettings(
            UpdateSettingsRequestDto request) {

        AiSettings settings =
                repository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Settings not found"));

        settings.setProvider(
                request.getProvider());

        settings.setModel(
                request.getModel());

        settings.setTemperature(
                request.getTemperature());

        settings.setMaxTokens(
                request.getMaxTokens());

        settings.setEnabled(
                request.getEnabled());

        settings =
                repository.save(settings);

        return mapToResponse(settings);
    }

    private SettingsResponseDto
    mapToResponse(
            AiSettings settings) {

        return SettingsResponseDto
                .builder()
                .id(settings.getId())
                .provider(
                        settings.getProvider())
                .model(
                        settings.getModel())
                .temperature(
                        settings.getTemperature())
                .maxTokens(
                        settings.getMaxTokens())
                .enabled(
                        settings.getEnabled())
                .build();
    }
}