package com.ami.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CreatePrepaidRechargeOrderRequestDto;
import com.ami.dto.requests.VerifyPrepaidRechargeRequestDto;
import com.ami.dto.responses.PrepaidBalanceResponseDto;
import com.ami.dto.responses.PrepaidRechargeOrderResponseDto;
import com.ami.dto.responses.PrepaidRechargeResponseDto;
import com.ami.dto.responses.PrepaidUsageLedgerResponseDto;
import com.ami.enums.RechargeStatus;

public interface PrepaidRechargeService {

	PrepaidRechargeOrderResponseDto createRechargeOrder(CreatePrepaidRechargeOrderRequestDto request);

	PrepaidRechargeResponseDto verifyRechargePayment(VerifyPrepaidRechargeRequestDto request);

	PrepaidBalanceResponseDto getPrepaidBalance(Long deviceId);

	List<PrepaidUsageLedgerResponseDto> getPrepaidUsageHistory(Long deviceId);

	Page<PrepaidRechargeResponseDto> getDeviceRechargeHistory(Long deviceId, int page, int size, String search,
			RechargeStatus status, LocalDateTime fromDate, LocalDateTime toDate);

}