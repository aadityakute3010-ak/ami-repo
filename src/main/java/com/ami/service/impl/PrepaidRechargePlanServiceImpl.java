package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.CreatePrepaidRechargePlanRequestDto;
import com.ami.dto.requests.UpdatePrepaidRechargePlanRequestDto;
import com.ami.dto.responses.PagedPrepaidRechargePlanResponseDto;
import com.ami.dto.responses.PrepaidRechargePlanResponseDto;
import com.ami.entity.BillingSettings;
import com.ami.entity.Device;
import com.ami.entity.PrepaidRechargePlan;
import com.ami.entity.User;
import com.ami.enums.BillingType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PrepaidRechargePlanRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.BillingSettingsService;
import com.ami.service.PrepaidRechargePlanService;
import lombok.RequiredArgsConstructor;
import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.service.AuditService;

@Service
@RequiredArgsConstructor
public class PrepaidRechargePlanServiceImpl implements PrepaidRechargePlanService {

	private final PrepaidRechargePlanRepository prepaidRechargePlanRepository;

	private final DeviceRepository deviceRepository;

	private final SecurityUtils securityUtils;

	private final BillingSettingsService billingSettingsService;

	private final AuditService auditService;

	@Override
	@Transactional
	public PrepaidRechargePlanResponseDto createPlan(CreatePrepaidRechargePlanRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePlanWriteAccess(loggedInUser);

		validateMinimumRechargeAmount(request.getAmount(), loggedInUser);

		if (prepaidRechargePlanRepository.existsByAmountAndSourceTypeAndStatus(request.getAmount(),
				request.getSourceType(), PrepaidPlanStatus.ACTIVE)) {
			throw new IllegalArgumentException(
					"Active prepaid recharge plan already exists for this amount and source type");
		}

		PrepaidRechargePlan plan = PrepaidRechargePlan.builder().planName(request.getPlanName().trim())
				.amount(request.getAmount()).sourceType(request.getSourceType()).status(PrepaidPlanStatus.ACTIVE)
				.description(normalizeText(request.getDescription())).createdBy(loggedInUser).build();

		plan.setCreatedAt(LocalDateTime.now());
		plan.setUpdatedAt(LocalDateTime.now());

		PrepaidRechargePlan savedPlan = prepaidRechargePlanRepository.save(plan);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(savedPlan.getId());
		auditRequest.setEntityType("RECHARGE_PLAN");
		auditRequest.setTargetAdminId(resolveTargetAdminId(savedPlan.getCreatedBy()));
		auditRequest.setAction("CREATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Prepaid recharge plan '" + savedPlan.getPlanName() + "' created for "
				+ savedPlan.getSourceType() + " with amount " + savedPlan.getAmount());
		auditService.createAuditLog(auditRequest);

		return mapToResponse(savedPlan);
	}

	@Override
	@Transactional
	public PrepaidRechargePlanResponseDto updatePlan(Long planId, UpdatePrepaidRechargePlanRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePlanWriteAccess(loggedInUser);

		validateMinimumRechargeAmount(request.getAmount(), loggedInUser);

		PrepaidRechargePlan plan = prepaidRechargePlanRepository.findById(planId)
				.orElseThrow(() -> new ResourceNotFoundException("Prepaid recharge plan not found with id: " + planId));

		validatePlanOwnershipForWrite(plan, loggedInUser);

		boolean duplicateActivePlan = prepaidRechargePlanRepository.existsByAmountAndSourceTypeAndStatus(
				request.getAmount(), request.getSourceType(), PrepaidPlanStatus.ACTIVE);

		boolean samePlanAmountAndSource = plan.getAmount().compareTo(request.getAmount()) == 0
				&& plan.getSourceType() == request.getSourceType();

		if (duplicateActivePlan && !samePlanAmountAndSource) {
			throw new IllegalArgumentException(
					"Active prepaid recharge plan already exists for this amount and source type");
		}

		plan.setPlanName(request.getPlanName().trim());
		plan.setAmount(request.getAmount());
		plan.setSourceType(request.getSourceType());
		plan.setDescription(normalizeText(request.getDescription()));
		plan.setUpdatedAt(LocalDateTime.now());

		PrepaidRechargePlan updatedPlan = prepaidRechargePlanRepository.save(plan);
		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(updatedPlan.getId());
		auditRequest.setEntityType("RECHARGE_PLAN");
		auditRequest.setTargetAdminId(resolveTargetAdminId(updatedPlan.getCreatedBy()));
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Prepaid recharge plan '" + updatedPlan.getPlanName() + "' updated");
		auditService.createAuditLog(auditRequest);
		return mapToResponse(updatedPlan);
	}

