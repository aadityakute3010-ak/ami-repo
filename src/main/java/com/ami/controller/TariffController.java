package com.ami.controller;

import java.util.List;
import com.ami.dto.requests.CreateTariffSlabRequestDto;
import com.ami.dto.responses.TariffSlabResponseDto;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateTariffRequestDto;
import com.ami.dto.requests.UpdateTariffRequestDto;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.service.TariffService;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(
            TariffService tariffService) {

        this.tariffService = tariffService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public TariffResponseDto createTariff(
            @RequestBody
            CreateTariffRequestDto request) {

        return tariffService
                .createTariff(request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping
    public List<TariffResponseDto>
    getAllTariffs() {

        return tariffService
                .getAllTariffs();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/{id}")
    public TariffResponseDto getTariffById(
            @PathVariable Long id) {

        return tariffService
                .getTariffById(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public TariffResponseDto updateTariff(
            @PathVariable Long id,
            @RequestBody
            UpdateTariffRequestDto request) {

        return tariffService
                .updateTariff(
                        id,
                        request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteTariff(
            @PathVariable Long id) {

        return tariffService
                .deleteTariff(id);
    }
    @PostMapping("/{tariffId}/slabs")
    public TariffSlabResponseDto addSlab(

            @PathVariable Long tariffId,

            @RequestBody
            CreateTariffSlabRequestDto request) {

        return tariffService.addSlab(
                tariffId,
                request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/{tariffId}/slabs")
    public List<TariffSlabResponseDto>
    getSlabsByTariffId(

            @PathVariable Long tariffId) {

        return tariffService
                .getSlabsByTariffId(
                        tariffId);
    }
}