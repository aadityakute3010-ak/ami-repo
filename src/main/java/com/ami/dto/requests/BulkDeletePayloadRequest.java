package com.ami.dto.requests;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkDeletePayloadRequest {

	@NotEmpty(message = "At least one payload id is required")
	private List<@NotNull Long> payloadIds;
}