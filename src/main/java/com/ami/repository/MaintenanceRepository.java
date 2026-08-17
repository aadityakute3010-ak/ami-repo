package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.Maintenance;
import com.ami.enums.MaintenanceStatus;
import com.ami.enums.MaintenanceType;

@Repository
public interface MaintenanceRepository
        extends JpaRepository<Maintenance, Long>,
                JpaSpecificationExecutor<Maintenance> {

    List<Maintenance> findByDeviceId(
            String deviceId);

    List<Maintenance> findByStatus(
            MaintenanceStatus status);

    List<Maintenance> findByMaintenanceType(
            MaintenanceType maintenanceType);

    long countByStatus(
            MaintenanceStatus status);

    long countByMaintenanceType(
            MaintenanceType maintenanceType);

    List<Maintenance> findBySource(
            com.ami.enums.MaintenanceSource source);

    List<Maintenance> findByPriority(
            com.ami.enums.MaintenancePriority priority);

    List<Maintenance> findByAssignedEngineerId(
            Long assignedEngineerId);
}