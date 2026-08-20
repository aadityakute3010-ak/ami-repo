package com.ami.controller;

import com.ami.dto.requests.AdminUpdateUserRequestDto;
import com.ami.dto.requests.CreateUserRequest;
import com.ami.dto.requests.UpdateProfileRequestDto;
import com.ami.dto.requests.UserFilterRequestDto;
import com.ami.dto.responses.AdminUpdateUserResponseDto;
import com.ami.dto.responses.BulkUploadResponseDto;
import com.ami.dto.responses.CreateUserResponseDto;
import com.ami.dto.responses.CreationOptionsResponse;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.MyInfoResponseDto;
import com.ami.dto.responses.PagedUserResponseDto;
import com.ami.dto.responses.UserDashboardResponseDto;
import com.ami.dto.responses.UserDetailsResponseDto;
import com.ami.dto.responses.UserListResponseDto;
import com.ami.dto.responses.UserMapMarkerDto;
import com.ami.enums.DeleteType;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import com.ami.service.LocationService;
import com.ami.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final LocationService locationService;

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

	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId, @RequestParam DeleteType deleteType,
			@RequestParam(required = false) StatusType status) {
		return ResponseEntity.ok(userService.deleteUser(userId, deleteType, status));
	}

	@GetMapping("/getUsers")
	public ResponseEntity<PagedUserResponseDto> getUsers(UserFilterRequestDto request) {
		return ResponseEntity.ok(userService.getUsers(request));
	}

	// to get Admins for assigning to Device
	@GetMapping("/eligible-admins")
	public ResponseEntity<List<UserListResponseDto>> getEligibleAdmins(@RequestParam SourceType sourceType,
			@RequestParam(required = false) String search) {
		return ResponseEntity.ok(userService.getEligibleAdminsBySource(sourceType, search));
	}

	// to get user for assigning to Device
	@GetMapping("/eligible-users")
	public ResponseEntity<List<UserListResponseDto>> getEligibleUsers(@RequestParam Long adminId,
			@RequestParam SourceType sourceType) {
		return ResponseEntity.ok(userService.getEligibleUsers(adminId, sourceType));
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	@GetMapping("/dashboard")
	public UserDashboardResponseDto getUserDashboard() {
		return userService.getDashboard();
	}

	@PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
	public BulkUploadResponseDto bulkUploadUsers(@RequestParam("file") MultipartFile file) {
		return userService.bulkUploadUsers(file);
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> exportUsers(@RequestParam(defaultValue = "csv") String fileType,
			@RequestParam(required = false) String keyword, @RequestParam(required = false) RoleType role,
			@RequestParam(required = false) StatusType status, @RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate) {

		ExportFileResponseDto exportFile = userService.exportUsers(fileType, keyword, role, status, fromDate, toDate);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + exportFile.getFileName())
				.contentType(MediaType.parseMediaType(exportFile.getContentType())).body(exportFile.getFile());
	}

	@GetMapping("/map-markers")
	public ResponseEntity<List<UserMapMarkerDto>> getUserMapMarkers() {
		return ResponseEntity.ok(userService.getUserMapMarkers());
	}

	@PostMapping("/locations/backfill/users")
	public ResponseEntity<String> backfillUserLocations() {
		locationService.backfillUserLocations();
		return ResponseEntity.ok("User locations backfilled successfully");
	}

}