package com.ami.service;

public interface AutomaticInvoiceService {

    void generateMonthlyInvoices();

    void generateMissingMonthlyInvoices();
}