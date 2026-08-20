package com.ami.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.responses.PrepaidUnitCalculationResponseDto;
import com.ami.entity.Device;
import com.ami.entity.PrepaidRechargePlan;
import com.ami.entity.Tariff;
import com.ami.entity.TariffSlab;
import com.ami.service.PrepaidUnitCalculationService;

@Service
public class PrepaidUnitCalculationServiceImpl implements PrepaidUnitCalculationService {

	@Override
	@Transactional(readOnly = true)
	public PrepaidUnitCalculationResponseDto calculateUnits(Device device, PrepaidRechargePlan plan, Tariff tariff) {

		validateInputs(device, plan, tariff);

		BigDecimal rechargeAmount = plan.getAmount();

		BigDecimal fixedCharge = zeroIfNull(tariff.getFixedCharge());

		BigDecimal taxPercentage = zeroIfNull(tariff.getTax());

		BigDecimal amountBeforeTax = calculateAmountBeforeTax(rechargeAmount, taxPercentage);

		BigDecimal taxAmount = rechargeAmount.subtract(amountBeforeTax);

		BigDecimal unitPurchaseAmount = amountBeforeTax.subtract(fixedCharge);

		if (unitPurchaseAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException(
					"Recharge amount is not sufficient after fixed charge and tax deduction");
		}

		List<TariffSlab> slabs = tariff.getSlabs().stream().sorted(Comparator.comparing(TariffSlab::getFromUnit))
				.toList();

		BigDecimal creditedUnits = calculateUnitsFromSlabs(unitPurchaseAmount, slabs);

		if (creditedUnits.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Credited units cannot be zero");
		}

		return PrepaidUnitCalculationResponseDto.builder().deviceId(device.getId())
				.deviceIdentifier(device.getDeviceId()).planId(plan.getId()).rechargeAmount(rechargeAmount)
				.tariffId(tariff.getId()).tariffName(tariff.getName()).fixedCharge(fixedCharge)
				.taxPercentage(taxPercentage).taxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP))
				.unitPurchaseAmount(unitPurchaseAmount.setScale(2, RoundingMode.HALF_UP))
				.creditedUnits(creditedUnits.setScale(3, RoundingMode.HALF_UP)).build();
	} 

	private BigDecimal calculateAmountBeforeTax(BigDecimal rechargeAmount, BigDecimal taxPercentage) {

		if (taxPercentage.compareTo(BigDecimal.ZERO) == 0) {
			return rechargeAmount;
		}

		BigDecimal divisor = BigDecimal.ONE.add(taxPercentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

		return rechargeAmount.divide(divisor, 6, RoundingMode.HALF_UP);
	}

	private BigDecimal calculateUnitsFromSlabs(BigDecimal availableAmount, List<TariffSlab> slabs) {

		BigDecimal remainingAmount = availableAmount;
		BigDecimal creditedUnits = BigDecimal.ZERO;

		for (TariffSlab slab : slabs) {

			validateSlab(slab);

			BigDecimal rate = slab.getRate();

			BigDecimal fromUnit = slab.getFromUnit();

			BigDecimal toUnit = slab.getToUnit();

			BigDecimal slabUnitLimit = toUnit.subtract(fromUnit);

			BigDecimal slabAmountLimit = slabUnitLimit.multiply(rate);

			if (remainingAmount.compareTo(slabAmountLimit) >= 0) {

				creditedUnits = creditedUnits.add(slabUnitLimit);

				remainingAmount = remainingAmount.subtract(slabAmountLimit);

			} else {

				BigDecimal partialUnits = remainingAmount.divide(rate, 6, RoundingMode.HALF_UP);

				creditedUnits = creditedUnits.add(partialUnits);

				remainingAmount = BigDecimal.ZERO;

				break;
			}
		}

		if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {

			TariffSlab lastSlab = slabs.get(slabs.size() - 1);

			BigDecimal lastRate = lastSlab.getRate();

			BigDecimal extraUnits = remainingAmount.divide(lastRate, 6, RoundingMode.HALF_UP);

			creditedUnits = creditedUnits.add(extraUnits);
		}

		return creditedUnits;
	}

	private void validateInputs(Device device, PrepaidRechargePlan plan, Tariff tariff) {

		if (device == null) {
			throw new IllegalArgumentException("Device is required for prepaid unit calculation");
		}

		if (plan == null) {
			throw new IllegalArgumentException("Prepaid recharge plan is required for unit calculation");
		}

		if (plan.getAmount() == null || plan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Recharge plan amount must be greater than zero");
		}

		if (tariff == null) {
			throw new IllegalArgumentException("Tariff is required for prepaid unit calculation");
		}

		if (tariff.getSlabs() == null || tariff.getSlabs().isEmpty()) {
			throw new IllegalArgumentException("Tariff slabs are not configured");
		}
	}

	private void validateSlab(TariffSlab slab) {

		if (slab.getFromUnit() == null) {
			throw new IllegalArgumentException("Tariff slab fromUnit is required");
		}

		if (slab.getToUnit() == null) {
			throw new IllegalArgumentException("Tariff slab toUnit is required");
		}

		if (slab.getRate() == null || slab.getRate().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Tariff slab rate must be greater than zero");
		}

		if (slab.getToUnit().compareTo(slab.getFromUnit()) <= 0) {
			throw new IllegalArgumentException("Tariff slab toUnit must be greater than fromUnit");
		}
	}

	private BigDecimal zeroIfNull(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value;
	}
}