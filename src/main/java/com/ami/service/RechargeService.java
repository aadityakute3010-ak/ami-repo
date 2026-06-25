package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateRechargeRequestDto;
import com.ami.dto.responses.RechargeResponseDto;

public interface RechargeService {

    RechargeResponseDto createRecharge(
            CreateRechargeRequestDto request);

    List<RechargeResponseDto> getAllRecharges();

    RechargeResponseDto getRechargeById(
            Long id);

    List<RechargeResponseDto>
    getCustomerRecharges(
            String customerId);
}