	@Override
	@Transactional(readOnly = true)
	public PrepaidRechargePlanResponseDto getPlanById(Long planId) {

		PrepaidRechargePlan plan = prepaidRechargePlanRepository.findById(planId)
				.orElseThrow(() -> new ResourceNotFoundException("Prepaid recharge plan not found with id: " + planId));

		return mapToResponse(plan);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedPrepaidRechargePlanResponseDto getPlans(int page, int size, String search, SourceType sourceType,
			PrepaidPlanStatus status) {

		String normalizedSearch = search == null || search.isBlank() ? null : search.trim();

		Pageable pageable = PageRequest.of(page, size);

		User loggedInUser = securityUtils.getLoggedInUser();

		User creatorFilter = null;

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			creatorFilter = loggedInUser;
		}

		Page<PrepaidRechargePlan> planPage = prepaidRechargePlanRepository
				.findPlansWithFiltersAndCreator(normalizedSearch, sourceType, status, creatorFilter, pageable);

		return PagedPrepaidRechargePlanResponseDto.builder()
				.plans(planPage.getContent().stream().map(this::mapToResponse).toList())
				.currentPage(planPage.getNumber()).totalPages(planPage.getTotalPages())
				.totalElements(planPage.getTotalElements()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PrepaidRechargePlanResponseDto> getActivePlansForPrepaidDevice(Long deviceId) {

		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

		validatePrepaidDevice(device);

		User loggedInUser = securityUtils.getLoggedInUser();

		validateDeviceAccess(device, loggedInUser);

		SourceType sourceType = device.getMeter().getSourceType();

		User allowedAdmin = resolveAllowedAdminForPlans(device, loggedInUser);

		return prepaidRechargePlanRepository
				.findActivePlansForAdminOrUserDevice(sourceType, PrepaidPlanStatus.ACTIVE, allowedAdmin).stream()
				.map(this::mapToResponse).toList();
	}

	private User resolveAllowedAdminForPlans(Device device, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return null;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return loggedInUser;
		}

		if (loggedInUser.getRole() == RoleType.USER) {

			if (device.getAssignedAdmin() == null) {
				throw new IllegalStateException("Assigned admin is not configured for this prepaid device");
			}

			return device.getAssignedAdmin();
		}

		throw new IllegalArgumentException("You are not allowed to access prepaid recharge plans");
	}

	@Override
	@Transactional
	public PrepaidRechargePlanResponseDto updatePlanStatus(Long planId, PrepaidPlanStatus status) {

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePlanWriteAccess(loggedInUser);

		PrepaidRechargePlan plan = prepaidRechargePlanRepository.findById(planId)
				.orElseThrow(() -> new ResourceNotFoundException("Prepaid recharge plan not found with id: " + planId));

		validatePlanOwnershipForWrite(plan, loggedInUser);

		PrepaidPlanStatus previousStatus = plan.getStatus();

		plan.setStatus(status);
		plan.setUpdatedAt(LocalDateTime.now());

		PrepaidRechargePlan updatedPlan = prepaidRechargePlanRepository.save(plan);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(updatedPlan.getId());

		String action;

		if (status == PrepaidPlanStatus.ACTIVE) {
			action = "ACTIVATED";
		} else if (status == PrepaidPlanStatus.INACTIVE) {
			action = "DEACTIVATED";
		} else {
			action = "STATUS_UPDATED";
		}

		auditRequest.setAction(action);
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setEntityType("RECHARGE_PLAN");
		auditRequest.setTargetAdminId(resolveTargetAdminId(updatedPlan.getCreatedBy()));
		auditRequest.setDescription("Prepaid recharge plan '" + updatedPlan.getPlanName() + "' status changed from "
				+ previousStatus + " to " + updatedPlan.getStatus());

		auditService.createAuditLog(auditRequest);

		return mapToResponse(updatedPlan);
	}

	@Override
	@Transactional
	public void deletePlan(Long planId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePlanWriteAccess(loggedInUser);

		PrepaidRechargePlan plan = prepaidRechargePlanRepository.findById(planId)
				.orElseThrow(() -> new ResourceNotFoundException("Prepaid recharge plan not found with id: " + planId));

		validatePlanOwnershipForWrite(plan, loggedInUser);

		Long deletedPlanId = plan.getId();
		String deletedPlanName = plan.getPlanName();
		SourceType deletedSourceType = plan.getSourceType();
		BigDecimal deletedAmount = plan.getAmount();
		Long deletedTargetAdminId = resolveTargetAdminId(plan.getCreatedBy());

		prepaidRechargePlanRepository.delete(plan);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();
		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(deletedPlanId);
		auditRequest.setEntityType("RECHARGE_PLAN");
		auditRequest.setTargetAdminId(deletedTargetAdminId);
		auditRequest.setAction("DELETED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Prepaid recharge plan '" + deletedPlanName + "' deleted for " + deletedSourceType
				+ " with amount " + deletedAmount);
		auditService.createAuditLog(auditRequest);
	}

	private void validatePlanWriteAccess(User loggedInUser) {

		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN && loggedInUser.getRole() != RoleType.ADMIN) {
			throw new IllegalArgumentException("You are not allowed to manage prepaid recharge plans");
		}
	}

