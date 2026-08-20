package com.ami.service;

import com.ami.dto.requests.BillCalculationRequest;
import com.ami.dto.requests.PayloadBillCalculationRequest;
import com.ami.dto.responses.BillCalculationResponseDto;

public interface BillingCalculatorService {

	BillCalculationResponseDto calculateBill(BillCalculationRequest request);
	
	BillCalculationResponseDto calculateBillFromPayload(PayloadBillCalculationRequest request);
}