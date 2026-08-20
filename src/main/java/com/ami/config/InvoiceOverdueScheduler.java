package com.ami.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ami.service.InvoiceOverdueService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceOverdueScheduler {

    private final InvoiceOverdueService invoiceOverdueService;

    @Scheduled(cron = "0 0 1 * * *")
    public void updateOverdueInvoicesDaily() {
        invoiceOverdueService.updateOverdueInvoices();
    }
}