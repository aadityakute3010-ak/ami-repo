package com.ami.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.BillCalculationRequest;
import com.ami.dto.responses.BillCalculationResponseDto;
import com.ami.dto.responses.SlabCalculationResponseDto;
import com.ami.entity.Device;
import com.ami.entity.Tariff;
import com.ami.entity.TariffSlab;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.TariffCategoryResolver;
import com.ami.repository.DeviceRepository;
import com.ami.repository.TariffRepository;
import com.ami.repository.TariffSlabRepository;
import com.ami.service.BillingCalculatorService;
import com.ami.service.PayloadBillingReadingService;
import com.ami.dto.requests.PayloadBillCalculationRequest;
import com.ami.dto.responses.PayloadBillingReadingResponseDto;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingCalculatorServiceImpl implements BillingCalculatorService {

	private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int MONEY_SCALE = 2;

	private final TariffRepository tariffRepository;

	private final TariffSlabRepository tariffSlabRepository;

	private final PayloadBillingReadingService payloadBillingReadingService;

	private final DeviceRepository deviceRepository;

	private final TariffCategoryResolver tariffCategoryResolver;

	@Override
	@Transactional(readOnly = true)
	public BillCalculationResponseDto calculateBill(BillCalculationRequest request) {

		Tariff tariff = tariffRepository.findById(request.getTariffId())
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + request.getTariffId()));

		if (tariff.getStatus() != TariffStatus.ACTIVE) {
			throw new IllegalArgumentException("Selected tariff is inactive");
		}

		validateReadings(request.getPreviousReading(), request.getCurrentReading());

		BigDecimal previousDues = request.getPreviousDues() == null ? BigDecimal.ZERO : request.getPreviousDues();

		BigDecimal totalConsumption = request.getCurrentReading().subtract(request.getPreviousReading());

		List<TariffSlab> activeSlabs = tariffSlabRepository.findByTariff_IdAndStatusOrderByFromUnitAsc(tariff.getId(),
				TariffStatus.ACTIVE);

		List<SlabCalculationResponseDto> slabBreakdown = new ArrayList<>();

		BigDecimal consumptionAmount;

		boolean slabBased = !activeSlabs.isEmpty();

		if (slabBased) {

			consumptionAmount = calculateSlabAmount(totalConsumption, activeSlabs, slabBreakdown);

		} else {

			consumptionAmount = totalConsumption.multiply(tariff.getRate());
		}

		BigDecimal fixedCharge = defaultZero(tariff.getFixedCharge());

		BigDecimal taxPercentage = defaultZero(tariff.getTax());

		BigDecimal taxableAmount = consumptionAmount.add(fixedCharge);

		BigDecimal taxAmount = taxableAmount.multiply(taxPercentage).divide(ONE_HUNDRED, MONEY_SCALE,
				RoundingMode.HALF_UP);

		BigDecimal totalAmount = taxableAmount.add(taxAmount).add(previousDues);

		return BillCalculationResponseDto.builder().tariffId(tariff.getId()).tariffName(tariff.getName())
				.source(tariff.getSource()).category(tariff.getCategory()).unit(tariff.getUnit())
				.previousReading(scale(request.getPreviousReading())).currentReading(scale(request.getCurrentReading()))
				.totalConsumption(scale(totalConsumption)).baseRate(scale(tariff.getRate()))
				.consumptionAmount(scale(consumptionAmount)).fixedCharge(scale(fixedCharge))
				.taxableAmount(scale(taxableAmount)).taxPercentage(scale(taxPercentage)).taxAmount(scale(taxAmount))
				.previousDues(scale(previousDues)).totalAmount(scale(totalAmount)).slabBased(slabBased)
				.slabBreakdown(slabBreakdown).build();
	}

	private BigDecimal calculateSlabAmount(BigDecimal totalConsumption, List<TariffSlab> slabs,
			List<SlabCalculationResponseDto> breakdown) {

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (TariffSlab slab : slabs) {

			BigDecimal slabFrom = slab.getFromUnit();
			BigDecimal slabTo = slab.getToUnit();

			if (totalConsumption.compareTo(slabFrom) <= 0) {
				continue;
			}

			BigDecimal consumedUnits;

			if (slabTo == null) {

				consumedUnits = totalConsumption.subtract(slabFrom);

			} else {

				BigDecimal upperLimit = totalConsumption.min(slabTo);

				consumedUnits = upperLimit.subtract(slabFrom);
			}

			if (consumedUnits.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			BigDecimal slabAmount = consumedUnits.multiply(slab.getRate());

			totalAmount = totalAmount.add(slabAmount);

			breakdown.add(SlabCalculationResponseDto.builder().slabId(slab.getId()).from(scale(slabFrom))
					.to(slabTo == null ? null : scale(slabTo)).consumedUnits(scale(consumedUnits))
					.rate(scale(slab.getRate())).amount(scale(slabAmount)).build());
		}

		validateCompleteSlabCoverage(totalConsumption, slabs);

		return totalAmount;
	}

	private void validateCompleteSlabCoverage(BigDecimal totalConsumption, List<TariffSlab> slabs) {

		if (totalConsumption.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		BigDecimal expectedFrom = BigDecimal.ZERO;

		for (TariffSlab slab : slabs) {

			if (slab.getFromUnit().compareTo(expectedFrom) != 0) {

				throw new IllegalArgumentException("Active tariff slabs contain a gap");
			}

			if (slab.getToUnit() == null) {
				return;
			}

			expectedFrom = slab.getToUnit();

			if (totalConsumption.compareTo(expectedFrom) <= 0) {
				return;
			}
		}

		if (totalConsumption.compareTo(expectedFrom) > 0) {
			throw new IllegalArgumentException("Tariff slabs do not cover total consumption");
		}
	}

	private void validateReadings(BigDecimal previousReading, BigDecimal currentReading) {

		if (previousReading == null) {
			throw new IllegalArgumentException("Previous reading is required");
		}

		if (currentReading == null) {
			throw new IllegalArgumentException("Current reading is required");
		}

		if (previousReading.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Previous reading cannot be negative");
		}

		if (currentReading.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Current reading cannot be negative");
		}

		if (currentReading.compareTo(previousReading) < 0) {
			throw new IllegalArgumentException("Current reading cannot be less than previous reading");
		}
	}

	private BigDecimal defaultZero(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value;
	}

	private BigDecimal scale(BigDecimal value) {

		if (value == null) {
			return null;
		}

		return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
	}

	@Override
	@Transactional(readOnly = true)
	public BillCalculationResponseDto calculateBillFromPayload(PayloadBillCalculationRequest request) {

		Device device = deviceRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + request.getDeviceId()));

		if (device.getMeter() == null) {
			throw new IllegalStateException("Meter not configured for device");
		}

		Tariff tariff = tariffRepository.findById(request.getTariffId())
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + request.getTariffId()));

		if (tariff.getStatus() != TariffStatus.ACTIVE) {
			throw new IllegalArgumentException("Selected tariff is inactive");
		}

		if (device.getMeter().getSourceType() != tariff.getSource()) {
			throw new IllegalArgumentException("Selected tariff source does not match device source type");
		}

		TariffCategory deviceCategory = tariffCategoryResolver
				.resolveFromApplication(device.getMeter().getApplication());

		if (tariff.getCategory() != deviceCategory) {
			throw new IllegalArgumentException(
					"Selected tariff category does not match meter application. Required category: " + deviceCategory);
		}

		LocalDate billingPeriodFrom = parseDate(request.getBillingPeriodFrom(), "billingPeriodFrom");
		LocalDate billingPeriodTo = parseDate(request.getBillingPeriodTo(), "billingPeriodTo");

		validateCompletedBillingMonth(billingPeriodFrom, billingPeriodTo);

		PayloadBillingReadingResponseDto readings = payloadBillingReadingService
				.getBillingReadings(request.getDeviceId(), billingPeriodFrom, billingPeriodTo);

		BillCalculationRequest calculationRequest = new BillCalculationRequest();

		calculationRequest.setTariffId(request.getTariffId());
		calculationRequest.setPreviousReading(readings.getPreviousReading());
		calculationRequest.setCurrentReading(readings.getCurrentReading());
		calculationRequest.setPreviousDues(request.getPreviousDues());

		return calculateBill(calculationRequest);
	}

	private LocalDate parseDate(String date, String fieldName) {

		if (date == null || date.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format");
		}
	}

	private void validateCompletedBillingMonth(LocalDate from, LocalDate to) {

		if (from.isAfter(to)) {
			throw new IllegalArgumentException("Billing period from date cannot be after to date");
		}

		if (!from.equals(from.withDayOfMonth(1))) {
			throw new IllegalArgumentException("Billing period from date must be first day of month");
		}

		if (!to.equals(to.withDayOfMonth(to.lengthOfMonth()))) {
			throw new IllegalArgumentException("Billing period to date must be last day of month");
		}

		if (from.getMonth() != to.getMonth() || from.getYear() != to.getYear()) {
			throw new IllegalArgumentException("Billing period must be within the same month");
		}

		// commented for testing purpose later switch on for actual working
//		if (!to.isBefore(LocalDate.now())) {
//			throw new IllegalArgumentException("Billing can be calculated only for completed month");
//		}
	}

}