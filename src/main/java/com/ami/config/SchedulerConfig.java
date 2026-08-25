package com.ami.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Dedicated thread pool for all @Scheduled tasks in the application
 * (PayloadSimulatorScheduler, PrepaidLowBalanceScheduler,
 * DeviceHealthScheduler, InvoiceOverdueScheduler, AutomaticInvoiceScheduler,
 * etc.).
 *
 * Without this bean, Spring can fall back to reusing whatever TaskScheduler-
 * compatible bean already exists in the context — in this project that was the
 * WebSocket message broker's internal scheduler (visible as "MessageBroker-N"
 * threads in logs/stack traces). Sharing that pool is risky: it was provisioned
 * for WebSocket heartbeats/broadcast, not for our scheduled business logic, and
 * its thread count/behavior is not something we control or size intentionally.
 */
@Configuration
public class SchedulerConfig {

	@Bean
	public TaskScheduler taskScheduler() {

		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

		scheduler.setPoolSize(5);
		scheduler.setThreadNamePrefix("app-scheduler-");
		scheduler.setRemoveOnCancelPolicy(true);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setAwaitTerminationSeconds(20);

		return scheduler;
	}
}