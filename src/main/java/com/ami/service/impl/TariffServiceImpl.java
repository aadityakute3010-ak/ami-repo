package com.ami.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.requests.CreateTariffRequest;
import com.ami.dto.requests.CreateTariffSlabRequest;
import com.ami.dto.requests.UpdateTariffRequest;
import com.ami.dto.requests.UpdateTariffSlabRequest;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.dto.responses.TariffSlabResponseDto;
import com.ami.entity.Tariff;
import com.ami.entity.TariffSlab;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.exception.ResourceNotFoundException;
import com.ami.mapper.TariffMapper;
import com.ami.repository.TariffRepository;
import com.ami.repository.TariffSlabRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.AuditService;
import com.ami.service.TariffService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

	private final TariffRepository tariffRepository;

	private final TariffSlabRepository tariffSlabRepository;

	private final TariffMapper tariffMapper;

	private final SecurityUtils securityUtils;

	private final AuditService auditService;

	@Override
	@Transactional
	public TariffResponseDto createTariff(CreateTariffRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		validateDuplicateTariff(loggedInUser, request.getName(), request.getSource(), request.getCategory(), null);

		Tariff tariff = Tariff.builder().name(request.getName().trim()).source(request.getSource())
				.category(request.getCategory()).unit(request.getUnit().trim()).rate(request.getRate())
				.fixedCharge(request.getFixedCharge()).tax(request.getTax()).status(request.getStatus())
				.description(normalizeDescription(request.getDescription())).createdBy(loggedInUser).build();

		Tariff savedTariff = tariffRepository.save(tariff);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(savedTariff.getId());
		auditRequest.setEntityType("TARIFF");
		auditRequest.setTargetAdminId(resolveTargetAdminId(savedTariff.getCreatedBy()));
		auditRequest.setAction("CREATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tariff '" + savedTariff.getName() + "' created");
		auditService.createAuditLog(auditRequest);

		return tariffMapper.toTariffResponse(savedTariff);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TariffResponseDto> getAllTariffs(SourceType source, TariffCategory category, TariffStatus status,
			String search) {

		String normalizedSearch = null;

		if (search != null && !search.isBlank()) {
			normalizedSearch = search.trim();
		}

		return tariffRepository.findWithFilters(source, category, status, normalizedSearch).stream()
				.map(tariffMapper::toTariffResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public TariffResponseDto getTariffById(Long tariffId) {

		Tariff tariff = findTariffOrThrow(tariffId);

		return tariffMapper.toTariffResponse(tariff);
	}

	@Override
	@Transactional
	public TariffResponseDto updateTariff(Long tariffId, UpdateTariffRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Tariff tariff = findTariffOrThrow(tariffId);

		validateDuplicateTariff(tariff.getCreatedBy(), request.getName(), request.getSource(), request.getCategory(),
				tariffId);
		tariff.setName(request.getName().trim());
		tariff.setSource(request.getSource());
		tariff.setCategory(request.getCategory());
		tariff.setUnit(request.getUnit().trim());
		tariff.setRate(request.getRate());
		tariff.setFixedCharge(request.getFixedCharge());
		tariff.setTax(request.getTax());
		tariff.setStatus(request.getStatus());
		tariff.setDescription(normalizeDescription(request.getDescription()));
		Tariff updatedTariff = tariffRepository.save(tariff);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(updatedTariff.getId());
		auditRequest.setEntityType("TARIFF");
		auditRequest.setTargetAdminId(resolveTargetAdminId(updatedTariff.getCreatedBy()));
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tariff '" + updatedTariff.getName() + "' updated");

		auditService.createAuditLog(auditRequest);

		return tariffMapper.toTariffResponse(updatedTariff);
	}

	@Override
	@Transactional
	public void deleteTariff(Long tariffId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Tariff tariff = findTariffOrThrow(tariffId);

		tariff.setStatus(TariffStatus.INACTIVE);

		tariffRepository.save(tariff);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(tariff.getId());
		auditRequest.setEntityType("TARIFF");
		auditRequest.setTargetAdminId(resolveTargetAdminId(tariff.getCreatedBy()));
		auditRequest.setAction("DEACTIVATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tariff '" + tariff.getName() + "' deactivated");
		auditService.createAuditLog(auditRequest);
	}

	@Override
	@Transactional
	public TariffSlabResponseDto createSlab(Long tariffId, CreateTariffSlabRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Tariff tariff = findTariffOrThrow(tariffId);

		validateSlabRange(tariffId, null, request.getFrom(), request.getTo());

		TariffSlab slab = TariffSlab.builder().tariff(tariff).fromUnit(request.getFrom()).toUnit(request.getTo())
				.rate(request.getRate()).fixedCharge(request.getFixedCharge()).tax(request.getTax())
				.status(request.getStatus()).description(normalizeDescription(request.getDescription())).build();

		TariffSlab savedSlab = tariffSlabRepository.save(slab);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(savedSlab.getId());
		auditRequest.setEntityType("TARIFF_SLAB");
		auditRequest.setTargetAdminId(resolveTargetAdminId(tariff.getCreatedBy()));
		auditRequest.setAction("CREATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tariff slab created for tariff '" + tariff.getName() + "' with range "
				+ savedSlab.getFromUnit() + " - " + savedSlab.getToUnit());
		auditService.createAuditLog(auditRequest);

		return tariffMapper.toSlabResponse(savedSlab);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TariffSlabResponseDto> getSlabs(Long tariffId) {

		findTariffOrThrow(tariffId);

		return tariffSlabRepository.findByTariff_IdOrderByFromUnitAsc(tariffId).stream()
				.map(tariffMapper::toSlabResponse).toList();
	}

	@Override
	@Transactional
	public TariffSlabResponseDto updateSlab(Long tariffId, Long slabId, UpdateTariffSlabRequest request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Tariff tariff = findTariffOrThrow(tariffId);

		TariffSlab slab = findSlabOrThrow(tariffId, slabId);

		validateSlabRange(tariffId, slabId, request.getFrom(), request.getTo());

		slab.setFromUnit(request.getFrom());

		slab.setToUnit(request.getTo());

		slab.setRate(request.getRate());

		slab.setFixedCharge(request.getFixedCharge());

		slab.setTax(request.getTax());

		slab.setStatus(request.getStatus());

		slab.setDescription(normalizeDescription(request.getDescription()));

		TariffSlab updatedSlab = tariffSlabRepository.save(slab);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(updatedSlab.getId());
		auditRequest.setEntityType("TARIFF_SLAB");
		auditRequest.setTargetAdminId(resolveTargetAdminId(tariff.getCreatedBy()));
		auditRequest.setAction("UPDATED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription("Tariff slab ID " + updatedSlab.getId() + " updated for tariff ID " + tariffId);
		auditService.createAuditLog(auditRequest);

		return tariffMapper.toSlabResponse(updatedSlab);
	}

	@Override
	@Transactional
	public void deleteSlab(Long tariffId, Long slabId) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Tariff tariff = findTariffOrThrow(tariffId);

		TariffSlab slab = findSlabOrThrow(tariffId, slabId);

		String description = "Tariff slab ID " + slab.getId() + " deleted from tariff '" + tariff.getName() + "'"
				+ " (range: " + slab.getFromUnit() + " - " + slab.getToUnit() + ")";

		tariffSlabRepository.delete(slab);

		CreateAuditLogRequestDto auditRequest = new CreateAuditLogRequestDto();

		auditRequest.setModule("BILLING");
		auditRequest.setEntityId(slabId);
		auditRequest.setEntityType("TARIFF_SLAB");
		auditRequest.setTargetAdminId(resolveTargetAdminId(tariff.getCreatedBy()));
		auditRequest.setAction("DELETED");
		auditRequest.setPerformedBy(loggedInUser.getEmail());
		auditRequest.setDescription(description);

		auditService.createAuditLog(auditRequest);
	}

	private Tariff findTariffOrThrow(Long tariffId) {

		return tariffRepository.findById(tariffId)
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + tariffId));
	}

	private TariffSlab findSlabOrThrow(Long tariffId, Long slabId) {

		TariffSlab slab = tariffSlabRepository.findById(slabId)
				.orElseThrow(() -> new ResourceNotFoundException("Tariff slab not found with id: " + slabId));

		if (slab.getTariff() == null || !tariffId.equals(slab.getTariff().getId())) {

			throw new ResourceNotFoundException("Tariff slab " + slabId + " does not belong to tariff " + tariffId);
		}

		return slab;
	}

	private void validateDuplicateTariff(User createdBy, String name, SourceType source, TariffCategory category,
			Long excludedTariffId) {
		String normalizedName = name.trim();
		boolean duplicate;
		if (excludedTariffId == null) {
			duplicate = tariffRepository.existsByCreatedByAndNameIgnoreCaseAndSourceAndCategoryAndStatus(createdBy,
					normalizedName, source, category, TariffStatus.ACTIVE);
		} else {
			duplicate = tariffRepository.existsByCreatedByAndNameIgnoreCaseAndSourceAndCategoryAndStatusAndIdNot(
					createdBy, normalizedName, source, category, TariffStatus.ACTIVE, excludedTariffId);
		}
		if (duplicate) {
			throw new IllegalArgumentException(
					"An active tariff already exists with the same name, source and category for this admin");
		}
	}

	private void validateSlabRange(Long tariffId, Long excludedSlabId, BigDecimal from, BigDecimal to) {

		if (from == null) {
			throw new IllegalArgumentException("Slab from range is required");
		}

		if (from.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Slab from range cannot be negative");
		}

		if (to != null && to.compareTo(from) <= 0) {

			throw new IllegalArgumentException("Slab to range must be greater than from range");
		}

		boolean overlapping = tariffSlabRepository.existsOverlappingSlab(tariffId, from, to, excludedSlabId);

		if (overlapping) {
			throw new IllegalArgumentException("Tariff slab range overlaps with an existing slab");
		}
	}

	private String normalizeDescription(String description) {

		if (description == null || description.isBlank()) {
			return null;
		}

		return description.trim();
	}
	
	private Long resolveTargetAdminId(User owner) {
	    if (owner == null || owner.getRole() != RoleType.ADMIN) {
	        return null;
	    }
	    return owner.getId();
	}
}