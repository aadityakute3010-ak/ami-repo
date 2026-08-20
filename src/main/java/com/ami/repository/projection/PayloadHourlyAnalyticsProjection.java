package com.ami.repository.projection;

import java.time.LocalDateTime;

public interface PayloadHourlyAnalyticsProjection {

	Integer getReadingHour();

	LocalDateTime getLastPayloadTime();

	Double getTotalReading();

	Double getConsumption();
}