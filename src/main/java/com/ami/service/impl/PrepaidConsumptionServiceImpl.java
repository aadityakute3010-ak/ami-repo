package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;
import com.ami.entity.PrepaidUsageLedger;
import com.ami.enums.BillingType;
import com.ami.enums.PrepaidBalanceStatus;
import com.ami.enums.PrepaidLedgerType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.PrepaidBalanceRepository;
import com.ami.repository.PrepaidUsageLedgerRepository;
import com.ami.service.PrepaidConsumptionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrepaidConsumptionServiceImpl implements PrepaidConsumptionService {

	private final PrepaidBalanceRepository prepaidBalanceRepository;

	private final PrepaidUsageLedgerRepository prepaidUsageLedgerRepository;

	@PersistenceContext
	private EntityManager entityManager;

//	@Override
//	@Transactional
//	public void deductConsumption(Device device, BigDecimal startReading, BigDecimal endReading) {
//
//		if (device == null) {
//			throw new IllegalArgumentException("Device is required for prepaid consumption deduction");
//		}
//
//		if (device.getBillingType() != BillingType.PREPAID) {
//			return;
//		}
//
//		PrepaidBalance balance = prepaidBalanceRepository.findByDeviceForUpdate(device).orElseThrow(
//				() -> new ResourceNotFoundException("Prepaid balance not found for device id: " + device.getId()));
//
//		if (balance.isConsumptionBlocked()) {
//			throw new IllegalStateException("Prepaid consumption is blocked. Please recharge the device.");
//		}
//
//		validateReading(startReading, "Start meter reading");
//		validateReading(endReading, "End meter reading");
//
//		if (endReading.compareTo(startReading) < 0) {
//			throw new IllegalArgumentException("End meter reading cannot be less than start meter reading");
//		}
//
//		BigDecimal previousReading = balance.getLastMeterReading();
//
//		/*
//		 * First telemetry received after prepaid balance creation.
//		 *
//		 * We use the telemetry's startReading as the baseline, then deduct the
//		 * consumption between startReading and endReading.
//		 */
//		if (previousReading == null) {
//			previousReading = startReading;
//		}
//
//		/*
//		 * Prevent the device from sending telemetry that goes backwards relative to the
//		 * prepaid meter state.
//		 */
//		if (startReading.compareTo(previousReading) < 0) {
//			throw new IllegalArgumentException(
//					"Start meter reading cannot be less than previous prepaid meter reading");
//		}
//
//		BigDecimal consumedUnits = endReading.subtract(startReading);
//
//		if (consumedUnits.compareTo(BigDecimal.ZERO) < 0) {
//			throw new IllegalArgumentException("Consumption cannot be negative");
//		}
//
//		BigDecimal balanceBefore = zeroIfNull(balance.getAvailableUnits());
//
//		if (consumedUnits.compareTo(balanceBefore) > 0) {
//			throw new IllegalStateException("Insufficient prepaid balance for device " + device.getDeviceId()
//					+ ". Available units: " + balanceBefore + ", requested consumption: " + consumedUnits);
//		}
//
//		BigDecimal balanceAfter = balanceBefore.subtract(consumedUnits);
//
//		boolean exhausted = balanceAfter.compareTo(BigDecimal.ZERO) <= 0;
//
//		if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
//			balanceAfter = BigDecimal.ZERO;
//		}
//
//		BigDecimal currentUsedUnits = zeroIfNull(balance.getTotalUsedUnits());
//
//		balance.setTotalUsedUnits(currentUsedUnits.add(consumedUnits));
//
//		balance.setAvailableUnits(balanceAfter);
//
//		balance.setLastMeterReading(endReading);
//
//		balance.setLastConsumptionAt(LocalDateTime.now());
//
//		balance.setStatus(resolveBalanceStatus(balanceAfter));
//
//		balance.setUpdatedAt(LocalDateTime.now());
//
//		if (exhausted) {
//			balance.setConsumptionBlocked(true);
//		}
//
//		PrepaidBalance savedBalance = prepaidBalanceRepository.save(balance);
//
//		System.out.println("BALANCE AFTER SAVE => available=" + savedBalance.getAvailableUnits() + ", used="
//				+ savedBalance.getTotalUsedUnits() + ", reading=" + savedBalance.getLastMeterReading());
//
//		createConsumptionLedger(savedBalance, device, consumedUnits, startReading, endReading, balanceBefore,
//				balanceAfter);
//	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deductConsumption(Device device, BigDecimal startReading, BigDecimal endReading) {

		if (device == null) {
			throw new IllegalArgumentException("Device is required");
		}

		System.err.println("######## PREPAID deductConsumption() ENTERED ########");

		System.err.println("CLASS = " + this.getClass().getName());

		System.err.println("DEVICE = " + device.getDeviceId());

		System.err.println("START = " + startReading + ", END = " + endReading);

		if (device.getBillingType() != BillingType.PREPAID) {
			return;
		}

		validateReading(startReading, "Start meter reading");
		validateReading(endReading, "End meter reading");

		if (endReading.compareTo(startReading) < 0) {
			throw new IllegalArgumentException("End meter reading cannot be less than start meter reading");
		}

		PrepaidBalance balance = prepaidBalanceRepository.findByDeviceForUpdate(device).orElseThrow(
				() -> new ResourceNotFoundException("Prepaid balance not found for device id: " + device.getId()));

		if (balance.isConsumptionBlocked()) {
			throw new IllegalStateException("Prepaid consumption is blocked. Please recharge the device.");
		}

		BigDecimal previousReading = balance.getLastMeterReading();

		/*
		 * First telemetry after recharge/balance creation. Use the payload start
		 * reading as the previous reading.
		 */
		if (previousReading != null && startReading.compareTo(previousReading) < 0) {

			throw new IllegalArgumentException(
					"Start meter reading cannot be less than previous prepaid meter reading");
		}

		BigDecimal consumedUnits = endReading.subtract(startReading);

		BigDecimal balanceBefore = zeroIfNull(balance.getAvailableUnits());

		if (consumedUnits.compareTo(balanceBefore) > 0) {

			throw new IllegalStateException("Insufficient prepaid balance for device " + device.getDeviceId()
					+ ". Available units: " + balanceBefore + ", requested consumption: " + consumedUnits);
		}

		BigDecimal balanceAfter = balanceBefore.subtract(consumedUnits);

		BigDecimal totalUsed = zeroIfNull(balance.getTotalUsedUnits());

		balance.setTotalUsedUnits(totalUsed.add(consumedUnits));

		balance.setAvailableUnits(balanceAfter);

		balance.setLastMeterReading(endReading);

		balance.setLastConsumptionAt(LocalDateTime.now());

		balance.setStatus(resolveBalanceStatus(balanceAfter));

		if (balanceAfter.compareTo(BigDecimal.ZERO) <= 0) {
			balance.setConsumptionBlocked(true);
		}

		balance.setUpdatedAt(LocalDateTime.now());

		System.err.println("######## ABOUT TO CALL createConsumptionLedger ########");

		PrepaidBalance savedBalance = prepaidBalanceRepository.save(balance);

		createConsumptionLedger(savedBalance, device, consumedUnits, startReading, endReading, balanceBefore,
				balanceAfter); 

		System.err.println("######## LEDGER METHOD RETURNED ########");

		entityManager.flush();
	}

	private void validateReading(BigDecimal reading, String fieldName) {

		if (reading == null) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		if (reading.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(fieldName + " cannot be negative");
		}
	}

	private void createConsumptionLedger(PrepaidBalance balance, Device device, BigDecimal consumedUnits,
			BigDecimal previousReading, BigDecimal currentMeterReading, BigDecimal balanceBefore,
			BigDecimal balanceAfter) { 

		PrepaidUsageLedger ledger = PrepaidUsageLedger.builder().prepaidBalance(balance).device(device)
				.ledgerType(PrepaidLedgerType.CONSUMPTION_DEBIT).units(consumedUnits).readingBefore(previousReading)
				.readingAfter(currentMeterReading).balanceBefore(balanceBefore).balanceAfter(balanceAfter)
				.description("Prepaid consumption deducted from payload").build();

		ledger.setCreatedAt(LocalDateTime.now());
		ledger.setUpdatedAt(LocalDateTime.now());

		prepaidUsageLedgerRepository.save(ledger);
	}

	private PrepaidBalanceStatus resolveBalanceStatus(BigDecimal availableUnits) {

		if (availableUnits.compareTo(BigDecimal.ZERO) <= 0) {
			return PrepaidBalanceStatus.EXHAUSTED;
		}

		return PrepaidBalanceStatus.ACTIVE;
	}

	private BigDecimal zeroIfNull(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value;
	}
}