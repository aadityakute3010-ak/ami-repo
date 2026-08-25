package com.ami.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;

import jakarta.persistence.LockModeType;

public interface PrepaidBalanceRepository extends JpaRepository<PrepaidBalance, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM PrepaidBalance p WHERE p.device = :device")
	Optional<PrepaidBalance> findByDeviceForUpdate(@Param("device") Device device);

	boolean existsByDevice(Device device);

	Optional<PrepaidBalance> findByDevice(Device device);

	@Query("""
			SELECT pb
			FROM PrepaidBalance pb
			JOIN FETCH pb.device d
			JOIN FETCH pb.user u
			WHERE d.billingType = com.ami.enums.BillingType.PREPAID
			  AND pb.status IN (com.ami.enums.PrepaidBalanceStatus.ACTIVE, com.ami.enums.PrepaidBalanceStatus.EXHAUSTED)
			  AND pb.availableUnits IS NOT NULL
			  AND pb.totalCreditedUnits IS NOT NULL
			""")
	List<PrepaidBalance> findActivePrepaidBalancesForNotification();
}