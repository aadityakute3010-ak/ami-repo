package com.ami.config;

import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ami.service.AutomaticInvoiceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutomaticInvoiceStartupRunner implements ApplicationRunner {

	private final AutomaticInvoiceService automaticInvoiceService;

	@Override
	public void run(ApplicationArguments args) {

		LocalDate today = LocalDate.now();

		int dayOfMonth = today.getDayOfMonth();

		if (dayOfMonth >= 1 && dayOfMonth <= 3) {
			automaticInvoiceService.generateMissingMonthlyInvoices();
		}
	}
}