package com.ami.service.impl;

import java.time.LocalDateTime;
import com.ami.dto.requests.CreateTariffSlabRequestDto;
import com.ami.dto.responses.TariffSlabResponseDto;
import com.ami.entity.TariffSlab;
import com.ami.repository.TariffSlabRepository;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateTariffRequestDto;
import com.ami.dto.requests.UpdateTariffRequestDto;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.entity.Tariff;
import com.ami.repository.TariffRepository;
import com.ami.service.TariffService;

@Service
public class TariffServiceImpl
        implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffSlabRepository tariffSlabRepository;

    public TariffServiceImpl(
            TariffRepository tariffRepository,
            TariffSlabRepository tariffSlabRepository) {

        this.tariffRepository = tariffRepository;
        this.tariffSlabRepository = tariffSlabRepository;
    }

    @Override
    public TariffResponseDto createTariff(
            CreateTariffRequestDto request) {

        Tariff tariff =
                Tariff.builder()
                        .tariffName(
                                request.getTariffName())
                        .source(
                                request.getSource())
                        .ratePerUnit(
                                request.getRatePerUnit())
                        .fixedCharge(
                                request.getFixedCharge())
                        .taxPercentage(
                                request.getTaxPercentage())
                        .description(
                                request.getDescription())
                        .active(true)
                        .createdAt(
                                LocalDateTime.now())
                        .updatedAt(
                                LocalDateTime.now())
                        .build();

        tariff =
                tariffRepository.save(
                        tariff);

        return mapToResponse(tariff);
    }

    @Override
    public List<TariffResponseDto>
    getAllTariffs() {

        return tariffRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TariffResponseDto
    getTariffById(Long id) {

        Tariff tariff =
                tariffRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tariff not found"));

        return mapToResponse(tariff);
    }

    @Override
    public TariffResponseDto updateTariff(
            Long id,
            UpdateTariffRequestDto request) {

        Tariff tariff =
                tariffRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tariff not found"));

        tariff.setRatePerUnit(
                request.getRatePerUnit());

        tariff.setFixedCharge(
                request.getFixedCharge());

        tariff.setTaxPercentage(
                request.getTaxPercentage());

        tariff.setDescription(
                request.getDescription());

        tariff.setActive(
                request.getActive());

        tariff.setUpdatedAt(
                LocalDateTime.now());

        tariff =
                tariffRepository.save(
                        tariff);

        return mapToResponse(tariff);
    }

    @Override
    public String deleteTariff(
            Long id) {

        Tariff tariff =
                tariffRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tariff not found"));

        tariffRepository.delete(tariff);

        return "Tariff deleted successfully";
    }

    private TariffResponseDto
    mapToResponse(
            Tariff tariff) {

        return TariffResponseDto
                .builder()
                .id(tariff.getId())
                .tariffName(
                        tariff.getTariffName())
                .source(
                        tariff.getSource())
                .ratePerUnit(
                        tariff.getRatePerUnit())
                .fixedCharge(
                        tariff.getFixedCharge())
                .taxPercentage(
                        tariff.getTaxPercentage())
                .description(
                        tariff.getDescription())
                .active(
                        tariff.getActive())
                .build();
    }
    @Override
    public TariffSlabResponseDto addSlab(
            Long tariffId,
            CreateTariffSlabRequestDto request) {

        tariffRepository.findById(tariffId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tariff not found"));

        TariffSlab slab =
                TariffSlab.builder()
                        .tariffId(tariffId)
                        .fromUnit(request.getFromUnit())
                        .toUnit(request.getToUnit())
                        .ratePerUnit(
                                request.getRatePerUnit())
                        .build();

        slab =
                tariffSlabRepository.save(slab);

        return TariffSlabResponseDto
                .builder()
                .id(slab.getId())
                .tariffId(slab.getTariffId())
                .fromUnit(slab.getFromUnit())
                .toUnit(slab.getToUnit())
                .ratePerUnit(
                        slab.getRatePerUnit())
                .build();
    }
    @Override
    public List<TariffSlabResponseDto>
    getSlabsByTariffId(
            Long tariffId) {

        return tariffSlabRepository
                .findByTariffId(tariffId)
                .stream()
                .map(slab ->
                        TariffSlabResponseDto
                                .builder()
                                .id(slab.getId())
                                .tariffId(
                                        slab.getTariffId())
                                .fromUnit(
                                        slab.getFromUnit())
                                .toUnit(
                                        slab.getToUnit())
                                .ratePerUnit(
                                        slab.getRatePerUnit())
                                .build())
                .toList();
    }
}