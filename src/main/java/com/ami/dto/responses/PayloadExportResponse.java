package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayloadExportResponse {

	private byte[] data;

	private String fileName;

	private String contentType;
}