	private void validateMinimumRechargeAmount(BigDecimal amount, User loggedInUser) {

		BillingSettings billingSettings = billingSettingsService
				.getSettingsForAdmin(loggedInUser.getRole() == RoleType.ADMIN ? loggedInUser : null);

		BigDecimal minimumRechargeAmount = billingSettings.getPrepaidMinimumRechargeAmount();

		if (minimumRechargeAmount == null) {
			throw new IllegalStateException("Prepaid minimum recharge amount is not configured");
		}

		if (amount.compareTo(minimumRechargeAmount) < 0) {
			throw new IllegalArgumentException("Minimum prepaid recharge amount is " + minimumRechargeAmount);
		}
	}

	private void validatePrepaidDevice(Device device) {

		if (device.getBillingType() != BillingType.PREPAID) {
			throw new IllegalArgumentException("Prepaid plans are available only for prepaid devices");
		}

		if (device.getMeter() == null || device.getMeter().getSourceType() == null) {
			throw new IllegalStateException("Device source type is not configured");
		}

		if (device.getMeter().getStatus() != DeviceStatus.ACTIVE) {
			throw new IllegalArgumentException("Prepaid plans are available only for active devices");
		}
	}

	private void validateDeviceAccess(Device device, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to access this prepaid device");
			}

			return;
		}

		if (loggedInUser.getRole() == RoleType.USER) {

			if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to access this prepaid device");
			}

			return;
		}

		throw new IllegalArgumentException("You are not allowed to access this prepaid device");
	}

	private PrepaidRechargePlanResponseDto mapToResponse(PrepaidRechargePlan plan) {

		return PrepaidRechargePlanResponseDto.builder().id(plan.getId()).planName(plan.getPlanName())
				.amount(plan.getAmount()).sourceType(plan.getSourceType()).status(plan.getStatus())
				.description(plan.getDescription())
				.createdById(plan.getCreatedBy() != null ? plan.getCreatedBy().getId() : null)
				.createdByName(plan.getCreatedBy() != null ? plan.getCreatedBy().getEmail() : null)
				.createdAt(plan.getCreatedAt()).updatedAt(plan.getUpdatedAt()).build();
	}

	private String normalizeText(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private void validatePlanOwnershipForWrite(PrepaidRechargePlan plan, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (plan.getCreatedBy() == null || !plan.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to modify this prepaid recharge plan");
			}

			return;
		}

		throw new IllegalArgumentException("You are not allowed to modify prepaid recharge plans");
	}
	
	private Long resolveTargetAdminId(User owner) {
	    if (owner == null || owner.getRole() != RoleType.ADMIN) {
	        return null;
	    }
	    return owner.getId();
	}

}