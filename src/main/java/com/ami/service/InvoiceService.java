package com.ami.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CreateInvoiceRequestDto;
import com.ami.dto.requests.UpdateInvoiceRequestDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.dto.responses.InvoiceSummaryResponseDto;

public interface InvoiceService {

    InvoiceResponseDto createInvoice(
            CreateInvoiceRequestDto request);

    List<InvoiceResponseDto> getAllInvoices();

    InvoiceResponseDto getInvoiceById(
            Long id);

    InvoiceResponseDto updateInvoice(
            Long id,
            UpdateInvoiceRequestDto request);

    String deleteInvoice(Long id);
    
    InvoiceSummaryResponseDto getSummary();
    
    Page<InvoiceResponseDto> getInvoicesWithPagination(
            int page,
            int limit);
    
    List<InvoiceResponseDto> getInvoices(
            String customerName,
            String status,
            String paymentStatus,
            String source,
            String billingType);
    
    byte[] exportInvoices();
    
}