package com.ami.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.entity.Device;
import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.TariffCategoryResolver;
import com.ami.repository.TariffRepository;
import com.ami.service.PrepaidTariffResolverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrepaidTariffResolverServiceImpl implements PrepaidTariffResolverService {

	private final TariffRepository tariffRepository;

	private final TariffCategoryResolver tariffCategoryResolver;

	@Override
	@Transactional(readOnly = true)
	public Tariff resolveTariffForPrepaidDevice(Device device) {

		validateDevice(device);

		SourceType sourceType = device.getMeter().getSourceType();

		TariffCategory tariffCategory = tariffCategoryResolver
				.resolveFromApplication(device.getMeter().getApplication());

		User assignedAdmin = device.getAssignedAdmin();

		if (assignedAdmin != null) {

			return tariffRepository
					.findFirstByCreatedByAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(assignedAdmin, sourceType,
							tariffCategory, TariffStatus.ACTIVE)
					.orElseGet(() -> findSuperAdminFallbackTariff(sourceType, tariffCategory));
		}

		return findSuperAdminFallbackTariff(sourceType, tariffCategory);
	}

	private Tariff findSuperAdminFallbackTariff(SourceType sourceType, TariffCategory tariffCategory) {

		return tariffRepository
				.findFirstByCreatedBy_RoleAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(RoleType.SUPER_ADMIN,
						sourceType, tariffCategory, TariffStatus.ACTIVE)
				.orElseThrow(() -> new ResourceNotFoundException("Active prepaid tariff not found for source "
						+ sourceType + " and category " + tariffCategory));
	}

	private void validateDevice(Device device) {

		if (device == null) {
			throw new IllegalArgumentException("Device is required to resolve prepaid tariff");
		}

		if (device.getMeter() == null) {
			throw new IllegalStateException("Meter is not configured for this device");
		}

		if (device.getMeter().getSourceType() == null) {
			throw new IllegalStateException("Meter source type is not configured");
		}

		if (device.getMeter().getApplication() == null || device.getMeter().getApplication().isBlank()) {
			throw new IllegalStateException("Meter application is not configured");
		}
	}
}