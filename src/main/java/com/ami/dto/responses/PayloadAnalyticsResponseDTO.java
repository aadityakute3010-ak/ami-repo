package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayloadAnalyticsResponseDTO {

	private Long totalPayloads;

	private Long successfulPayloads;

	private Long failedPayloads;

	private Long warningPayloads;

	private Long pendingPayloads;

	private Long onlineDevices;

	private Long offlineDevices;

	private Double successRate;

	private Double failureRate;

	private List<ConsumptionTrendDTO> consumptionTrend;

	private List<HourlyReadingDTO> hourlyReadings;

	private Long energyPayloads;

	private Long waterPayloads;

	private Long gasPayloads;

	private Long solarPayloads;

	private Double averageBattery;

	private Double averageSignalQuality;

	private Double averageSignalPower;

	private LocalDateTime lastPayloadTime;
}