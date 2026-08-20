package com.ami.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ami.service.AutomaticInvoiceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutomaticInvoiceScheduler {

    private final AutomaticInvoiceService automaticInvoiceService;

    @Scheduled(cron = "0 0 2 1 * *")
    public void generateMonthlyInvoices() {
        automaticInvoiceService.generateMonthlyInvoices();
    }
}