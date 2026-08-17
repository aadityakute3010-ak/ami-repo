package com.ami.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.ami.dto.requests.CreateInvoiceRequestDto;
import com.ami.dto.requests.UpdateInvoiceRequestDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.dto.responses.InvoiceSummaryResponseDto;
import com.ami.entity.Invoice;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.repository.InvoiceRepository;
import com.ami.service.InvoiceService;

@Service
public class InvoiceServiceImpl
        implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository) {

        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public InvoiceResponseDto createInvoice(
            CreateInvoiceRequestDto request) {

        Double consumption =
                request.getCurrentReading()
                - request.getPreviousReading();

        Double amount = consumption * 10;

        Double netAmount =
                amount
                + request.getFixedCharge()
                + request.getTax()
                - request.getDiscount();

        Invoice invoice =
                Invoice.builder()
                        .invoiceNumber(
                                "INV-" +
                                System.currentTimeMillis())
                        .customerId(
                                request.getCustomerId())
                        .customerName(
                                request.getCustomerName())
                        .email(
                                request.getEmail())
                        .phone(
                                request.getPhone())
                        .meterNumber(
                                request.getMeterNumber())
                        .source(
                                request.getSource())
                        .billingType(
                                request.getBillingType())
                        .tariffId(
                                request.getTariffId())
                        .previousReading(
                                request.getPreviousReading())
                        .currentReading(
                                request.getCurrentReading())
                        .consumption(
                                consumption)
                        .amount(
                                amount)
                        .fixedCharge(
                                request.getFixedCharge())
                        .tax(
                                request.getTax())
                        .discount(
                                request.getDiscount())
                        .netAmount(
                                netAmount)
                        .status(
                                InvoiceStatus.GENERATED)
                        .paymentStatus(
                                PaymentStatus.PENDING)
                        .invoiceDate(
                                request.getInvoiceDate())
                        .dueDate(
                                request.getDueDate())
                        .billingPeriodFrom(
                                request.getBillingPeriodFrom())
                        .billingPeriodTo(
                                request.getBillingPeriodTo())
                        .remarks(
                                request.getRemarks())
                        .paidAmount(0.0)
                        .balanceAmount(
                                netAmount)
                        .createdAt(
                                LocalDateTime.now())
                        .updatedAt(
                                LocalDateTime.now())
                        .history(
                        	    "[" + LocalDateTime.now() + "] Invoice Created"
                        	)
                        .build();

        invoice =
                invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    @Override
    public List<InvoiceResponseDto>
    getAllInvoices() {

        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceResponseDto
    getInvoiceById(Long id) {

        Invoice invoice =
                invoiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"));

        return mapToResponse(invoice);
    }

    @Override
    public InvoiceResponseDto updateInvoice(
            Long id,
            UpdateInvoiceRequestDto request) {

        Invoice invoice =
                invoiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"));

        invoice.setStatus(
                request.getStatus());

        
        
        invoice.setHistory(
                invoice.getHistory()
                + "\n[" + LocalDateTime.now()
                + "] Status Changed To "
                + request.getStatus()
        );

        invoice.setPaidAmount(
                request.getPaidAmount());
        
        
        Double paidAmount =
                request.getPaidAmount() == null
                ? 0.0
                : request.getPaidAmount();

        invoice.setPaidAmount(paidAmount);

        invoice.setBalanceAmount(
                invoice.getNetAmount() - paidAmount);
        
        if(invoice.getBalanceAmount() <= 0) {

            invoice.setPaymentStatus(
                    PaymentStatus.PAID);

        } else if(invoice.getPaidAmount() > 0) {

            invoice.setPaymentStatus(
                    PaymentStatus.PARTIAL);

        } else {

            invoice.setPaymentStatus(
                    PaymentStatus.PENDING);
        }

        invoice.setRemarks(
                request.getRemarks());

        invoice.setDueDate(
                request.getDueDate());

        invoice.setUpdatedAt(
                LocalDateTime.now());
        
        String history =
                invoice.getHistory() == null
                ? ""
                : invoice.getHistory();

        invoice.setHistory(
                history
                + "\n[" + LocalDateTime.now()
                + "] Invoice Updated");
        invoice =
                invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    @Override
    public String deleteInvoice(
            Long id) {

        Invoice invoice =
                invoiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"));
       

        invoiceRepository.delete(invoice);

        return "Invoice deleted successfully";
    }

    private InvoiceResponseDto
    mapToResponse(
            Invoice invoice) {

        return InvoiceResponseDto
                .builder()
                .id(invoice.getId())
                .invoiceNumber(
                        invoice.getInvoiceNumber())
                .customerId(
                        invoice.getCustomerId())
                .customerName(
                        invoice.getCustomerName())
                .meterNumber(
                        invoice.getMeterNumber())
                .source(
                        invoice.getSource())
                .billingType(
                        invoice.getBillingType())
                .netAmount(
                        invoice.getNetAmount())
                .status(
                        invoice.getStatus())
                .paymentStatus(
                        invoice.getPaymentStatus())
                .invoiceDate(
                        invoice.getInvoiceDate())
                .dueDate(
                        invoice.getDueDate())
                .paidAmount(
                        invoice.getPaidAmount())
                .balanceAmount(
                        invoice.getBalanceAmount())
                .history(
                        invoice.getHistory())
                .build();
    }
    @Override
    public InvoiceSummaryResponseDto getSummary() {

        List<Invoice> invoices =
                invoiceRepository.findAll();

        long totalInvoices =
                invoices.size();

        long paidInvoices =
                invoices.stream()
                        .filter(invoice ->
                                invoice.getPaymentStatus()
                                        == PaymentStatus.PAID)
                        .count();

        long pendingInvoices =
                invoices.stream()
                        .filter(invoice ->
                                invoice.getPaymentStatus()
                                        == PaymentStatus.PENDING)
                        .count();

        long overdueInvoices =
                invoices.stream()
                        .filter(invoice ->
                                invoice.getStatus()
                                        == InvoiceStatus.OVERDUE)
                        .count();

        double totalRevenue =
                invoices.stream()
                        .filter(invoice ->
                                invoice.getPaymentStatus()
                                        == PaymentStatus.PAID)
                        .mapToDouble(
                                Invoice::getPaidAmount)
                        .sum();

        return InvoiceSummaryResponseDto
                .builder()
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .pendingInvoices(pendingInvoices)
                .overdueInvoices(overdueInvoices)
                .totalRevenue(totalRevenue)
                .build();
    }
    @Override
    public Page<InvoiceResponseDto>
    getInvoicesWithPagination(
            int page,
            int limit) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        limit);

        return invoiceRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }
    @Override
    public List<InvoiceResponseDto> getInvoices(
            String customerName,
            String status,
            String paymentStatus,
            String source,
            String billingType) {

        return invoiceRepository.findAll()
                .stream()

                .filter(invoice ->
                        customerName == null ||
                        invoice.getCustomerName()
                                .toLowerCase()
                                .contains(
                                        customerName.toLowerCase()))

                .filter(invoice ->
                        status == null ||
                        invoice.getStatus()
                                .name()
                                .equalsIgnoreCase(status))

                .filter(invoice ->
                        paymentStatus == null ||
                        invoice.getPaymentStatus()
                                .name()
                                .equalsIgnoreCase(paymentStatus))

                .filter(invoice ->
                        source == null ||
                        invoice.getSource()
                                .name()
                                .equalsIgnoreCase(source))

                .filter(invoice ->
                        billingType == null ||
                        invoice.getBillingType()
                                .name()
                                .equalsIgnoreCase(billingType))

                .map(this::mapToResponse)

                .toList();
    }
    @Override
    public byte[] exportInvoices() {

        List<Invoice> invoices =
                invoiceRepository.findAll();

        StringBuilder csv =
                new StringBuilder();

        csv.append(
                "Invoice Number,Customer Name,Amount,Status,Payment Status\n");

        for (Invoice invoice : invoices) {

            csv.append(invoice.getInvoiceNumber())
                    .append(",");

            csv.append(invoice.getCustomerName())
                    .append(",");

            csv.append(invoice.getNetAmount())
                    .append(",");

            csv.append(invoice.getStatus())
                    .append(",");

            csv.append(invoice.getPaymentStatus())
                    .append("\n");
        }

        return csv.toString()
                .getBytes(StandardCharsets.UTF_8);
    }
}