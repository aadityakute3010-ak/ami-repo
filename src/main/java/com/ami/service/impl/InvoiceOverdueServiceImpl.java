package com.ami.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ami.entity.Invoice;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.repository.InvoiceRepository;
import com.ami.service.InvoiceOverdueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceOverdueServiceImpl implements InvoiceOverdueService {

	private final InvoiceRepository invoiceRepository;

	@Override
	@Transactional
	public void updateOverdueInvoices() {

		LocalDate today = LocalDate.now();

		List<Invoice> invoices = invoiceRepository
				.findByDueDateBeforeAndPaymentStatusNotAndBalanceAmountGreaterThanAndStatusNotIn(today,
						PaymentStatus.PAID, BigDecimal.ZERO,
						List.of(InvoiceStatus.DRAFT, InvoiceStatus.FAILED, InvoiceStatus.OVERDUE));

		for (Invoice invoice : invoices) {

			int gracePeriodDays = invoice.getGracePeriodDaysSnapshot() == null ? 0
					: invoice.getGracePeriodDaysSnapshot();

			LocalDate overdueDate = invoice.getDueDate().plusDays(gracePeriodDays);

			if (today.isAfter(overdueDate)) {

				invoice.setStatus(InvoiceStatus.OVERDUE);

				boolean penaltyNotApplied = invoice.getPenaltyApplied() == null || !invoice.getPenaltyApplied();

				boolean penaltyEnabled = invoice.getPenaltyEnabledSnapshot() != null
						&& invoice.getPenaltyEnabledSnapshot();

				BigDecimal penaltyPercentage = invoice.getPenaltyPercentageSnapshot() == null ? BigDecimal.ZERO
						: invoice.getPenaltyPercentageSnapshot();

				if (penaltyEnabled && penaltyNotApplied && penaltyPercentage.compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal penaltyAmount = invoice.getBalanceAmount().multiply(penaltyPercentage)
							.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

					invoice.setPenaltyAmount(penaltyAmount);
					invoice.setPenaltyApplied(true);

					invoice.setNetAmount(invoice.getNetAmount().add(penaltyAmount));
					invoice.setBalanceAmount(invoice.getBalanceAmount().add(penaltyAmount));
				}
			}
		}

		invoiceRepository.saveAll(invoices);
	}
}