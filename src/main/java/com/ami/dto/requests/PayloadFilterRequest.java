package com.ami.dto.requests;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.ami.enums.PayloadStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayloadFilterRequest {

	private Long deviceId;

	private String consumer;

	private PayloadStatus status; 

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;

	private String search;

	@Builder.Default
	private int page = 0;

	@Builder.Default
	private int size = 10;
}