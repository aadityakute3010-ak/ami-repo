package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.GenerateInvoiceRequestDto;
import com.ami.dto.requests.PayloadBillCalculationRequest;
import com.ami.dto.responses.BillCalculationResponseDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.entity.Device;
import com.ami.entity.Invoice;
import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.InvoiceGenerationType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.TariffCategoryResolver;
import com.ami.repository.DeviceRepository;
import com.ami.repository.InvoiceRepository;
import com.ami.repository.TariffRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.BillingCalculatorService;
import com.ami.service.EmailService;
import com.ami.service.InvoiceOverdueService;
import com.ami.service.InvoicePdfService;
import com.ami.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ami.dto.responses.PagedInvoiceResponseDto;
import com.ami.enums.BillingType;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.entity.BillingSettings;
import com.ami.service.BillingSettingsService;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;

	private final DeviceRepository deviceRepository;

	private final TariffRepository tariffRepository;

	private final BillingCalculatorService billingCalculatorService;

	private final SecurityUtils securityUtils;

	private final InvoicePdfService invoicePdfService;

	private final EmailService emailService;

	private final BillingSettingsService billingSettingsService;

	private final InvoiceOverdueService invoiceOverdueService;

	private final TariffCategoryResolver tariffCategoryResolver;

	@Override
	@Transactional
	public InvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto request, InvoiceGenerationType generationType) {

		Device device = deviceRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + request.getDeviceId()));

		Tariff tariff = tariffRepository.findById(request.getTariffId())
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + request.getTariffId()));

		validateTariffMatchesDevice(device, tariff, generationType);

		BillingSettings settings = billingSettingsService.getSettingsForAdmin(device.getAssignedAdmin());

		LocalDate invoiceDate = LocalDate.now();
		LocalDate dueDate = invoiceDate.plusDays(settings.getInvoiceDueDays());

		String invoiceNumber = generateInvoiceNumber(settings.getInvoicePrefix(), invoiceDate);

		LocalDate billingPeriodFrom = LocalDate.parse(request.getBillingPeriodFrom());
		LocalDate billingPeriodTo = LocalDate.parse(request.getBillingPeriodTo());

		boolean successfulInvoiceAlreadyExists = invoiceRepository
				.existsByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatusNot(device, billingPeriodFrom,
						billingPeriodTo, InvoiceStatus.FAILED);

		if (successfulInvoiceAlreadyExists) {
			throw new IllegalStateException("Invoice already generated for selected billing period");
		}

		invoiceRepository.deleteByDeviceAndBillingPeriodFromAndBillingPeriodToAndStatus(device, billingPeriodFrom,
				billingPeriodTo, InvoiceStatus.FAILED);

		BigDecimal previousDues = calculatePreviousDues(device, billingPeriodFrom);

		PayloadBillCalculationRequest calculationRequest = new PayloadBillCalculationRequest();
		calculationRequest.setDeviceId(device.getId());
		calculationRequest.setTariffId(tariff.getId());
		calculationRequest.setBillingPeriodFrom(request.getBillingPeriodFrom());
		calculationRequest.setBillingPeriodTo(request.getBillingPeriodTo());
		calculationRequest.setPreviousDues(previousDues);

		BillCalculationResponseDto bill = billingCalculatorService.calculateBillFromPayload(calculationRequest);

		BigDecimal discount = request.getDiscount() == null ? BigDecimal.ZERO : request.getDiscount();

		BigDecimal netAmount = bill.getTotalAmount().subtract(discount);

		if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Discount cannot be greater than total bill amount");
		}

		User assignedUser = device.getAssignedUser();

		Invoice invoice = Invoice.builder().invoiceNumber(invoiceNumber).device(device).tariff(tariff)
				.customer(assignedUser).customerName(resolveCustomerName(device, assignedUser))
				.email(assignedUser != null ? assignedUser.getEmail() : null)
				.phone(assignedUser != null ? assignedUser.getPhoneNo() : null).meterNumber(device.getDeviceId())
				.source(tariff.getSource()).billingType(device.getBillingType())
				.previousReading(bill.getPreviousReading()).currentReading(bill.getCurrentReading())
				.consumption(bill.getTotalConsumption()).amount(bill.getConsumptionAmount())
				.fixedCharge(bill.getFixedCharge()).tax(bill.getTaxAmount()).discount(discount)
				.previousDues(previousDues).netAmount(netAmount).invoiceDueDaysSnapshot(settings.getInvoiceDueDays())
				.gracePeriodDaysSnapshot(settings.getGracePeriodDays())
				.penaltyEnabledSnapshot(settings.getPenaltyEnabled())
				.penaltyPercentageSnapshot(
						settings.getPenaltyPercentage() == null ? BigDecimal.ZERO : settings.getPenaltyPercentage())
				.penaltyAmount(BigDecimal.ZERO).penaltyApplied(false).paidAmount(BigDecimal.ZERO)
				.balanceAmount(netAmount).status(InvoiceStatus.PENDING).paymentStatus(PaymentStatus.PENDING)
				.invoiceDate(invoiceDate).dueDate(dueDate).billingPeriodFrom(billingPeriodFrom)
				.billingPeriodTo(billingPeriodTo).remarks(request.getRemarks()).generationType(generationType)
				.generatedBy(generationType == InvoiceGenerationType.MANUAL ? securityUtils.getLoggedInUser() : null)
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

		Invoice savedInvoice = invoiceRepository.save(invoice);

		byte[] pdfBytes = invoicePdfService.generateInvoicePdf(savedInvoice);

		emailService.sendInvoiceGeneratedEmail(savedInvoice, pdfBytes);

		return mapToResponse(savedInvoice);
	}

	private BigDecimal calculatePreviousDues(Device device, LocalDate billingPeriodFrom) {

		BigDecimal previousDues = invoiceRepository.calculatePreviousDues(device, billingPeriodFrom);

		return previousDues == null ? BigDecimal.ZERO : previousDues;
	}

	private void validateTariffMatchesDevice(Device device, Tariff tariff, InvoiceGenerationType generationType) {

		if (device.getAssignedAdmin() == null) {
			throw new IllegalStateException("Device has no assigned admin");
		}

		if (device.getMeter() == null) {
			throw new IllegalStateException("Meter not configured for device");
		}

		if (device.getMeter().getSourceType() == null) {
			throw new IllegalStateException("Device source type not found");
		}

		if (tariff.getStatus() != TariffStatus.ACTIVE) {
			throw new IllegalArgumentException("Selected tariff is inactive");
		}

		if (generationType == InvoiceGenerationType.MANUAL) {

			User loggedInUser = securityUtils.getLoggedInUser();

			boolean isSuperAdmin = loggedInUser.getRole() != null && loggedInUser.getRole() == RoleType.SUPER_ADMIN;

			if (!isSuperAdmin) {

				boolean isOwnAdminTariff = tariff.getCreatedBy() != null && device.getAssignedAdmin() != null
						&& tariff.getCreatedBy().getId().equals(device.getAssignedAdmin().getId());

				boolean isSuperAdminTariff = tariff.getCreatedBy() != null && tariff.getCreatedBy().getRole() != null
						&& tariff.getCreatedBy().getRole() == RoleType.SUPER_ADMIN;

				if (!isOwnAdminTariff && !isSuperAdminTariff) {
					throw new IllegalArgumentException(
							"Selected tariff must belong to device assigned admin or Super Admin");
				}
			}
		}

		if (tariff.getSource() != device.getMeter().getSourceType()) {
			throw new IllegalArgumentException("Selected tariff source does not match device source type");
		}

		TariffCategory deviceCategory = tariffCategoryResolver
				.resolveFromApplication(device.getMeter().getApplication());

		if (tariff.getCategory() != deviceCategory) {
			throw new IllegalArgumentException(
					"Selected tariff category does not match meter application. Required category: " + deviceCategory);
		}
	}

	private String generateInvoiceNumber(String invoicePrefix, LocalDate invoiceDate) {

		String prefix = invoicePrefix == null || invoicePrefix.isBlank() ? "INV" : invoicePrefix.trim();

		String datePart = invoiceDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

		long todayInvoiceCount = invoiceRepository.countByInvoiceDate(invoiceDate);

		String sequence = String.format("%06d", todayInvoiceCount + 1);

		return prefix + "-" + datePart + "-" + sequence;
	}

	@Override
	@Transactional(readOnly = true)
	public InvoiceResponseDto getInvoiceById(Long invoiceId) {

		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

		return mapToResponse(invoice);
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

	private InvoiceResponseDto mapToResponse(Invoice invoice) {

		return InvoiceResponseDto.builder().id(invoice.getId()).invoiceNumber(invoice.getInvoiceNumber())
				.customerId(invoice.getCustomer() != null ? invoice.getCustomer().getId() : null)
				.customerName(invoice.getCustomerName()).email(invoice.getEmail()).phone(invoice.getPhone())
				.meterNumber(invoice.getMeterNumber()).source(invoice.getSource()).billingType(invoice.getBillingType())
				.tariffId(invoice.getTariff() != null ? invoice.getTariff().getId() : null)
				.tariffName(invoice.getTariff() != null ? invoice.getTariff().getName() : null)
				.previousReading(invoice.getPreviousReading()).currentReading(invoice.getCurrentReading())
				.consumption(invoice.getConsumption()).amount(invoice.getAmount()).fixedCharge(invoice.getFixedCharge())
				.tax(invoice.getTax()).discount(invoice.getDiscount()).previousDues(invoice.getPreviousDues())
				.netAmount(invoice.getNetAmount()).invoiceDueDaysSnapshot(invoice.getInvoiceDueDaysSnapshot())
				.gracePeriodDaysSnapshot(invoice.getGracePeriodDaysSnapshot())
				.penaltyEnabledSnapshot(invoice.getPenaltyEnabledSnapshot())
				.penaltyPercentageSnapshot(invoice.getPenaltyPercentageSnapshot())
				.penaltyAmount(invoice.getPenaltyAmount()).penaltyApplied(invoice.getPenaltyApplied())
				.status(invoice.getStatus()).paymentStatus(invoice.getPaymentStatus())
				.invoiceDate(invoice.getInvoiceDate()).dueDate(invoice.getDueDate())
				.billingPeriodFrom(invoice.getBillingPeriodFrom()).billingPeriodTo(invoice.getBillingPeriodTo())
				.remarks(invoice.getRemarks()).paidAmount(invoice.getPaidAmount())
				.balanceAmount(invoice.getBalanceAmount()).generationType(invoice.getGenerationType())
				.failureReason(invoice.getFailureReason()).createdAt(invoice.getCreatedAt())
				.pdfPreviewUrl(invoice.getId() != null && invoice.getStatus() != InvoiceStatus.FAILED
						? "/api/billing/invoices/" + invoice.getId() + "/preview"
						: null)
				.pdfDownloadUrl(invoice.getId() != null && invoice.getStatus() != InvoiceStatus.FAILED
						? "/api/billing/invoices/" + invoice.getId() + "/download"
						: null)
				.updatedAt(invoice.getUpdatedAt()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ByteArrayResource> generateInvoicePdfResponse(Long invoiceId, boolean download) {

		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

		if (invoice.getStatus() == InvoiceStatus.FAILED) {
			throw new IllegalStateException("PDF is not available for failed invoice");
		}

		byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);

		ByteArrayResource resource = new ByteArrayResource(pdfBytes);

		String fileName = invoice.getInvoiceNumber() + ".pdf";

		String disposition = download ? "attachment" : "inline";

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + fileName + "\"")
				.contentLength(pdfBytes.length).body(resource);
	}

	@Override
	@Transactional
	public PagedInvoiceResponseDto getInvoices(int page, int size, String search, InvoiceStatus status,
			PaymentStatus paymentStatus, SourceType source, BillingType billingType, String fromDate, String toDate) {

		invoiceOverdueService.updateOverdueInvoices();

		User loggedInUser = securityUtils.getLoggedInUser();

		Long adminId = null;
		Long userId = null;

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			adminId = loggedInUser.getId();
		} else if (loggedInUser.getRole() == RoleType.USER) {
			userId = loggedInUser.getId();
		}

		String normalizedSearch = null;

		if (search != null && !search.isBlank()) {
			normalizedSearch = search.trim();
		}

		LocalDate parsedFromDate = parseDate(fromDate, "fromDate");
		LocalDate parsedToDate = parseDate(toDate, "toDate");

		LocalDateTime fromDateTime = parsedFromDate != null ? parsedFromDate.atStartOfDay() : null;

		LocalDateTime toDateTime = parsedToDate != null ? parsedToDate.atTime(LocalTime.MAX) : null;

		Pageable pageable = PageRequest.of(page, size);

		Page<Invoice> invoicePage = invoiceRepository.findInvoicesWithFilters(adminId, userId, normalizedSearch, status,
				paymentStatus, source, billingType, fromDateTime, toDateTime, pageable);

		return PagedInvoiceResponseDto.builder()
				.invoices(invoicePage.getContent().stream().map(this::mapToResponse).toList())
				.currentPage(invoicePage.getNumber()).totalPages(invoicePage.getTotalPages())
				.totalElements(invoicePage.getTotalElements()).build();
	}

	private LocalDate parseDate(String date, String fieldName) {

		if (date == null || date.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InvoiceResponseDto sendInvoiceEmail(Long invoiceId) {

		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

		validateInvoiceEmailAllowed(invoice);

		byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);

		emailService.sendInvoiceGeneratedEmail(invoice, pdfBytes);

		return mapToResponse(invoice);
	}

	private void validateInvoiceEmailAllowed(Invoice invoice) {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (invoice.getStatus() == InvoiceStatus.FAILED) {
			throw new IllegalStateException("Email cannot be sent for failed invoice");
		}

		if (invoice.getEmail() == null || invoice.getEmail().isBlank()) {
			throw new IllegalStateException("Customer email is not available for this invoice");
		}

		if (loggedInUser.getRole() == RoleType.USER) {
			throw new IllegalArgumentException("User is not allowed to send invoice email");
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (invoice.getDevice() == null || invoice.getDevice().getAssignedAdmin() == null) {
				throw new IllegalStateException("Invoice device admin is not available");
			}

			if (!invoice.getDevice().getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to send email for this invoice");
			}
		}
	}

}