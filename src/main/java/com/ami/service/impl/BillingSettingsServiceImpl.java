package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.InvoiceSettingsRequestDto;
import com.ami.dto.requests.PenaltySettingsRequestDto;
import com.ami.dto.requests.ReminderSettingsRequestDto;
import com.ami.dto.requests.TaxSettingsRequestDto;
import com.ami.dto.responses.BillingSettingsResponseDto;
import com.ami.dto.responses.InvoiceSettingsResponseDto;
import com.ami.dto.responses.PenaltySettingsResponseDto;
import com.ami.dto.responses.ReminderSettingsResponseDto;
import com.ami.dto.responses.TaxSettingsResponseDto;
import com.ami.entity.BillingSettings;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.repository.BillingSettingsRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.BillingSettingsService;
import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.service.AuditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingSettingsServiceImpl implements BillingSettingsService {

	private final BillingSettingsRepository billingSettingsRepository;
	private final SecurityUtils securityUtils;
	private final AuditService auditService;

	@Override
	@Transactional
	public BillingSettingsResponseDto getSettings() {

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = resolveSettingsForLoggedInUser(loggedInUser);

		return mapToResponse(settings);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updateInvoiceSettings(InvoiceSettingsRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		String previousPrefix = settings.getInvoicePrefix();
		Integer previousDueDays = settings.getInvoiceDueDays();
		String previousCurrency = settings.getCurrency();

		settings.setInvoicePrefix(request.getInvoicePrefix().trim());
		settings.setInvoiceDueDays(request.getInvoiceDueDays());
		settings.setCurrency(request.getCurrency().trim());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(saved.getId());
		auditRequest.setEntityType("BILLING_SETTINGS");
		auditRequest.setTargetAdminId(saved.getAdmin() != null ? saved.getAdmin().getId() : null);
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Invoice settings updated for " + settingsOwnerLabel(saved) + ": prefix '"
				+ previousPrefix + "' -> '" + saved.getInvoicePrefix() + "', due days " + previousDueDays + " -> "
				+ saved.getInvoiceDueDays() + ", currency '" + previousCurrency + "' -> '" + saved.getCurrency() + "'");
		auditService.createAuditLog(auditRequest);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updateTaxSettings(TaxSettingsRequestDto request) {

		validateTaxSettings(request);

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		Boolean previousTaxEnabled = settings.getTaxEnabled();
		BigDecimal previousTaxPercentage = settings.getDefaultTaxPercentage();

		settings.setTaxEnabled(request.getTaxEnabled());
		settings.setDefaultTaxPercentage(defaultZero(request.getDefaultTaxPercentage()));
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(saved.getId());
		auditRequest.setEntityType("BILLING_SETTINGS");
		auditRequest.setTargetAdminId(saved.getAdmin() != null ? saved.getAdmin().getId() : null);
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tax settings updated for " + settingsOwnerLabel(saved) + ": enabled "
				+ previousTaxEnabled + " -> " + saved.getTaxEnabled() + ", percentage " + previousTaxPercentage + " -> "
				+ saved.getDefaultTaxPercentage());
		auditService.createAuditLog(auditRequest);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updatePenaltySettings(PenaltySettingsRequestDto request) {

		validatePenaltySettings(request);

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		Boolean previousPenaltyEnabled = settings.getPenaltyEnabled();
		BigDecimal previousPenaltyPercentage = settings.getPenaltyPercentage();
		Integer previousGracePeriodDays = settings.getGracePeriodDays();

		settings.setPenaltyEnabled(request.getPenaltyEnabled());
		settings.setPenaltyPercentage(defaultZero(request.getPenaltyPercentage()));
		settings.setGracePeriodDays(request.getGracePeriodDays());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(saved.getId());
		auditRequest.setEntityType("BILLING_SETTINGS");
		auditRequest.setTargetAdminId(saved.getAdmin() != null ? saved.getAdmin().getId() : null);
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Penalty settings updated for " + settingsOwnerLabel(saved) + ": enabled "
				+ previousPenaltyEnabled + " -> " + saved.getPenaltyEnabled() + ", percentage "
				+ previousPenaltyPercentage + " -> " + saved.getPenaltyPercentage() + ", grace period days "
				+ previousGracePeriodDays + " -> " + saved.getGracePeriodDays());
		auditService.createAuditLog(auditRequest);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updateReminderSettings(ReminderSettingsRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		if (request.getReminderEnabled() && request.getReminderBeforeDueDays() > settings.getInvoiceDueDays()) {
			throw new IllegalArgumentException("Reminder before due days cannot be greater than invoice due days");
		}

		Boolean previousReminderEnabled = settings.getReminderEnabled();
		Integer previousReminderBeforeDueDays = settings.getReminderBeforeDueDays();

		settings.setReminderEnabled(request.getReminderEnabled());
		settings.setReminderBeforeDueDays(request.getReminderBeforeDueDays());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(saved.getId());
		auditRequest.setEntityType("BILLING_SETTINGS");
		auditRequest.setTargetAdminId(saved.getAdmin() != null ? saved.getAdmin().getId() : null);
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Reminder settings updated for " + settingsOwnerLabel(saved) + ": enabled "
				+ previousReminderEnabled + " -> " + saved.getReminderEnabled() + ", before due days "
				+ previousReminderBeforeDueDays + " -> " + saved.getReminderBeforeDueDays());
		auditService.createAuditLog(auditRequest);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettings getSettingsForAdmin(User admin) {

		if (admin == null) {
			return getGlobalDefaultSettings();
		}

		return billingSettingsRepository.findByAdmin(admin).orElseGet(() -> createDefaultSettingsForAdmin(admin));
	}

	private BillingSettings resolveSettingsForLoggedInUser(User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return getGlobalDefaultSettings();
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return getSettingsForAdmin(loggedInUser);
		}

		throw new IllegalArgumentException("Only Super Admin or Admin can access billing settings");
	}

	private BillingSettings getOrCreateSettingsForLoggedInUser(User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return getGlobalDefaultSettings();
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return getSettingsForAdmin(loggedInUser);
		}

		throw new IllegalArgumentException("Only Super Admin or Admin can update billing settings");
	}

	private BillingSettings getGlobalDefaultSettings() {

		return billingSettingsRepository.findByAdminIsNull().orElseGet(() -> createDefaultSettings(null));
	}

	private BillingSettings createDefaultSettingsForAdmin(User admin) {

		BillingSettings globalSettings = getGlobalDefaultSettings();

		BillingSettings settings = BillingSettings.builder().admin(admin)
				.invoicePrefix(globalSettings.getInvoicePrefix()).invoiceDueDays(globalSettings.getInvoiceDueDays())
				.currency(globalSettings.getCurrency()).taxEnabled(globalSettings.getTaxEnabled())
				.defaultTaxPercentage(globalSettings.getDefaultTaxPercentage())
				.penaltyEnabled(globalSettings.getPenaltyEnabled())
				.penaltyPercentage(globalSettings.getPenaltyPercentage())
				.gracePeriodDays(globalSettings.getGracePeriodDays())
				.reminderEnabled(globalSettings.getReminderEnabled())
				.reminderBeforeDueDays(globalSettings.getReminderBeforeDueDays()).createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now()).build();

		return billingSettingsRepository.save(settings);
	}

	private BillingSettings createDefaultSettings(User admin) {

		BillingSettings settings = BillingSettings.builder().admin(admin).invoicePrefix("INV").invoiceDueDays(15)
				.currency("INR").taxEnabled(true).defaultTaxPercentage(BigDecimal.ZERO).penaltyEnabled(false)
				.penaltyPercentage(BigDecimal.ZERO).gracePeriodDays(0).reminderEnabled(true).reminderBeforeDueDays(3)
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

		return billingSettingsRepository.save(settings);
	}

	private void validateTaxSettings(TaxSettingsRequestDto request) {

		if (request.getTaxEnabled() && request.getDefaultTaxPercentage() == null) {
			throw new IllegalArgumentException("Default tax percentage is required when tax is enabled");
		}
	}

	private void validatePenaltySettings(PenaltySettingsRequestDto request) {

		if (request.getPenaltyEnabled() && request.getPenaltyPercentage() == null) {
			throw new IllegalArgumentException("Penalty percentage is required when penalty is enabled");
		}
	}

	private BigDecimal defaultZero(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private String settingsOwnerLabel(BillingSettings settings) {

		User admin = settings.getAdmin();

		return admin != null ? admin.getEmail() : "GLOBAL";
	}

	private BillingSettingsResponseDto mapToResponse(BillingSettings settings) {

		User admin = settings.getAdmin();

		return BillingSettingsResponseDto.builder().id(settings.getId()).adminId(admin != null ? admin.getId() : null)
				.adminName(admin != null ? admin.getFirstName() + " " + admin.getLastName() : "GLOBAL")
				.invoiceSettings(InvoiceSettingsResponseDto.builder().invoicePrefix(settings.getInvoicePrefix())
						.invoiceDueDays(settings.getInvoiceDueDays()).currency(settings.getCurrency()).build())

				.taxSettings(TaxSettingsResponseDto.builder().taxEnabled(settings.getTaxEnabled())
						.defaultTaxPercentage(settings.getDefaultTaxPercentage()).build())
				.penaltySettings(PenaltySettingsResponseDto.builder().penaltyEnabled(settings.getPenaltyEnabled())
						.penaltyPercentage(settings.getPenaltyPercentage())
						.gracePeriodDays(settings.getGracePeriodDays()).build())
				.reminderSettings(ReminderSettingsResponseDto.builder().reminderEnabled(settings.getReminderEnabled())
						.reminderBeforeDueDays(settings.getReminderBeforeDueDays()).build())
				.createdAt(settings.getCreatedAt()).updatedAt(settings.getUpdatedAt()).build();
	}
}