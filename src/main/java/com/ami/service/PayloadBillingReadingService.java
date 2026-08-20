package com.ami.service;

import java.time.LocalDate;

import com.ami.dto.responses.PayloadBillingReadingResponseDto;

public interface PayloadBillingReadingService {

	PayloadBillingReadingResponseDto getBillingReadings(Long deviceId, LocalDate billingPeriodFrom,LocalDate billingPeriodTo);
} 