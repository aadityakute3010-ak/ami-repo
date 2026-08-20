package com.ami.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.ami.dto.responses.TariffResponseDto;
import com.ami.dto.responses.TariffSlabResponseDto;
import com.ami.entity.Tariff;
import com.ami.entity.TariffSlab;
import com.ami.entity.User;

@Component
public class TariffMapper {

	public TariffResponseDto toTariffResponse(Tariff tariff) {

		List<TariffSlabResponseDto> slabResponses = tariff.getSlabs() == null ? Collections.emptyList()
				: tariff.getSlabs().stream().map(this::toSlabResponse).toList();

		User createdBy = tariff.getCreatedBy();

		return TariffResponseDto.builder().id(tariff.getId()).name(tariff.getName()).source(tariff.getSource())
				.category(tariff.getCategory()).unit(tariff.getUnit()).rate(tariff.getRate())
				.fixedCharge(tariff.getFixedCharge()).tax(tariff.getTax()).status(tariff.getStatus())
				.slabs(slabResponses).createdById(createdBy != null ? createdBy.getId() : null)
				.createdBy(buildUserDisplayName(createdBy)).createdAt(tariff.getCreatedAt())
				.updatedAt(tariff.getUpdatedAt()).description(tariff.getDescription()).version(tariff.getVersion())
				.build();
	}

	public TariffSlabResponseDto toSlabResponse(TariffSlab slab) {

		Tariff tariff = slab.getTariff();

		return TariffSlabResponseDto.builder().id(slab.getId()).tariffId(tariff != null ? tariff.getId() : null)
				.source(tariff != null ? tariff.getSource() : null).unit(tariff != null ? tariff.getUnit() : null)
				.from(slab.getFromUnit()).to(slab.getToUnit()).rate(slab.getRate()).fixedCharge(slab.getFixedCharge())
				.tax(slab.getTax()).status(slab.getStatus()).description(slab.getDescription())
				.createdAt(slab.getCreatedAt()).updatedAt(slab.getUpdatedAt()).build();
	}

	private String buildUserDisplayName(User user) {

		if (user == null) {
			return null;
		}

		String fullName = Stream.of(user.getFirstName(), user.getLastName())
				.filter(value -> value != null && !value.isBlank()).collect(Collectors.joining(" "));

		if (!fullName.isBlank()) {
			return fullName;
		}

		if (user.getUserName() != null && !user.getUserName().isBlank()) {
			return user.getUserName();
		}

		return user.getEmail();
	}

	public TariffResponseDto toResponseDto(Tariff tariff) {

		return TariffResponseDto.builder().id(tariff.getId()).name(tariff.getName()).source(tariff.getSource())
				.category(tariff.getCategory()).unit(tariff.getUnit()).rate(tariff.getRate())
				.fixedCharge(tariff.getFixedCharge()).tax(tariff.getTax()).status(tariff.getStatus())
				.description(tariff.getDescription()).createdAt(tariff.getCreatedAt()).updatedAt(tariff.getUpdatedAt())
				.build();
	}

}