package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Device;
import com.ami.entity.DeviceAudit;

@Repository
public interface DeviceAuditRepository extends JpaRepository<DeviceAudit, Long> {

	List<DeviceAudit> findByDeviceOrderByActionTimeDesc(Device device);

}
