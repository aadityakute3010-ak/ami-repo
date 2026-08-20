package com.ami.controller;

import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.entity.User;
import com.ami.service.EngineerService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequestMapping("/api/engineers")
public class EngineerController {

	private final EngineerService engineerService;

	public EngineerController(EngineerService engineerService) {
		this.engineerService = engineerService;
	}

	@GetMapping
	public List<User> getEngineers() {
		return engineerService.getEngineers();
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	@GetMapping("/{engineerId}")
	public User getEngineerById(@PathVariable Long engineerId) {
		return engineerService.getEngineerById(engineerId);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	@GetMapping("/available")
	public List<User> getAvailableEngineers() {

		return engineerService.getAvailableEngineers();
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	@GetMapping("/{engineerId}/workload")
	public EngineerWorkloadResponseDto getWorkload(@PathVariable Long engineerId) {

		return engineerService.getWorkload(engineerId);
	}

	@GetMapping("/dashboard/{engineerId}")
	public EngineerDashboardResponseDto getDashboard(@PathVariable Long engineerId) {

		return engineerService.getDashboard(engineerId);
	}

}