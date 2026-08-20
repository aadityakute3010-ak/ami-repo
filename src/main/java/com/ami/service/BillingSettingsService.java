package com.ami.service;

import com.ami.dto.requests.InvoiceSettingsRequestDto;
import com.ami.dto.requests.PenaltySettingsRequestDto;
import com.ami.dto.requests.ReminderSettingsRequestDto;
import com.ami.dto.requests.TaxSettingsRequestDto;
import com.ami.dto.responses.BillingSettingsResponseDto;
import com.ami.entity.BillingSettings;
import com.ami.entity.User;

public interface BillingSettingsService {

	BillingSettingsResponseDto getSettings();

	BillingSettingsResponseDto updateInvoiceSettings(InvoiceSettingsRequestDto request);

	BillingSettingsResponseDto updateTaxSettings(TaxSettingsRequestDto request);

	BillingSettingsResponseDto updatePenaltySettings(PenaltySettingsRequestDto request);

	BillingSettingsResponseDto updateReminderSettings(ReminderSettingsRequestDto request);

	BillingSettings getSettingsForAdmin(User admin);
}