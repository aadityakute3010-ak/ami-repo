package com.ami.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayloadStatsDTO {

	private long totalPayloads;

	private long successfulPayloads;

	private long failedPayloads;

	private long onlineDevices;
}