package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateRechargeRequestDto;
import com.ami.dto.responses.RechargeResponseDto;
import com.ami.service.RechargeService;

@RestController
@RequestMapping("/api/recharges")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(
            RechargeService rechargeService) {

        this.rechargeService = rechargeService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public RechargeResponseDto createRecharge(
            @RequestBody
            CreateRechargeRequestDto request) {

        return rechargeService
                .createRecharge(request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")

    @GetMapping
    public List<RechargeResponseDto>
    getAllRecharges() {

        return rechargeService
                .getAllRecharges();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/{id}")
    public RechargeResponseDto getRechargeById(
            @PathVariable Long id) {

        return rechargeService
                .getRechargeById(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/customer/{customerId}")
    public List<RechargeResponseDto>
    getCustomerRecharges(
            @PathVariable String customerId) {

        return rechargeService
                .getCustomerRecharges(
                        customerId);
    }
}