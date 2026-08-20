package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.Device;
import com.ami.entity.PrepaidUsageLedger;

public interface PrepaidUsageLedgerRepository extends JpaRepository<PrepaidUsageLedger, Long> {

	List<PrepaidUsageLedger> findByDeviceOrderByCreatedAtDesc(Device device);
}