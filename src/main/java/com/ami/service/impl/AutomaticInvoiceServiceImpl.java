package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.GenerateInvoiceRequestDto;
import com.ami.entity.Device;
import com.ami.entity.Invoice;
import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.BillingType;
import com.ami.enums.InvoiceGenerationType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.mapper.TariffCategoryResolver;
import com.ami.repository.DeviceRepository;
import com.ami.repository.InvoiceRepository;
import com.ami.repository.TariffRepository;
import com.ami.service.AutomaticInvoiceService;
import com.ami.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomaticInvoiceServiceImpl implements AutomaticInvoiceService {

	private final DeviceRepository deviceRepository;

	private final TariffRepository tariffRepository;

	private final InvoiceRepository invoiceRepository;

	private final InvoiceService invoiceService;

	private final TariffCategoryResolver tariffCategoryResolver;

	@Override
	@Transactional
	public void generateMonthlyInvoices() {

		LocalDate today = LocalDate.now();

		LocalDate billingPeriodFrom = today.minusMonths(1).withDayOfMonth(1);

		LocalDate billingPeriodTo = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());

		List<Device> postpaidDevices = deviceRepository.findActiveDevicesByBillingType(BillingType.POSTPAID);

		for (Device device : postpaidDevices) {

			try {

				generateInvoiceForDevice(device, billingPeriodFrom, billingPeriodTo);

			} catch (Exception exception) {

				saveFailedInvoice(device, billingPeriodFrom, billingPeriodTo, exception.getMessage());
			}
		}
	}

	private void generateInvoiceForDevice(Device device, LocalDate billingPeriodFrom, LocalDate billingPeriodTo) {

		boolean successfulInvoiceAlreadyExists = invoiceRepository
				.existsByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatusNot(device, billingPeriodFrom,
						billingPeriodTo, InvoiceStatus.FAILED);

		if (successfulInvoiceAlreadyExists) {
			return;
		}

		if (device.getAssignedAdmin() == null) {
			throw new IllegalStateException("Device has no assigned admin");
		}

		if (device.getAssignedUser() == null) {
			throw new IllegalStateException("Device has no assigned user");
		}

		SourceType sourceType = resolveSourceType(device);

		TariffCategory tariffCategory = tariffCategoryResolver
				.resolveFromApplication(device.getMeter().getApplication());

		Tariff tariff = tariffRepository
				.findFirstByCreatedByAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(device.getAssignedAdmin(),
						sourceType, tariffCategory, TariffStatus.ACTIVE)
				.orElseGet(() -> tariffRepository
						.findFirstByCreatedBy_RoleAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(
								RoleType.SUPER_ADMIN, sourceType, tariffCategory, TariffStatus.ACTIVE)
						.orElseThrow(() -> new IllegalStateException("No active tariff found for admin "
								+ device.getAssignedAdmin().getId() + " or Super Admin, source " + sourceType
								+ " and category " + tariffCategory)));

		GenerateInvoiceRequestDto request = new GenerateInvoiceRequestDto();

		request.setDeviceId(device.getId());
		request.setTariffId(tariff.getId());
		request.setBillingPeriodFrom(billingPeriodFrom.toString());
		request.setBillingPeriodTo(billingPeriodTo.toString());
		request.setDiscount(BigDecimal.ZERO);
		request.setRemarks("Automatically generated monthly invoice");

		invoiceService.generateInvoice(request, InvoiceGenerationType.AUTO);
	}

	private void saveFailedInvoice(Device device, LocalDate billingPeriodFrom, LocalDate billingPeriodTo,
			String failureReason) {

		boolean successfulInvoiceAlreadyExists = invoiceRepository
				.existsByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatusNot(device, billingPeriodFrom,
						billingPeriodTo, InvoiceStatus.FAILED);

		if (successfulInvoiceAlreadyExists) {
			return;
		}

		User assignedUser = device.getAssignedUser();

		Invoice failedInvoice = invoiceRepository
				.findByDeviceAndBillingPeriodFromAndBillingPeriodTo(device, billingPeriodFrom, billingPeriodTo)
				.orElseGet(() -> Invoice.builder().invoiceNumber(generateFailedInvoiceNumber()).device(device)
						.billingPeriodFrom(billingPeriodFrom).billingPeriodTo(billingPeriodTo).build());

		failedInvoice.setTariff(null);
		failedInvoice.setCustomer(assignedUser);
		failedInvoice.setCustomerName(resolveCustomerName(device, assignedUser));
		failedInvoice.setEmail(assignedUser != null ? assignedUser.getEmail() : null);
		failedInvoice.setPhone(assignedUser != null ? assignedUser.getPhoneNo() : null);
		failedInvoice.setMeterNumber(device.getDeviceId());
		failedInvoice.setSource(resolveSourceTypeSafely(device));
		failedInvoice.setBillingType(device.getBillingType());

		failedInvoice.setPreviousReading(BigDecimal.ZERO);
		failedInvoice.setCurrentReading(BigDecimal.ZERO);
		failedInvoice.setConsumption(BigDecimal.ZERO);
		failedInvoice.setAmount(BigDecimal.ZERO);
		failedInvoice.setFixedCharge(BigDecimal.ZERO);
		failedInvoice.setTax(BigDecimal.ZERO);
		failedInvoice.setDiscount(BigDecimal.ZERO);
		failedInvoice.setPreviousDues(BigDecimal.ZERO);
		failedInvoice.setNetAmount(BigDecimal.ZERO);
		failedInvoice.setPaidAmount(BigDecimal.ZERO);
		failedInvoice.setBalanceAmount(BigDecimal.ZERO);

		failedInvoice.setPenaltyAmount(BigDecimal.ZERO);
		failedInvoice.setPenaltyApplied(false);
		failedInvoice.setPenaltyEnabledSnapshot(false);
		failedInvoice.setPenaltyPercentageSnapshot(BigDecimal.ZERO);

		failedInvoice.setStatus(InvoiceStatus.FAILED);
		failedInvoice.setPaymentStatus(PaymentStatus.PENDING);
		failedInvoice.setInvoiceDate(LocalDate.now());
		failedInvoice.setDueDate(LocalDate.now());
		failedInvoice.setRemarks("Automatic invoice generation failed");
		failedInvoice.setGenerationType(InvoiceGenerationType.AUTO);
		failedInvoice.setGeneratedBy(null);
		failedInvoice.setFailureReason(normalizeFailureReason(failureReason));
		failedInvoice.setUpdatedAt(LocalDateTime.now());

		if (failedInvoice.getCreatedAt() == null) {
			failedInvoice.setCreatedAt(LocalDateTime.now());
		}

		invoiceRepository.save(failedInvoice);
	}

	private SourceType resolveSourceType(Device device) {

		SourceType sourceType = resolveSourceTypeSafely(device);

		if (sourceType == null) {
			throw new IllegalStateException("Device source type not found");
		}

		return sourceType;
	}

	private SourceType resolveSourceTypeSafely(Device device) {

		if (device.getMeter() != null && device.getMeter().getSourceType() != null) {
			return device.getMeter().getSourceType();
		}

		return null;
	}

	private String resolveCustomerName(Device device, User assignedUser) {

		if (assignedUser != null) {

			String fullName = ((assignedUser.getFirstName() != null ? assignedUser.getFirstName() : "") + " "
					+ (assignedUser.getLastName() != null ? assignedUser.getLastName() : "")).trim();

			if (!fullName.isBlank()) {
				return fullName;
			}
		}

		return device.getCustomerName();
	}

	private String generateFailedInvoiceNumber() {

		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		String uniquePart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

		return "FAILED-" + datePart + "-" + uniquePart;
	}

	private String normalizeFailureReason(String failureReason) {

		if (failureReason == null || failureReason.isBlank()) {
			return "Automatic invoice generation failed";
		}

		return failureReason.length() > 500 ? failureReason.substring(0, 500) : failureReason;
	}

	@Override
	@Transactional
	public void generateMissingMonthlyInvoices() {

		LocalDate today = LocalDate.now();

		LocalDate billingPeriodFrom = today.minusMonths(1).withDayOfMonth(1);

		LocalDate previousMonth = today.minusMonths(1);

		LocalDate billingPeriodTo = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());

		List<Device> postpaidDevices = deviceRepository.findActiveDevicesByBillingType(BillingType.POSTPAID);

		for (Device device : postpaidDevices) {

			boolean invoiceAlreadyExists = invoiceRepository
					.existsByDeviceAndBillingPeriodFromAndBillingPeriodTo(device, billingPeriodFrom, billingPeriodTo);

			if (invoiceAlreadyExists) {
				continue;
			}

			try {

				generateInvoiceForDevice(device, billingPeriodFrom, billingPeriodTo);

			} catch (Exception exception) {

				saveFailedInvoice(device, billingPeriodFrom, billingPeriodTo, exception.getMessage());
			}
		}
	}
}