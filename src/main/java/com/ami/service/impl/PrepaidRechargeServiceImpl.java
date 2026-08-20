package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.CreatePrepaidRechargeOrderRequestDto;
import com.ami.dto.requests.VerifyPrepaidRechargeRequestDto;
import com.ami.dto.responses.PrepaidBalanceResponseDto;
import com.ami.dto.responses.PrepaidRechargeOrderResponseDto;
import com.ami.dto.responses.PrepaidRechargeResponseDto;
import com.ami.dto.responses.PrepaidUnitCalculationResponseDto;
import com.ami.dto.responses.PrepaidUsageLedgerResponseDto;
import com.ami.entity.Device;
import com.ami.entity.PrepaidBalance;
import com.ami.entity.PrepaidRecharge;
import com.ami.entity.PrepaidRechargePlan;
import com.ami.entity.PrepaidUsageLedger;
import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.BillingType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.PaymentGateway;
import com.ami.enums.PrepaidBalanceStatus;
import com.ami.enums.PrepaidLedgerType;
import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.RechargeStatus;
import com.ami.enums.RoleType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PrepaidBalanceRepository;
import com.ami.repository.PrepaidRechargePlanRepository;
import com.ami.repository.PrepaidRechargeRepository;
import com.ami.repository.PrepaidUsageLedgerRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.PrepaidRechargeService;
import com.ami.service.PrepaidTariffResolverService;
import com.ami.service.PrepaidUnitCalculationService;
import com.ami.service.RazorpayService;
import com.ami.service.RazorpayService.RazorpayOrderResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrepaidRechargeServiceImpl implements PrepaidRechargeService {

	private final DeviceRepository deviceRepository;

	private final PrepaidRechargePlanRepository prepaidRechargePlanRepository;

	private final PrepaidRechargeRepository prepaidRechargeRepository;

	private final PrepaidTariffResolverService prepaidTariffResolverService;

	private final PrepaidUnitCalculationService prepaidUnitCalculationService;

	private final RazorpayService razorpayService;

	private final SecurityUtils securityUtils;

	private final PrepaidBalanceRepository prepaidBalanceRepository;

	private final PrepaidUsageLedgerRepository prepaidUsageLedgerRepository;

	@Override
	@Transactional
	public PrepaidRechargeOrderResponseDto createRechargeOrder(CreatePrepaidRechargeOrderRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(request.getDeviceId())
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + request.getDeviceId()));

		validatePrepaidDevice(device);

		validateDeviceAccess(device, loggedInUser);

		PrepaidRechargePlan plan = prepaidRechargePlanRepository.findById(request.getPlanId()).orElseThrow(
				() -> new ResourceNotFoundException("Prepaid recharge plan not found with id: " + request.getPlanId()));

		validatePlanForRecharge(plan, device);

		Tariff tariff = prepaidTariffResolverService.resolveTariffForPrepaidDevice(device);

		PrepaidUnitCalculationResponseDto calculation = prepaidUnitCalculationService.calculateUnits(device, plan,
				tariff);

		String rechargeNumber = generateRechargeNumber();

		RazorpayOrderResult orderResult = razorpayService.createOrder(rechargeNumber, plan.getAmount(),
				Map.of("type", "PREPAID_RECHARGE", "deviceId", String.valueOf(device.getId()), "deviceIdentifier",
						device.getDeviceId(), "planId", String.valueOf(plan.getId()), "tariffId",
						String.valueOf(tariff.getId()), "creditedUnits",
						calculation.getCreditedUnits().toPlainString()));

		PrepaidRecharge recharge = PrepaidRecharge.builder().rechargeNumber(rechargeNumber)
				.transactionId(orderResult.orderId()).razorpayOrderId(orderResult.orderId()).device(device).plan(plan)
				.user(device.getAssignedUser()).customerName(resolveCustomerName(device))
				.customerEmail(resolveCustomerEmail(device)).customerPhone(resolveCustomerPhone(device))
				.amount(plan.getAmount()).taxAmount(calculation.getTaxAmount())
				.netRechargeAmount(calculation.getUnitPurchaseAmount()).creditedUnits(calculation.getCreditedUnits())
				.sourceType(device.getMeter().getSourceType()).tariff(tariff).paymentMethod(request.getPaymentMethod())
				.paymentGateway(PaymentGateway.RAZORPAY).status(RechargeStatus.PENDING)
				.referenceNumber(orderResult.orderId()).remarks(null).rechargeDate(LocalDateTime.now())
				.createdBy(loggedInUser).build();

		recharge.setCreatedAt(LocalDateTime.now());
		recharge.setUpdatedAt(LocalDateTime.now());

		PrepaidRecharge savedRecharge = prepaidRechargeRepository.save(recharge);

		return PrepaidRechargeOrderResponseDto.builder().rechargeId(savedRecharge.getId())
				.rechargeNumber(savedRecharge.getRechargeNumber()).orderId(orderResult.orderId())
				.deviceId(device.getId()).deviceIdentifier(device.getDeviceId()).planId(plan.getId())
				.amount(plan.getAmount()).creditedUnits(calculation.getCreditedUnits()).tariffId(tariff.getId())
				.tariffName(tariff.getName()).fixedCharge(calculation.getFixedCharge())
				.taxAmount(calculation.getTaxAmount()).unitPurchaseAmount(calculation.getUnitPurchaseAmount())
				.currency(orderResult.currency()).gateway(PaymentGateway.RAZORPAY.name()).status(orderResult.status())
				.keyId(razorpayService.getKeyId()).build();
	}

	@Override
	@Transactional
	public PrepaidRechargeResponseDto verifyRechargePayment(VerifyPrepaidRechargeRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		PrepaidRecharge recharge = prepaidRechargeRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Prepaid recharge not found for Razorpay order id: " + request.getRazorpayOrderId()));

		validateDeviceAccess(recharge.getDevice(), loggedInUser);

		if (recharge.getStatus() != RechargeStatus.PENDING) {
			throw new IllegalArgumentException("Only pending prepaid recharge can be verified");
		}

		if (prepaidRechargeRepository.existsByRazorpayPaymentId(request.getRazorpayPaymentId())) {
			throw new IllegalArgumentException("Razorpay payment already verified");
		}

		boolean validSignature = razorpayService.verifySignature(request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(), request.getRazorpaySignature());

		if (!validSignature) {
			recharge.setStatus(RechargeStatus.FAILED);
			recharge.setRemarks("Invalid Razorpay signature");
			recharge.setUpdatedAt(LocalDateTime.now());
			prepaidRechargeRepository.save(recharge);

			throw new IllegalArgumentException("Invalid Razorpay payment signature");
		}

		recharge.setRazorpayPaymentId(request.getRazorpayPaymentId());
		recharge.setRazorpaySignature(request.getRazorpaySignature());
		recharge.setStatus(RechargeStatus.SUCCESS);
		recharge.setRemarks(normalizeText(request.getRemarks()));
		recharge.setUpdatedAt(LocalDateTime.now());

		PrepaidRecharge savedRecharge = prepaidRechargeRepository.save(recharge);

		PrepaidBalance balance = creditRechargeToBalance(savedRecharge);

		createRechargeCreditLedger(savedRecharge, balance);

		return mapToRechargeResponse(savedRecharge, balance);
	}

	private PrepaidBalance creditRechargeToBalance(PrepaidRecharge recharge) {

		Device device = recharge.getDevice();

		PrepaidBalance balance = prepaidBalanceRepository.findByDeviceForUpdate(device).orElseGet(() -> {
			PrepaidBalance newBalance = PrepaidBalance.builder().device(device).user(recharge.getUser())
					.totalRechargedAmount(BigDecimal.ZERO).totalCreditedUnits(BigDecimal.ZERO)
					.totalUsedUnits(BigDecimal.ZERO).availableUnits(BigDecimal.ZERO).consumptionBlocked(false)
					.lastMeterReading(null).status(PrepaidBalanceStatus.ACTIVE).lastRechargeAt(null)
					.lastConsumptionAt(null).build();

			newBalance.setCreatedAt(LocalDateTime.now());
			newBalance.setUpdatedAt(LocalDateTime.now());

			return newBalance;
		});

		BigDecimal currentTotalAmount = zeroIfNull(balance.getTotalRechargedAmount());
		BigDecimal currentCreditedUnits = zeroIfNull(balance.getTotalCreditedUnits());
		BigDecimal currentAvailableUnits = zeroIfNull(balance.getAvailableUnits());
		balance.setConsumptionBlocked(false);
		balance.setTotalRechargedAmount(currentTotalAmount.add(recharge.getAmount()));
		balance.setTotalCreditedUnits(currentCreditedUnits.add(recharge.getCreditedUnits()));
		balance.setAvailableUnits(currentAvailableUnits.add(recharge.getCreditedUnits()));
		balance.setStatus(PrepaidBalanceStatus.ACTIVE);
		balance.setLastRechargeAt(LocalDateTime.now());
		balance.setUpdatedAt(LocalDateTime.now());

		return prepaidBalanceRepository.save(balance);
	}

	private void createRechargeCreditLedger(PrepaidRecharge recharge, PrepaidBalance balance) {

		BigDecimal balanceAfter = zeroIfNull(balance.getAvailableUnits());

		BigDecimal balanceBefore = balanceAfter.subtract(recharge.getCreditedUnits());

		PrepaidUsageLedger ledger = PrepaidUsageLedger.builder().prepaidBalance(balance).device(recharge.getDevice())
				.ledgerType(PrepaidLedgerType.RECHARGE_CREDIT).units(recharge.getCreditedUnits())
				.readingBefore(balance.getLastMeterReading()).readingAfter(balance.getLastMeterReading())
				.balanceBefore(balanceBefore).balanceAfter(balanceAfter)
				.description("Prepaid recharge credited: " + recharge.getRechargeNumber()).build();

		ledger.setCreatedAt(LocalDateTime.now());
		ledger.setUpdatedAt(LocalDateTime.now());

		prepaidUsageLedgerRepository.save(ledger);
	}

	private PrepaidRechargeResponseDto mapToRechargeResponse(PrepaidRecharge recharge, PrepaidBalance balance) {

		return PrepaidRechargeResponseDto.builder().id(recharge.getId()).rechargeNumber(recharge.getRechargeNumber())
				.transactionId(recharge.getTransactionId())
				.deviceId(recharge.getDevice() != null ? recharge.getDevice().getId() : null)
				.deviceIdentifier(recharge.getDevice() != null ? recharge.getDevice().getDeviceId() : null)
				.userId(recharge.getUser() != null ? recharge.getUser().getId() : null)
				.customerName(recharge.getCustomerName()).customerEmail(recharge.getCustomerEmail())
				.amount(recharge.getAmount()).taxAmount(recharge.getTaxAmount())
				.netRechargeAmount(recharge.getNetRechargeAmount()).creditedUnits(recharge.getCreditedUnits())
				.sourceType(recharge.getSourceType())
				.tariffId(recharge.getTariff() != null ? recharge.getTariff().getId() : null)
				.tariffName(recharge.getTariff() != null ? recharge.getTariff().getName() : null)
				.paymentMethod(recharge.getPaymentMethod()).paymentGateway(recharge.getPaymentGateway())
				.status(recharge.getStatus()).referenceNumber(recharge.getReferenceNumber())
				.remarks(recharge.getRemarks()).rechargeDate(recharge.getRechargeDate())
				.totalCreditedUnits(balance != null ? balance.getTotalCreditedUnits() : null)
				.totalUsedUnits(balance != null ? balance.getTotalUsedUnits() : null)
				.availableUnits(balance != null ? balance.getAvailableUnits() : null)
				.receiptPreviewUrl(recharge.getId() != null
						? "/api/billing/prepaid/recharges/" + recharge.getId() + "/receipt/preview"
						: null)
				.receiptDownloadUrl(recharge.getId() != null
						? "/api/billing/prepaid/recharges/" + recharge.getId() + "/receipt/download"
						: null)
				.build();
	}

	private BigDecimal zeroIfNull(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value;
	}

	private String normalizeText(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private void validatePrepaidDevice(Device device) {

		if (device.getBillingType() != BillingType.PREPAID) {
			throw new IllegalArgumentException("Recharge is allowed only for prepaid devices");
		}

		if (device.getMeter() == null) {
			throw new IllegalStateException("Meter is not configured for this device");
		}

		if (device.getMeter().getSourceType() == null) {
			throw new IllegalStateException("Device source type is not configured");
		}

		if (device.getMeter().getStatus() != DeviceStatus.ACTIVE) {
			throw new IllegalArgumentException("Recharge is allowed only for active prepaid devices");
		}

		if (device.getAssignedUser() == null) {
			throw new IllegalStateException("User is not assigned to this prepaid device");
		}
	}

	private void validateDeviceAccess(Device device, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to recharge this prepaid device");
			}

			return;
		}

		if (loggedInUser.getRole() == RoleType.USER) {

			if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to recharge this prepaid device");
			}

			return;
		}

		throw new IllegalArgumentException("You are not allowed to recharge this prepaid device");
	}

	private void validatePlanForRecharge(PrepaidRechargePlan plan, Device device) {

		if (plan.getStatus() != PrepaidPlanStatus.ACTIVE) {
			throw new IllegalArgumentException("Selected prepaid recharge plan is not active");
		}

		if (plan.getSourceType() != device.getMeter().getSourceType()) {
			throw new IllegalArgumentException(
					"Selected prepaid recharge plan is not valid for this device source type");
		}

		if (plan.getCreatedBy() == null) {
			throw new IllegalStateException("Prepaid recharge plan creator is not configured");
		}

		if (plan.getCreatedBy().getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (device.getAssignedAdmin() != null
				&& plan.getCreatedBy().getId().equals(device.getAssignedAdmin().getId())) {
			return;
		}

		throw new IllegalArgumentException("Selected prepaid recharge plan is not allowed for this device");
	}

	private String generateRechargeNumber() {

		return UUID.randomUUID().toString();
	}

	private String resolveCustomerName(Device device) {

		if (device.getAssignedUser() != null && device.getAssignedUser().getEmail() != null) {
			return device.getAssignedUser().getEmail();
		}

		return device.getCustomerName();
	}

	private String resolveCustomerEmail(Device device) {

		return device.getAssignedUser() != null ? device.getAssignedUser().getEmail() : null;
	}

	private String resolveCustomerPhone(Device device) {

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public PrepaidBalanceResponseDto getPrepaidBalance(Long deviceId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		validatePrepaidDevice(device);

		validateDeviceAccess(device, loggedInUser);

		return prepaidBalanceRepository.findByDeviceForUpdate(device)
				.map(balance -> mapToBalanceResponse(device, balance))
				.orElseGet(() -> mapToEmptyBalanceResponse(device));
	}

	private PrepaidBalanceResponseDto mapToBalanceResponse(Device device, PrepaidBalance balance) {

		return PrepaidBalanceResponseDto.builder().deviceId(device.getId()).deviceIdentifier(device.getDeviceId())
				.userId(balance.getUser() != null ? balance.getUser().getId() : null)
				.customerName(resolveCustomerName(device)).totalRechargedAmount(balance.getTotalRechargedAmount())
				.totalCreditedUnits(balance.getTotalCreditedUnits()).totalUsedUnits(balance.getTotalUsedUnits())
				.availableUnits(balance.getAvailableUnits()).lastMeterReading(balance.getLastMeterReading())
				.status(balance.getStatus()).lastRechargeAt(balance.getLastRechargeAt())
				.lastConsumptionAt(balance.getLastConsumptionAt()).build();
	}

	private PrepaidBalanceResponseDto mapToEmptyBalanceResponse(Device device) {

		return PrepaidBalanceResponseDto.builder().deviceId(device.getId()).deviceIdentifier(device.getDeviceId())
				.userId(device.getAssignedUser() != null ? device.getAssignedUser().getId() : null)
				.customerName(resolveCustomerName(device)).totalRechargedAmount(BigDecimal.ZERO)
				.totalCreditedUnits(BigDecimal.ZERO).totalUsedUnits(BigDecimal.ZERO).availableUnits(BigDecimal.ZERO)
				.lastMeterReading(null).status(PrepaidBalanceStatus.EXHAUSTED).lastRechargeAt(null)
				.lastConsumptionAt(null).build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PrepaidUsageLedgerResponseDto> getPrepaidUsageHistory(Long deviceId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		validatePrepaidDevice(device);

		validateDeviceAccess(device, loggedInUser);

		return prepaidUsageLedgerRepository.findByDeviceOrderByCreatedAtDesc(device).stream()
				.map(this::mapToUsageLedgerResponse).toList();
	}

	private PrepaidUsageLedgerResponseDto mapToUsageLedgerResponse(PrepaidUsageLedger ledger) {

		return PrepaidUsageLedgerResponseDto.builder().id(ledger.getId())
				.deviceId(ledger.getDevice() != null ? ledger.getDevice().getId() : null)
				.deviceIdentifier(ledger.getDevice() != null ? ledger.getDevice().getDeviceId() : null)
				.ledgerType(ledger.getLedgerType()).units(ledger.getUnits()).readingBefore(ledger.getReadingBefore())
				.readingAfter(ledger.getReadingAfter()).balanceBefore(ledger.getBalanceBefore())
				.balanceAfter(ledger.getBalanceAfter()).description(ledger.getDescription())
				.createdAt(ledger.getCreatedAt()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PrepaidRechargeResponseDto> getDeviceRechargeHistory(Long deviceId, int page, int size, String search,
			RechargeStatus status, LocalDateTime fromDate, LocalDateTime toDate) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		validatePrepaidDevice(device);
		validateDeviceAccess(device, loggedInUser);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rechargeDate"));

		Page<PrepaidRecharge> recharges = prepaidRechargeRepository.findDeviceRechargesWithFilters(device,
				normalizeText(search), status, fromDate, toDate, pageable);

		PrepaidBalance balance = prepaidBalanceRepository.findByDevice(device).orElse(null);

		return recharges.map(recharge -> mapToRechargeResponse(recharge, balance));
	}

}