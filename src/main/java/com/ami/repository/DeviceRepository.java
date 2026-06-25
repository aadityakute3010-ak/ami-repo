package com.ami.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ami.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {

    boolean existsByMacAddress(String macAddress);

    boolean existsBySerialNumber(String serialNumber);

    List<Device> findByAssignedAdminId(Long adminId);

    List<Device> findByAssignedUserId(Long userId);
} 