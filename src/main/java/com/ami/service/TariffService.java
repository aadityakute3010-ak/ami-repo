package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateTariffRequest;
import com.ami.dto.requests.CreateTariffSlabRequest;
import com.ami.dto.requests.UpdateTariffRequest;
import com.ami.dto.requests.UpdateTariffSlabRequest;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.dto.responses.TariffSlabResponseDto;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

public interface TariffService {

	TariffResponseDto createTariff(CreateTariffRequest request);

	List<TariffResponseDto> getAllTariffs(SourceType source, TariffCategory category, TariffStatus status,
			String search);

	TariffResponseDto getTariffById(Long tariffId);

	TariffResponseDto updateTariff(Long tariffId, UpdateTariffRequest request);

	void deleteTariff(Long tariffId);

	TariffSlabResponseDto createSlab(Long tariffId, CreateTariffSlabRequest request);

	List<TariffSlabResponseDto> getSlabs(Long tariffId);

	TariffSlabResponseDto updateSlab(Long tariffId, Long slabId, UpdateTariffSlabRequest request);

	void deleteSlab(Long tariffId, Long slabId);
}