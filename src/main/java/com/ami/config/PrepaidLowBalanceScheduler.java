package com.ami.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ami.service.PrepaidLowBalanceNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrepaidLowBalanceScheduler {

	private final PrepaidLowBalanceNotificationService notificationService;

	/**
	 * Checks active prepaid balances periodically.
	 *
	 * Runs on startup and every 60 seconds while the application is running.
	 */
	@Scheduled(fixedRate = 60000)
	public void checkPrepaidBalances() {

		try {

			notificationService.checkLowBalances();

		} catch (Exception e) {

			log.error("LOW BALANCE CHECK FAILED", e);
		}
	}
}