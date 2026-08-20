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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingSettingsServiceImpl implements BillingSettingsService {

	private final BillingSettingsRepository billingSettingsRepository;
	private final SecurityUtils securityUtils;

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

		settings.setInvoicePrefix(request.getInvoicePrefix().trim());
		settings.setInvoiceDueDays(request.getInvoiceDueDays());
		settings.setCurrency(request.getCurrency().trim());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updateTaxSettings(TaxSettingsRequestDto request) {

		validateTaxSettings(request);

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		settings.setTaxEnabled(request.getTaxEnabled());
		settings.setDefaultTaxPercentage(defaultZero(request.getDefaultTaxPercentage()));
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

		return mapToResponse(saved);
	}

	@Override
	@Transactional
	public BillingSettingsResponseDto updatePenaltySettings(PenaltySettingsRequestDto request) {

		validatePenaltySettings(request);

		User loggedInUser = securityUtils.getLoggedInUser();
		BillingSettings settings = getOrCreateSettingsForLoggedInUser(loggedInUser);

		settings.setPenaltyEnabled(request.getPenaltyEnabled());
		settings.setPenaltyPercentage(defaultZero(request.getPenaltyPercentage()));
		settings.setGracePeriodDays(request.getGracePeriodDays());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

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

		settings.setReminderEnabled(request.getReminderEnabled());
		settings.setReminderBeforeDueDays(request.getReminderBeforeDueDays());
		settings.setUpdatedAt(LocalDateTime.now());

		BillingSettings saved = billingSettingsRepository.save(settings);

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
				.currency(globalSettings.getCurrency())
				.taxEnabled(globalSettings.getTaxEnabled())
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
				.currency("INR")
				.taxEnabled(true).defaultTaxPercentage(BigDecimal.ZERO).penaltyEnabled(false)
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