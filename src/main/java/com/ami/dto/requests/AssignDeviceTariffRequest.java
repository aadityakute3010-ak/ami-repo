package com.ami.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignDeviceTariffRequest {

	@NotNull(message = "Tariff ID is required")
	private Long tariffId;
}