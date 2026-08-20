package com.ami.dto.requests;

import com.ami.enums.BillingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignBillingTypeRequestDto {

	@NotNull(message = "Billing type is required")
	private BillingType billingType;
}