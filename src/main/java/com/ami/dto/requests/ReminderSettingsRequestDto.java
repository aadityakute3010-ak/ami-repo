package com.ami.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReminderSettingsRequestDto {

	@NotNull(message = "Reminder enabled flag is required")
	private Boolean reminderEnabled;

	@NotNull(message = "Reminder before due days is required")
	@Min(value = 0, message = "Reminder before due days cannot be negative")
	private Integer reminderBeforeDueDays;
}