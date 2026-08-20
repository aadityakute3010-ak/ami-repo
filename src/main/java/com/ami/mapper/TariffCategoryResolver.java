package com.ami.mapper;

import org.springframework.stereotype.Component;

import com.ami.enums.TariffCategory;

@Component
public class TariffCategoryResolver {

	public TariffCategory resolveFromApplication(String application) {

		if (application == null || application.isBlank()) {
			throw new IllegalStateException("Meter application is not configured");
		}

		String normalizedApplication = application.trim().toUpperCase().replace("-", "_").replace(" ", "_");

		return switch (normalizedApplication) {

		case "DOMESTIC", "RESIDENTIAL" -> TariffCategory.RESIDENTIAL;

		case "COMMERCIAL" -> TariffCategory.COMMERCIAL;

		case "INDUSTRIAL" -> TariffCategory.INDUSTRIAL;

		case "AGRICULTURE", "AGRICULTURAL" -> TariffCategory.AGRICULTURE;

		default -> throw new IllegalStateException("Unsupported meter application: " + application);
		};
	}
}