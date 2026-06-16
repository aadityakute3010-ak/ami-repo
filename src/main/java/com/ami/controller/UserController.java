package com.ami.controller;

import com.ami.dto.requests.AdminUpdateUserRequestDto;
import com.ami.dto.requests.CreateUserRequest;
import com.ami.dto.requests.UpdateProfileRequestDto;
import com.ami.dto.responses.AdminUpdateUserResponseDto;
import com.ami.dto.responses.CreateUserResponseDto;
import com.ami.dto.responses.CreationOptionsResponse;
import com.ami.dto.responses.MyInfoResponseDto;
import com.ami.dto.responses.PagedUserResponseDto;
import com.ami.dto.responses.UserDetailsResponseDto;
import com.ami.dto.responses.UserListResponseDto;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// CREATE USER / ADMIN / SERVICE ENGINEER
	@PostMapping("/createAccount")
	public CreateUserResponseDto createUser(@Valid @RequestBody CreateUserRequest request) {
		return userService.createUser(request);
	}

	// DYNAMIC FRONTEND OPTIONS
	@GetMapping("/creation-options")
	public CreationOptionsResponse getCreationOptions() {
		return userService.getCreationOptions();
	}

	@GetMapping("/getAllUsers")
	public PagedUserResponseDto getUsers(@RequestParam(required = false) RoleType role,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return userService.getUsers(role, page, size);
	}

	@GetMapping("/{userId}")
	public UserDetailsResponseDto getUserDetails(@PathVariable Long userId) {
		return userService.getUserDetails(userId);
	}

	@GetMapping("/my-info")
	public MyInfoResponseDto getMyInfo() {
		return userService.getMyInfo();
	}

	@GetMapping("/admin-update/{userId}")
	public AdminUpdateUserResponseDto getUserForAdminUpdate(@PathVariable Long userId) {
		return userService.getUserForAdminUpdate(userId);
	}

	@PutMapping("/update-profile")
	public UserDetailsResponseDto updateProfile(@Valid @RequestBody UpdateProfileRequestDto request) {
		return userService.updateProfile(request);
	}

	@PutMapping("/admin-update/{userId}")
	public AdminUpdateUserResponseDto adminUpdateUser(@Valid @PathVariable Long userId,
			@RequestBody AdminUpdateUserRequestDto request) {
		return userService.adminUpdateUser(userId, request);
	}

	@DeleteMapping("/soft-delete/{userId}")
	public String softDeleteUser(@PathVariable Long userId) {
		return userService.softDeleteUser(userId);
	}

	@DeleteMapping("/hard-delete/{userId}")
	public String hardDeleteUser(@PathVariable Long userId) {
		return userService.hardDeleteUser(userId);
	}

	@GetMapping("/searchUser")
	public PagedUserResponseDto searchUsers(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return userService.searchUsers(keyword, page, size);
	}

	@GetMapping("/status")
	public PagedUserResponseDto getUsersByStatus(@RequestParam Boolean active,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return userService.getUsersByStatus(active, page, size);
	}

	// to get Admins for assigning to Device
	@GetMapping("/eligible-admins")
	public ResponseEntity<List<UserListResponseDto>> getEligibleAdmins(@RequestParam SourceType sourceType,
			@RequestParam(required = false) String search) {
		return ResponseEntity.ok(userService.getEligibleAdminsBySource(sourceType, search));
	} 
 
} 