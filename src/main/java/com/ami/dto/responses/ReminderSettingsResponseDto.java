package com.ami.dto.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReminderSettingsResponseDto {

	private Boolean reminderEnabled;

	private Integer reminderBeforeDueDays;
}