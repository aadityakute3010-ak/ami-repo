package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.AdministrationConfiguration;
import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;

@Repository
public interface AdministrationConfigurationRepository
        extends JpaRepository<AdministrationConfiguration, Long>,
        JpaSpecificationExecutor<AdministrationConfiguration> {

    List<AdministrationConfiguration> findByConfigurationType(
            ConfigurationType configurationType);

    List<AdministrationConfiguration> findByStatus(
            ConfigurationStatus status);

    long countByConfigurationType(
            ConfigurationType configurationType);

    long countByStatus(
            ConfigurationStatus status);
}