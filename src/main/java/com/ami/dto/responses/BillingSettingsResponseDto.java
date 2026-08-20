package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingSettingsResponseDto {

	private Long id;

	private Long adminId;

	private String adminName;

	private InvoiceSettingsResponseDto invoiceSettings;

	private TaxSettingsResponseDto taxSettings;

	private PenaltySettingsResponseDto penaltySettings;

	private ReminderSettingsResponseDto reminderSettings;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}