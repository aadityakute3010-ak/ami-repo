package com.ami.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ami.dto.requests.CreateTariffRequest;
import com.ami.dto.requests.CreateTariffSlabRequest;
import com.ami.dto.requests.UpdateTariffRequest;
import com.ami.dto.requests.UpdateTariffSlabRequest;
import com.ami.dto.responses.TariffResponseDto;
import com.ami.dto.responses.TariffSlabResponseDto;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;
import com.ami.service.TariffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/tariffs")
@RequiredArgsConstructor
public class TariffController {

	private final TariffService tariffService;

	@PostMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<TariffResponseDto> createTariff(@Valid @RequestBody CreateTariffRequest request) {

		TariffResponseDto response = tariffService.createTariff(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<List<TariffResponseDto>> getAllTariffs(@RequestParam(required = false) SourceType source,
			@RequestParam(required = false) TariffCategory category,
			@RequestParam(required = false) TariffStatus status, @RequestParam(required = false) String search) {

		return ResponseEntity.ok(tariffService.getAllTariffs(source, category, status, search));
	}

	@GetMapping("/{tariffId}")
	public ResponseEntity<TariffResponseDto> getTariffById(@PathVariable Long tariffId) {

		return ResponseEntity.ok(tariffService.getTariffById(tariffId));
	}

	@PutMapping("/{tariffId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<TariffResponseDto> updateTariff(@PathVariable Long tariffId,
			@Valid @RequestBody UpdateTariffRequest request) {

		return ResponseEntity.ok(tariffService.updateTariff(tariffId, request));
	}

	@DeleteMapping("/{tariffId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<Map<String, String>> deleteTariff(@PathVariable Long tariffId) {

		tariffService.deleteTariff(tariffId);

		return ResponseEntity.ok(Map.of("message", "Tariff deleted successfully"));
	}

	@PostMapping("/{tariffId}/slabs")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<TariffSlabResponseDto> createSlab(@PathVariable Long tariffId,
			@Valid @RequestBody CreateTariffSlabRequest request) {

		TariffSlabResponseDto response = tariffService.createSlab(tariffId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{tariffId}/slabs")
	public ResponseEntity<List<TariffSlabResponseDto>> getSlabs(@PathVariable Long tariffId) {

		return ResponseEntity.ok(tariffService.getSlabs(tariffId));
	}

	@PutMapping("/{tariffId}/slabs/{slabId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<TariffSlabResponseDto> updateSlab(@PathVariable Long tariffId, @PathVariable Long slabId,
			@Valid @RequestBody UpdateTariffSlabRequest request) {

		return ResponseEntity.ok(tariffService.updateSlab(tariffId, slabId, request));
	}

	@DeleteMapping("/{tariffId}/slabs/{slabId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<Map<String, String>> deleteSlab(@PathVariable Long tariffId, @PathVariable Long slabId) {

		tariffService.deleteSlab(tariffId, slabId);

		return ResponseEntity.ok(Map.of("message", "Tariff slab deleted successfully"));
	}
}