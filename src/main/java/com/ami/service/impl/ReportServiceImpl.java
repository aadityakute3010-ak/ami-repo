//package com.ami.service.impl;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.ami.dto.responses.ReportSummaryResponseDto;
//import com.ami.entity.Invoice;
//import com.ami.entity.Payment;
//import com.ami.entity.Recharge;
//import com.ami.enums.InvoiceStatus;
//import com.ami.enums.PaymentStatus;
//import com.ami.repository.InvoiceRepository;
//import com.ami.repository.PaymentRepository;
//import com.ami.repository.RechargeRepository;
//import com.ami.service.ReportService;
//
//
//@Service
//public class ReportServiceImpl     implements ReportService {
//	
//	private final InvoiceRepository invoiceRepository;
//
//	private final PaymentRepository paymentRepository;
//
//	private final RechargeRepository rechargeRepository;
//	
//	public ReportServiceImpl(
//	        InvoiceRepository invoiceRepository,
//	        PaymentRepository paymentRepository,
//	        RechargeRepository rechargeRepository) {
//
//	    this.invoiceRepository = invoiceRepository;
//	    this.paymentRepository = paymentRepository;
//	    this.rechargeRepository = rechargeRepository;
//	}
//	@Override
//	public ReportSummaryResponseDto getRevenueReport() {
//
//	    double revenue =
//	            invoiceRepository.findAll()
//	                    .stream()
//	                    .mapToDouble(
//	                            Invoice::getNetAmount)
//	                    .sum();
//
//	    return ReportSummaryResponseDto
//	            .builder()
//	            .totalRevenue(revenue)
//	            .build();
//	}
//	@Override
//	public ReportSummaryResponseDto getCollectionReport() {
//
//	    double collection =
//	            paymentRepository.findAll()
//	                    .stream()
//	                    .mapToDouble(
//	                            Payment::getAmount)
//	                    .sum();
//
//	    return ReportSummaryResponseDto
//	            .builder()
//	            .totalCollection(collection)
//	            .build();
//	}
//	@Override
//	public ReportSummaryResponseDto getPendingReport() {
//
//		double pending =
//		        invoiceRepository.findAll()
//		                .stream()
//		                .filter(invoice ->
//		                        invoice.getPaymentStatus()
//		                                != PaymentStatus.PAID)
//		                .mapToDouble(
//		                        Invoice::getBalanceAmount)
//		                .sum();
//	    return ReportSummaryResponseDto
//	            .builder()
//	            .totalPending(pending)
//	            .build();
//	}
//	@Override
//	public ReportSummaryResponseDto getOverdueReport() {
//
//	    double overdue =
//	            invoiceRepository.findAll()
//	                    .stream()
//	                    .filter(invoice ->
//	                            invoice.getStatus()
//	                                    == InvoiceStatus.OVERDUE)
//	                    .mapToDouble(
//	                            Invoice::getBalanceAmount)
//	                    .sum();
//
//	    return ReportSummaryResponseDto
//	            .builder()
//	            .totalOverdue(overdue)
//	            .build();
//	}
//	@Override
//	public ReportSummaryResponseDto getRechargeReport() {
//
//	    double recharge =
//	            rechargeRepository.findAll()
//	                    .stream()
//	                    .mapToDouble(
//	                            Recharge::getAmount)
//	                    .sum();
//
//	    return ReportSummaryResponseDto
//	            .builder()
//	            .totalRecharge(recharge)
//	            .build();
//	}
//	@Override
//	public byte[] exportRevenueReport() {
//
//	    StringBuilder csv =
//	            new StringBuilder();
//
//	    csv.append(
//	            "Invoice Number,Customer,Amount\n");
//
//	    for (Invoice invoice :
//	            invoiceRepository.findAll()) {
//
//	        csv.append(
//	                invoice.getInvoiceNumber())
//	                .append(",");
//
//	        csv.append(
//	                invoice.getCustomerName())
//	                .append(",");
//
//	        csv.append(
//	                invoice.getNetAmount())
//	                .append("\n");
//	    }
//
//	    return csv.toString()
//	            .getBytes(
//	                    StandardCharsets.UTF_8);
//	}
//	@Override
//	public byte[] exportCollectionReport() {
//
//	    return exportRevenueReport();
//	}
//
//	@Override
//	public byte[] exportPendingReport() {
//
//	    return exportRevenueReport();
//	}
//
//	@Override
//	public byte[] exportOverdueReport() {
//
//	    return exportRevenueReport();
//	}
//}
