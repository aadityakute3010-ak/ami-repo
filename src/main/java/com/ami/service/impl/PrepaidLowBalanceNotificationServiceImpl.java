package com.ami.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;
import com.ami.repository.PrepaidBalanceRepository;
import com.ami.service.EmailService;
import com.ami.service.PrepaidLowBalanceNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrepaidLowBalanceNotificationServiceImpl implements PrepaidLowBalanceNotificationService {

	private static final BigDecimal LOW_THRESHOLD = new BigDecimal("0.15");
	private static final BigDecimal VERY_LOW_THRESHOLD = new BigDecimal("0.05");

	private final PrepaidBalanceRepository prepaidBalanceRepository;
	private final EmailService emailService;

	@Override
	@Transactional
	public void checkLowBalances() {
		List<PrepaidBalance> balances = prepaidBalanceRepository.findActivePrepaidBalancesForNotification();
		for (PrepaidBalance balance : balances) {
			try {
				checkBalance(balance);
			} catch (Exception e) {
				log.error("LOW BALANCE CHECK FAILED for balance id={}", balance.getId(), e);
			}
		}
	}

	private void checkBalance(PrepaidBalance balance) {

		BigDecimal totalCredited = zeroIfNull(balance.getTotalCreditedUnits());
		BigDecimal available = zeroIfNull(balance.getAvailableUnits());

		if (totalCredited.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		BigDecimal remainingRatio = available.divide(totalCredited, 6, RoundingMode.HALF_UP);

		// 5% or below
		if (remainingRatio.compareTo(VERY_LOW_THRESHOLD) <= 0) {

			if (!balance.isVeryLowBalanceNotificationSent()) {
				sendVeryLowBalanceNotification(balance);

				balance.setVeryLowBalanceNotificationSent(true);
				prepaidBalanceRepository.save(balance);
			}

			return;
		}

		// Above 5% and 15% or below
		if (remainingRatio.compareTo(LOW_THRESHOLD) <= 0) {

			if (!balance.isLowBalanceNotificationSent()) {
				sendLowBalanceNotification(balance);

				balance.setLowBalanceNotificationSent(true);
				prepaidBalanceRepository.save(balance);
			}
		}
	}

	private void sendLowBalanceNotification(PrepaidBalance balance) {

		Device device = balance.getDevice();

		if (device == null) {
			log.warn("Cannot send low balance notification: device is null for balance {}", balance.getId());
			return;
		}

		String recipientEmail = getCustomerEmail(device);

		if (recipientEmail == null || recipientEmail.isBlank()) {
			log.warn("Cannot send low balance notification: customer email is missing for balance {}", balance.getId());
			return;
		}

		BigDecimal totalCredited = zeroIfNull(balance.getTotalCreditedUnits());
		BigDecimal available = zeroIfNull(balance.getAvailableUnits());

		BigDecimal percentage = calculatePercentage(available, totalCredited);

		emailService.sendPrepaidLowBalanceEmail(recipientEmail, getCustomerName(device), getDeviceIdentifier(device),
				available, totalCredited, percentage);

		log.info("Low prepaid balance notification sent for balance {}", balance.getId());
	}

	private void sendVeryLowBalanceNotification(PrepaidBalance balance) {

		Device device = balance.getDevice();

		if (device == null) {
			log.warn("Cannot send very low balance notification: device is null for balance {}", balance.getId());
			return;
		}

		String recipientEmail = getCustomerEmail(device);

		if (recipientEmail == null || recipientEmail.isBlank()) {
			log.warn("Cannot send very low balance notification: customer email is missing for balance {}",
					balance.getId());
			return;
		}

		BigDecimal totalCredited = zeroIfNull(balance.getTotalCreditedUnits());
		BigDecimal available = zeroIfNull(balance.getAvailableUnits());

		BigDecimal percentage = calculatePercentage(available, totalCredited);

		emailService.sendPrepaidVeryLowBalanceEmail(recipientEmail, getCustomerName(device),
				getDeviceIdentifier(device), available, totalCredited, percentage);

		log.info("Very low prepaid balance notification sent for balance {}", balance.getId());
	}

	private BigDecimal calculatePercentage(BigDecimal available, BigDecimal totalCredited) {

		if (totalCredited.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		return available.divide(totalCredited, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2,
				RoundingMode.HALF_UP);
	}

	private String getCustomerEmail(Device device) {

		/*
		 * IMPORTANT: Replace this with the actual way your Device gets the customer's
		 * email.
		 */

		if (device.getAssignedUser() != null) {
			return device.getAssignedUser().getEmail();
		}

		return null;
	}

	private String getCustomerName(Device device) {

		if (device.getAssignedUser() != null) {
			return device.getAssignedUser().getFirstName();
		}

		return "Customer";
	}

	private String getDeviceIdentifier(Device device) {

		if (device.getDeviceId() != null) {
			return device.getDeviceId();
		}

		return "Unknown Device";
	}

	private BigDecimal zeroIfNull(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	@Override
	public void checkAndNotify(PrepaidBalance balance) {

		if (balance == null) {
			return;
		}

		BigDecimal totalCredited = balance.getTotalCreditedUnits();
		BigDecimal available = balance.getAvailableUnits();

		if (totalCredited == null || totalCredited.compareTo(BigDecimal.ZERO) <= 0 || available == null) {
			return;
		}

		BigDecimal remainingRatio = available.divide(totalCredited, 6, RoundingMode.HALF_UP);

		// 5% or below
		if (remainingRatio.compareTo(VERY_LOW_THRESHOLD) <= 0) {

			if (!balance.isVeryLowBalanceNotificationSent()) {

				sendVeryLowBalanceNotification(balance);

				balance.setVeryLowBalanceNotificationSent(true);
				prepaidBalanceRepository.save(balance);
			}

			// Above 5% and 15% or below
		} else if (remainingRatio.compareTo(LOW_THRESHOLD) <= 0) {

			if (!balance.isLowBalanceNotificationSent()) {

				sendLowBalanceNotification(balance);

				balance.setLowBalanceNotificationSent(true);
				prepaidBalanceRepository.save(balance);
			}
		}
	}
}