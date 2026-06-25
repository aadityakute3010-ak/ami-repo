package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateTariffRequestDto;
import com.ami.dto.requests.CreateTariffSlabRequestDto;
import com.ami.dto.requests.UpdateTariffRequestDto;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.dto.responses.TariffSlabResponseDto;

public interface TariffService {

    TariffResponseDto createTariff(
            CreateTariffRequestDto request);

    List<TariffResponseDto> getAllTariffs();

    TariffResponseDto getTariffById(
            Long id);

    TariffResponseDto updateTariff(
            Long id,
            UpdateTariffRequestDto request);

    String deleteTariff(
            Long id);
    
    TariffSlabResponseDto addSlab(
            Long tariffId,
            CreateTariffSlabRequestDto request);

    List<TariffSlabResponseDto>
    getSlabsByTariffId(
            Long tariffId);
}