package com.ami.dto.responses;

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
public class PayloadSourceSummaryDTO {

	private String source;

	private Long totalPayloads;

	private Long successfulPayloads;

	private Long warningPayloads;

	private Long failedPayloads;

	private Long pendingPayloads;

	private Long waterPayloads;

	private Long energyPayloads;

	private Long gasPayloads;

	private Long solarPayloads;

	private Long totalDevices;

	private Long onlineDevices;

	private Long offlineDevices;

	private Double totalConsumption;

	private Double averageConsumption;

	private Double averageBattery;

	private Double averageSignalQuality;

	private Double successRate;

	private Double failureRate;
}