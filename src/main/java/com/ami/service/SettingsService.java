package com.ami.service;

import com.ami.dto.requests.UpdateSettingsRequestDto;
import com.ami.dto.responses.SettingsResponseDto;

public interface SettingsService {

    SettingsResponseDto getSettings();

    SettingsResponseDto updateSettings(
            UpdateSettingsRequestDto request);
}