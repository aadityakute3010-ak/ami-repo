package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateRechargeRequestDto;
import com.ami.dto.responses.RechargeResponseDto;
import com.ami.entity.Recharge;
import com.ami.repository.RechargeRepository;
import com.ami.service.RechargeService;

@Service
public class RechargeServiceImpl
        implements RechargeService {

    private final RechargeRepository rechargeRepository;

    public RechargeServiceImpl(
            RechargeRepository rechargeRepository) {

        this.rechargeRepository =
                rechargeRepository;
    }

    @Override
    public RechargeResponseDto createRecharge(
            CreateRechargeRequestDto request) {

        Recharge recharge =
                Recharge.builder()
                        .rechargeNumber(
                                "REC-" +
                                System.currentTimeMillis())
                        .customerId(
                                request.getCustomerId())
                        .customerName(
                                request.getCustomerName())
                        .meterNumber(
                                request.getMeterNumber())
                        .amount(
                                request.getAmount())
                        .unitsAdded(
                                request.getUnitsAdded())
                        .paymentMethod(
                                request.getPaymentMethod())
                        .remarks(
                                request.getRemarks())
                        .rechargeDate(
                                LocalDateTime.now())
                        .createdAt(
                                LocalDateTime.now())
                        .build();

        recharge =
                rechargeRepository.save(
                        recharge);

        return mapToResponse(recharge);
    }

    @Override
    public List<RechargeResponseDto>
    getAllRecharges() {

        return rechargeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RechargeResponseDto
    getRechargeById(Long id) {

        Recharge recharge =
                rechargeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recharge not found"));

        return mapToResponse(recharge);
    }

    @Override
    public List<RechargeResponseDto>
    getCustomerRecharges(
            String customerId) {

        return rechargeRepository
                .findByCustomerId(
                        customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RechargeResponseDto
    mapToResponse(
            Recharge recharge) {

        return RechargeResponseDto
                .builder()
                .id(recharge.getId())
                .rechargeNumber(
                        recharge.getRechargeNumber())
                .customerId(
                        recharge.getCustomerId())
                .customerName(
                        recharge.getCustomerName())
                .amount(
                        recharge.getAmount())
                .unitsAdded(
                        recharge.getUnitsAdded())
                .rechargeDate(
                        recharge.getRechargeDate())
                .build();
    }
}