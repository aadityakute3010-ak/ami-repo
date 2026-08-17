package com.ami.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CreateAdministrationConfigurationRequestDto;
import com.ami.dto.responses.AdministrationConfigurationResponseDto;
import com.ami.dto.responses.AdministrationDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.ConfigurationHistoryResponseDto;
import com.ami.dto.responses.VersionHistoryResponseDto;
import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;

public interface AdministrationService {

    AdministrationConfigurationResponseDto createConfiguration(
            CreateAdministrationConfigurationRequestDto request);

    AdministrationConfigurationResponseDto getConfigurationById(
            Long id);

    Page<AdministrationConfigurationResponseDto> getAllConfigurations(

            int page,

            int size,

            String search,

            ConfigurationType configurationType,

            ConfigurationStatus status,

            String sortBy,

            String direction);

    AdministrationConfigurationResponseDto updateConfiguration(

            Long id,

            CreateAdministrationConfigurationRequestDto request);

    String deleteConfiguration(
            Long id);

    AdministrationDashboardResponseDto getDashboard();
    
    List<ConfigurationHistoryResponseDto> getConfigurationHistory(
            Long configurationId);
    
    List<VersionHistoryResponseDto> getVersionHistory(
            Long configurationId);
    
    List<AuditLogResponseDto> getAuditLogs(
            String module);
}