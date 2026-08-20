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
public class PayloadStatsDTO {

	private long totalPayloads;

	private long successfulPayloads;

	private long warningPayloads;

	private long failedPayloads;

	private long totalDevices;

	private long onlineDevices;

	private long offlineDevices;

	private double averageBattery;

	private double averageSignal;

	private double todayConsumption;

	private double todayRecharge;

	private double totalPayloadsTodayPercentage;

	private double successfulPayloadsTodayPercentage;

	private double warningPayloadsTodayPercentage;

	private double failedPayloadsTodayPercentage;

	private double onlineDevicesTodayPercentage;

	private double offlineDevicesTodayPercentage;

}