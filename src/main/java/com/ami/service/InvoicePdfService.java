package com.ami.service;

import com.ami.entity.Invoice;

public interface InvoicePdfService {

	byte[] generateInvoicePdf(Invoice invoice);
}