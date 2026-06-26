package com.ami.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.ami.dto.requests.CreateInvoiceRequestDto;
import com.ami.dto.requests.UpdateInvoiceRequestDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.dto.responses.InvoiceSummaryResponseDto;
import com.ami.service.InvoiceService;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceService invoiceService) {

        this.invoiceService = invoiceService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public InvoiceResponseDto createInvoice(
            @RequestBody
            CreateInvoiceRequestDto request) {

        return invoiceService
                .createInvoice(request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping
    public List<InvoiceResponseDto>
    getAllInvoices() {

        return invoiceService
                .getAllInvoices();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/{id}")
    public InvoiceResponseDto getInvoiceById(
            @PathVariable Long id) {

        return invoiceService
                .getInvoiceById(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public InvoiceResponseDto updateInvoice(
            @PathVariable Long id,
            @RequestBody
            UpdateInvoiceRequestDto request) {

        return invoiceService
                .updateInvoice(
                        id,
                        request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteInvoice(
            @PathVariable Long id) {

        return invoiceService
                .deleteInvoice(id);
    }
    @GetMapping("/summary")
    public InvoiceSummaryResponseDto getSummary() {

        return invoiceService.getSummary();
    }
    @GetMapping("/paged")
    public Page<InvoiceResponseDto>
    getInvoicesWithPagination(

            @RequestParam(
                    defaultValue = "0")
            int page,

            @RequestParam(
                    defaultValue = "10")
            int limit) {

        return invoiceService
                .getInvoicesWithPagination(
                        page,
                        limit);
    }
    @GetMapping("/filter")
    public List<InvoiceResponseDto> getInvoices(

            @RequestParam(required = false)
            String customerName,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String paymentStatus,

            @RequestParam(required = false)
            String source,

            @RequestParam(required = false)
            String billingType) {

        return invoiceService.getInvoices(
                customerName,
                status,
                paymentStatus,
                source,
                billingType);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]>
    exportInvoices() {

        byte[] data =
                invoiceService.exportInvoices();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoices.csv")
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}