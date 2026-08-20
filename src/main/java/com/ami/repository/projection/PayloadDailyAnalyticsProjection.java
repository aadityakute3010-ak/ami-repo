package com.ami.repository.projection;

import java.time.LocalDate;

public interface PayloadDailyAnalyticsProjection {

	LocalDate getReadingDate();

	Double getOpeningReading();

	Double getClosingReading();

	Double getTotalReading();

	Double getConsumption();
}