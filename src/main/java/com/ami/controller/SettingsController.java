package com.ami.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.UpdateSettingsRequestDto;
import com.ami.dto.responses.SettingsResponseDto;
import com.ami.service.SettingsService;

@RestController
@RequestMapping("/api/ai/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(
            SettingsService settingsService) {

        this.settingsService = settingsService;
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public SettingsResponseDto
    getSettings() {

        return settingsService
                .getSettings();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping
    public SettingsResponseDto
    updateSettings(
            @RequestBody
            UpdateSettingsRequestDto request) {

        return settingsService
                .updateSettings(
                        request);
    }
}