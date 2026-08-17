package com.ami.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateAdministrationConfigurationRequestDto;
import com.ami.dto.responses.AdministrationConfigurationResponseDto;
import com.ami.dto.responses.AdministrationDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.ConfigurationHistoryResponseDto;
import com.ami.dto.responses.VersionHistoryResponseDto;
import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;
import com.ami.service.AdministrationService;

@RestController
@RequestMapping("/api/administration")
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(
            AdministrationService administrationService) {

        this.administrationService = administrationService;
    }

    @PostMapping
    public ResponseEntity<AdministrationConfigurationResponseDto>
    createConfiguration(

            @RequestBody
            CreateAdministrationConfigurationRequestDto request) {

        return ResponseEntity.ok(

                administrationService.createConfiguration(
                        request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrationConfigurationResponseDto>
    getConfigurationById(

            @PathVariable Long id) {

        return ResponseEntity.ok(

                administrationService.getConfigurationById(
                        id));
    }

    @GetMapping
    public ResponseEntity<Page<AdministrationConfigurationResponseDto>>
    getAllConfigurations(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            ConfigurationType configurationType,

            @RequestParam(required = false)
            ConfigurationStatus status,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return ResponseEntity.ok(

                administrationService.getAllConfigurations(

                        page,

                        size,

                        search,

                        configurationType,

                        status,

                        sortBy,

                        direction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministrationConfigurationResponseDto>
    updateConfiguration(

            @PathVariable Long id,

            @RequestBody
            CreateAdministrationConfigurationRequestDto request) {

        return ResponseEntity.ok(

                administrationService.updateConfiguration(

                        id,

                        request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConfiguration(
            @PathVariable Long id) {

        return ResponseEntity.ok(

                administrationService.deleteConfiguration(
                        id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdministrationDashboardResponseDto>
    getDashboard() {

        return ResponseEntity.ok(

                administrationService.getDashboard());
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ConfigurationHistoryResponseDto>>
    getConfigurationHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(

                administrationService
                        .getConfigurationHistory(id));
    }
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<VersionHistoryResponseDto>>
    getVersionHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(

                administrationService
                        .getVersionHistory(id));
    }
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponseDto>>
    getAuditLogs(

            @RequestParam String module) {

        return ResponseEntity.ok(

                administrationService.getAuditLogs(
                        module));
    }
}