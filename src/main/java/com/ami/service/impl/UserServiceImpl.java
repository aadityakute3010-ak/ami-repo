package com.ami.service.impl;

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
import com.ami.entity.Device;
import com.ami.entity.User;
import com.ami.entity.UserLocation;
import com.ami.enums.DeleteType;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import com.ami.repository.DeviceRepository;
import com.ami.repository.PasswordResetTokenRepository;
import com.ami.repository.UserLocationRepository;
import com.ami.repository.UserRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.LocationService;
import com.ami.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Data
@Getter
@Setter
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final DeviceRepository deviceRepository;

	private final PasswordEncoder passwordEncoder;

	private final PasswordResetTokenRepository passwordResetTokenRepository;

	private final LocationService locationService;

	private final UserLocationRepository userLocationRepository;

	@Autowired
	private SecurityUtils securityUtils;

	private Set<SourceType> resolveAssignedSources(Set<SourceType> requestedSources) {
		if (requestedSources == null || requestedSources.isEmpty()) {
			return requestedSources;
		}
		if (requestedSources.contains(SourceType.ALL)) {
			return Arrays.stream(SourceType.values()).filter(source -> source != SourceType.ALL)
					.collect(Collectors.toSet());
		}
		return requestedSources;
	}

	private LocalDate parseDate(String date, String fieldName) {

		if (date == null || date.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format");
		}
	}

	@Override
	public CreateUserResponseDto createUser(CreateUserRequest request) {

		// Get Logged in User
		User creator = securityUtils.getLoggedInUser();

		// USER CANNOT CREATE ANYONE
		if (creator.getRole() == RoleType.USER) {
			throw new RuntimeException("User cannot create users");
		}

		// SERVICE ENGINEER CANNOT CREATE ANYONE
		if (creator.getRole() == RoleType.SERVICE_ENGINEER) {
			throw new RuntimeException("Service Engineer cannot create users");
		}

		// ADMIN CANNOT CREATE SUPER ADMIN
		if (creator.getRole() == RoleType.ADMIN && request.getRole() == RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Admin cannot create Super Admin");
		}

		// EMAIL VALIDATION
		String email = request.getEmail().trim();
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("Email already exists");
		}

		// USERNAME VALIDATION
		if (userRepository.existsByUserName(email)) {
			throw new RuntimeException("Username already exists");
		}

		// SOURCE VALIDATION
		Set<SourceType> resolvedSources = resolveAssignedSources(request.getAssignedSources());
		validateSourceAssignment(creator, resolvedSources);

		// CREATE USER
		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUserName(email);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhoneNo(request.getPhoneNo());
		user.setAddress(request.getAddress());
		user.setState(request.getState());
		user.setCity(request.getCity());
		user.setRole(request.getRole());
		user.setStatus(request.getStatus());
		user.setAssignedSources(resolvedSources);
		// TRACK WHO CREATED USER
		if (creator.getRole() == RoleType.SUPER_ADMIN && request.getRole() == RoleType.USER
				&& request.getAdminId() != null) {
			User admin = userRepository.findById(request.getAdminId())
					.orElseThrow(() -> new RuntimeException("Admin not found"));
			if (admin.getRole() != RoleType.ADMIN) {
				throw new RuntimeException("Selected user is not an Admin");
			}
			user.setCreatedBy(admin);
		} else {
			user.setCreatedBy(creator);
		}
		User savedUser = userRepository.save(user);
		locationService.saveOrUpdateUserLocation(savedUser);
		return mapToCreateUserResponse(savedUser);
	}

	private CreateUserResponseDto mapToCreateUserResponse(User user) {

		CreateUserResponseDto response = new CreateUserResponseDto();
		response.setId(user.getId());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setUserName(user.getUserName());
		response.setEmail(user.getEmail());
		response.setPhoneNo(user.getPhoneNo());
		response.setAddress(user.getAddress());
		response.setState(user.getState());
		response.setCity(user.getCity());
		response.setRole(user.getRole());
		response.setStatus(user.getStatus());
		response.setAssignedSources(user.getAssignedSources());
		response.setCreatedAt(user.getCreatedAt());
		return response;
	}

	@Override
	public CreationOptionsResponse getCreationOptions() {

		User loggedInUser = securityUtils.getLoggedInUser();
		CreationOptionsResponse response = new CreationOptionsResponse();

		// SUPER ADMIN
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			response.setAllowedRoles(
					Set.of(RoleType.SUPER_ADMIN, RoleType.ADMIN, RoleType.USER, RoleType.SERVICE_ENGINEER));
			response.setAllowedSources(Set.of(SourceType.values()));
			return response;
		}

		// ADMIN
		if (loggedInUser.getRole() == RoleType.ADMIN) {
			response.setAllowedRoles(Set.of(RoleType.ADMIN, RoleType.USER, RoleType.SERVICE_ENGINEER));
			Set<SourceType> allowedSources = new HashSet<>(loggedInUser.getAssignedSources());
			boolean hasAllSources = allowedSources.contains(SourceType.WATER)
					&& allowedSources.contains(SourceType.ENERGY) && allowedSources.contains(SourceType.GAS)
					&& allowedSources.contains(SourceType.SOLAR);
			if (hasAllSources) {
				allowedSources.add(SourceType.ALL);
			}
			response.setAllowedSources(allowedSources);
			return response;
		}

		// USER / SERVICE ENGINEER
		response.setAllowedRoles(Set.of());
		response.setAllowedSources(Set.of());
		return response;
	}

	private void validateSourceAssignment(User creator, Set<SourceType> requestedSources) {

		if (creator.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (requestedSources == null || requestedSources.isEmpty()) {
			return;
		}

		Set<SourceType> creatorSources = creator.getAssignedSources();

		for (SourceType source : requestedSources) {

			if (source == SourceType.ALL) {
				continue;
			}
			if (!creatorSources.contains(source)) {
				throw new RuntimeException("Cannot assign unauthorized source: " + source);
			}
		}
	}

	@Override
	public PagedUserResponseDto getUsers(UserFilterRequestDto request) {

		User loggedInUser = securityUtils.getLoggedInUser();

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

		LocalDateTime fromDateTime = request.getFromDate() != null ? request.getFromDate().atStartOfDay() : null;

		LocalDateTime toDateTime = request.getToDate() != null ? request.getToDate().atTime(23, 59, 59) : null;

		Page<User> userPage;

		// SUPER ADMIN
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			userPage = userRepository.findUsersWithFilters(loggedInUser.getId(), request.getKeyword(),
					request.getRole(), request.getStatus(), fromDateTime, toDateTime, pageable);
		}

		// ADMIN
		else if (loggedInUser.getRole() == RoleType.ADMIN) {

			userPage = userRepository.findUsersWithFiltersForAdmin(loggedInUser.getId(), loggedInUser.getId(),
					request.getKeyword(), request.getRole(), request.getStatus(), fromDateTime, toDateTime, pageable);
		}

		else {
			throw new RuntimeException("Access Denied");
		}

		PagedUserResponseDto response = new PagedUserResponseDto();

		response.setUsers(userPage.getContent().stream().map(this::mapToUserListResponse).toList());
		response.setCurrentPage(userPage.getNumber());
		response.setTotalPages(userPage.getTotalPages());
		response.setTotalElements(userPage.getTotalElements());
		return response;
	}

	private UserListResponseDto mapToUserListResponse(User user) {

		UserListResponseDto dto = new UserListResponseDto();
		dto.setId(user.getId());
		dto.setFullName(user.getFirstName() + " " + user.getLastName());
		dto.setEmail(user.getEmail());
		dto.setPhoneNo(user.getPhoneNo());
		dto.setAssignedSources(user.getAssignedSources());
		dto.setStatus(user.getStatus());
		dto.setRole(user.getRole());

		return dto;
	}

	@Override
	public UserDetailsResponseDto getUserDetails(Long userId) {

		// GET LOGGED IN USER
		User loggedInUser = securityUtils.getLoggedInUser();

		// FIND TARGET USER
		User targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Target user not found"));

		// SUPER ADMIN CAN ACCESS ALL
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return mapToUserDetailsResponse(targetUser);
		}

		// ADMIN CAN ACCESS ONLY USERS CREATED BY HIM
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			return mapToUserDetailsResponse(targetUser);
		}

		// USER CAN ACCESS ONLY SELF
		if (loggedInUser.getRole() == RoleType.USER) {

			if (!loggedInUser.getId().equals(targetUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			return mapToUserDetailsResponse(targetUser);
		}

		if (loggedInUser.getRole() == RoleType.SERVICE_ENGINEER) {
			if (!loggedInUser.getId().equals(targetUser.getId())) {
				throw new RuntimeException("Access Denied");
			}
			return mapToUserDetailsResponse(targetUser);
		}

		throw new RuntimeException("Access Denied");
	}

	private UserDetailsResponseDto mapToUserDetailsResponse(User user) {

		UserDetailsResponseDto dto = new UserDetailsResponseDto();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setUserName(user.getUserName());
		dto.setEmail(user.getEmail());
		dto.setPhoneNo(user.getPhoneNo());
		dto.setAddress(user.getAddress());
		dto.setState(user.getState());
		dto.setCity(user.getCity());
		dto.setRole(user.getRole());
		dto.setStatus(user.getStatus());
		dto.setAssignedSources(user.getAssignedSources());
		List<Device> assignedDevices = deviceRepository.findByAssignedUserId(user.getId());
		List<String> assignedDeviceNames = assignedDevices.stream().map(Device::getDeviceName).toList();
		List<String> assignedMeterNames = assignedDevices.stream().filter(device -> device.getMeter() != null)
				.map(device -> device.getMeter().getMeterName()).toList();
		dto.setAssignedDevices(assignedDeviceNames);
		dto.setAssignedMeters(assignedMeterNames);
		// ACTIVITY DETAILS
		if (user.getCreatedBy() != null) {
			dto.setCreatedBy(user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName());
			dto.setAdminId(user.getCreatedBy().getId());
			dto.setAdminName(user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName());
		}
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}

	// for user updating form
	@Override
	public MyInfoResponseDto getMyInfo() {
		User user = securityUtils.getLoggedInUser();
		return mapToMyInfoResponse(user);
	}

	private MyInfoResponseDto mapToMyInfoResponse(User user) {

		MyInfoResponseDto dto = new MyInfoResponseDto();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setUserName(user.getUserName());
		dto.setEmail(user.getEmail());
		dto.setPhoneNo(user.getPhoneNo());
		dto.setAddress(user.getAddress());
		dto.setState(user.getState());
		dto.setCity(user.getCity());

		return dto;
	}

	private AdminUpdateUserResponseDto mapToAdminUpdateUserResponse(User user) {

		AdminUpdateUserResponseDto dto = new AdminUpdateUserResponseDto();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setUserName(user.getUserName());
		dto.setEmail(user.getEmail());
		dto.setPhoneNo(user.getPhoneNo());
		dto.setAddress(user.getAddress());
		dto.setState(user.getState());
		dto.setCity(user.getCity());
		dto.setRole(user.getRole());
		dto.setStatus(user.getStatus());
		dto.setAssignedSources(user.getAssignedSources());
		if (user.getCreatedBy() != null) {
			dto.setAdminId(user.getCreatedBy().getId());
			dto.setAdminName(user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName());
		}
		return dto;
	}

	// for admin updating any user
	@Override
	public AdminUpdateUserResponseDto getUserForAdminUpdate(Long userId) {

		User admin = securityUtils.getLoggedInUser();
		User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// SUPER ADMIN ACCESS
		if (admin.getRole() == RoleType.SUPER_ADMIN) {
			return mapToAdminUpdateUserResponse(targetUser);
		}

		// ADMIN CANNOT UPDATE SUPER ADMIN OR OTHER ADMINS
		if (targetUser.getRole() == RoleType.SUPER_ADMIN || targetUser.getRole() == RoleType.ADMIN) {
			throw new RuntimeException("You cannot access this user");
		}

		// ADMIN'S OWN USERS
		if (targetUser.getRole() == RoleType.USER) {
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(admin.getId())) {
				throw new RuntimeException("You cannot access this user");
			}
		}

		// SERVICE ENGINEER
		if (targetUser.getRole() == RoleType.SERVICE_ENGINEER) {
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(admin.getId())) {
				throw new RuntimeException("You cannot access this engineer");
			}
		}

		return mapToAdminUpdateUserResponse(targetUser);
	}

	@Override
	public UserDetailsResponseDto updateProfile(UpdateProfileRequestDto request) {

		User user = securityUtils.getLoggedInUser();

		// UPDATE ONLY PROVIDED FIELDS
		if (request.getFirstName() != null) {
			user.setFirstName(request.getFirstName());
		}

		if (request.getLastName() != null) {
			user.setLastName(request.getLastName());
		}

		if (request.getEmail() != null && !request.getEmail().trim().equals(user.getEmail())) {
			String email = request.getEmail().trim();
			if (userRepository.existsByEmail(email)) {
				throw new RuntimeException("Email already exists");
			}
			if (userRepository.existsByUserName(email)) {
				throw new RuntimeException("Username already exists");
			}
			user.setEmail(email);
			user.setUserName(email);
		}

		if (request.getPhoneNo() != null) {
			user.setPhoneNo(request.getPhoneNo());
		}

		if (request.getAddress() != null) {
			user.setAddress(request.getAddress());
		}

		if (request.getState() != null) {
			user.setState(request.getState());
		}

		if (request.getCity() != null) {
			user.setCity(request.getCity());
		}

		User updatedUser = userRepository.save(user);
		locationService.saveOrUpdateUserLocation(updatedUser);
		locationService.updateAssignedDeviceLocationsForUser(updatedUser);

		return mapToUserDetailsResponse(updatedUser);
	}

	@Override
	public AdminUpdateUserResponseDto adminUpdateUser(Long userId, AdminUpdateUserRequestDto request) {

		User admin = securityUtils.getLoggedInUser();

		User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// SUPER ADMIN CAN UPDATE ANYONE
		if (admin.getRole() != RoleType.SUPER_ADMIN) {
			// ADMIN CANNOT UPDATE SUPER ADMIN OR OTHER ADMINS
			if (targetUser.getRole() == RoleType.SUPER_ADMIN || targetUser.getRole() == RoleType.ADMIN) {
				throw new RuntimeException("You cannot update this user");
			}

			// ADMIN CAN UPDATE OWN USERS
			if (targetUser.getRole() == RoleType.USER) {
				if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(admin.getId())) {
					throw new RuntimeException("You cannot update this user");
				}
			}

			// ADMIN CAN UPDATE ONLY OWN CREATED ENGINEERS
			if (targetUser.getRole() == RoleType.SERVICE_ENGINEER) {
				if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(admin.getId())) {
					throw new RuntimeException("You cannot update this engineer");
				}
			}
		}
		if (targetUser.getCreatedBy() != null && request.getAdminId() != null && targetUser.getRole() == RoleType.USER
				&& !targetUser.getCreatedBy().getId().equals(request.getAdminId())) {
			throw new RuntimeException("Assigned Admin cannot be changed.");

		}
		// UPDATE FIELDS
		targetUser.setFirstName(request.getFirstName());
		targetUser.setLastName(request.getLastName());
		if (request.getEmail() != null) {
			String email = request.getEmail().trim().toLowerCase();
			if (userRepository.existsEmailForOtherUser(email, targetUser.getId())) {
				throw new RuntimeException("Email already exists");
			}
			if (userRepository.existsUserNameForOtherUser(email, targetUser.getId())) {
				throw new RuntimeException("Username already exists");
			}
			targetUser.setEmail(email);
			targetUser.setUserName(email);
		}
		targetUser.setPhoneNo(request.getPhoneNo());
		targetUser.setAddress(request.getAddress());
		targetUser.setState(request.getState());
		targetUser.setCity(request.getCity());
		if (request.getStatus() != null) {
			targetUser.setStatus(request.getStatus());
		}
		// ADMIN CAN ASSIGN ONLY OWN SOURCES
		if (request.getAssignedSources() != null && !request.getAssignedSources().isEmpty()) {
			Set<SourceType> resolvedSources = resolveAssignedSources(request.getAssignedSources());
			validateSourceAssignment(admin, resolvedSources);
			targetUser.setAssignedSources(resolvedSources);
		}

		if (admin.getRole() == RoleType.SUPER_ADMIN && targetUser.getRole() == RoleType.USER
				&& targetUser.getCreatedBy() == null && request.getAdminId() != null) {

			User assignedAdmin = userRepository.findById(request.getAdminId())
					.orElseThrow(() -> new RuntimeException("Admin not found"));

			targetUser.setCreatedBy(assignedAdmin);
		}

		User updatedUser = userRepository.save(targetUser);
		locationService.saveOrUpdateUserLocation(updatedUser);
		locationService.updateAssignedDeviceLocationsForUser(updatedUser);
		return mapToAdminUpdateUserResponse(updatedUser);
	}

	@Transactional
	@Override
	public String deleteUser(Long userId, DeleteType deleteType, StatusType status) {

		User loggedInUser = securityUtils.getLoggedInUser();

		User targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Target user not found"));

		// SUPER ADMIN DELETE RULES
		if (targetUser.getRole() == RoleType.SUPER_ADMIN) {

			if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
				throw new RuntimeException("You cannot update Super Admin");
			}

			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot update this Super Admin");
			}
		}

		// NON SUPER ADMIN RULES
		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {

			if (loggedInUser.getRole() != RoleType.ADMIN) {
				throw new RuntimeException("Access Denied");
			}

			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot update this user");
			}
		}

		if (deleteType == DeleteType.SOFT) {

			if (status == null) {
				throw new RuntimeException("Status is required for soft delete");
			}

			if (status != StatusType.ACTIVE && status != StatusType.INACTIVE) {
				throw new RuntimeException("Only ACTIVE or INACTIVE status is allowed");
			}

			targetUser.setStatus(status);
			userRepository.save(targetUser);

			return status == StatusType.ACTIVE ? "User activated successfully" : "User deactivated successfully";
		}

		if (deleteType == DeleteType.HARD) {

			if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
				throw new RuntimeException("Only Super Admin can permanently delete users");
			}

			passwordResetTokenRepository.deleteByUserId(targetUser.getId());
			userLocationRepository.deleteById(targetUser.getId());

			userRepository.delete(targetUser);

			return "User deleted permanently";
		}

		throw new RuntimeException("Invalid delete type");
	}

	@Override
	public List<UserListResponseDto> getEligibleAdminsBySource(SourceType sourceType, String search) {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can view eligible admins");
		}

		List<User> admins = userRepository.findEligibleAdmins(RoleType.ADMIN, sourceType, search);

		return admins.stream().map(this::mapToUserListResponseDto).toList();
	}

	@Override
	public List<UserListResponseDto> getEligibleUsers(Long adminId, SourceType sourceType) {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (sourceType == null) {
			throw new RuntimeException("Source type is required");
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			adminId = loggedInUser.getId();
		}

		else if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Access Denied");
		}

		List<User> users = userRepository.findEligibleUsersByAdminAndSource(adminId, sourceType);

		return users.stream().map(this::mapToUserListResponseDto).toList();
	}

	private UserListResponseDto mapToUserListResponseDto(User user) {

		return UserListResponseDto.builder().id(user.getId())
				.fullName(user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""))
				.email(user.getEmail()).phoneNo(user.getPhoneNo()).assignedSources(user.getAssignedSources())
				.address(user.getAddress()).city(user.getCity()).state(user.getState()).status(user.getStatus())
				.role(user.getRole()).build();
	}

	@Override
	public UserDashboardResponseDto getDashboard() {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return buildSuperAdminDashboard();
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			return buildAdminDashboard(loggedInUser.getId());
		}

		throw new RuntimeException("Access Denied");
	}

	private UserDashboardResponseDto buildSuperAdminDashboard() {

		LocalDateTime start = LocalDate.now().atStartOfDay();

		long totalUsers = userRepository.count();
		long activeUsers = userRepository.countByStatus(StatusType.ACTIVE);
		long inactiveUsers = userRepository.countByStatus(StatusType.INACTIVE);
		long admins = userRepository.countByRole(RoleType.ADMIN);
		long engineers = userRepository.countByRole(RoleType.SERVICE_ENGINEER);
		long normalUsers = userRepository.countByRole(RoleType.USER);
		long superAdmins = userRepository.countByRole(RoleType.SUPER_ADMIN);
		long assignedUsers = userRepository.countAssignedUsers();
		long totalUsersToday = userRepository.countCreatedToday(start);
		long activeUsersToday = userRepository.countCreatedTodayByStatus(StatusType.ACTIVE, start);
		long inactiveUsersToday = userRepository.countCreatedTodayByStatus(StatusType.INACTIVE, start);
		long adminsToday = userRepository.countCreatedTodayByRole(RoleType.ADMIN, start);
		long engineersToday = userRepository.countCreatedTodayByRole(RoleType.SERVICE_ENGINEER, start);
		long normalUsersToday = userRepository.countCreatedTodayByRole(RoleType.USER, start);
		long superAdminsToday = userRepository.countCreatedTodayByRole(RoleType.SUPER_ADMIN, start);
		long assignedUsersToday = userRepository.countAssignedUsersCreatedToday(start);
		return UserDashboardResponseDto.builder().totalUsers(totalUsers)
				.totalUsersPercentage(calculatePercentage(totalUsersToday, totalUsers)).activeUsers(activeUsers)
				.activeUsersPercentage(calculatePercentage(activeUsersToday, activeUsers)).inactiveUsers(inactiveUsers)
				.inactiveUsersPercentage(calculatePercentage(inactiveUsersToday, inactiveUsers)).admins(admins)
				.adminsPercentage(calculatePercentage(adminsToday, admins)).engineers(engineers)
				.engineersPercentage(calculatePercentage(engineersToday, engineers)).normalUsers(normalUsers)
				.normalUsersPercentage(calculatePercentage(normalUsersToday, normalUsers)).superAdmins(superAdmins)
				.superAdminsPercentage(calculatePercentage(superAdminsToday, superAdmins)).assignedUsers(assignedUsers)
				.assignedUsersPercentage(calculatePercentage(assignedUsersToday, assignedUsers)).build();
	}

	private UserDashboardResponseDto buildAdminDashboard(Long adminId) {

		LocalDateTime start = LocalDate.now().atStartOfDay();
		long totalUsers = userRepository.countByCreatedBy(adminId);
		long activeUsers = userRepository.countByCreatedByAndStatus(adminId, StatusType.ACTIVE);
		long inactiveUsers = userRepository.countByCreatedByAndStatus(adminId, StatusType.INACTIVE);
		long engineers = userRepository.countByCreatedByAndRole(adminId, RoleType.SERVICE_ENGINEER);
		long normalUsers = userRepository.countByCreatedByAndRole(adminId, RoleType.USER);
		long assignedUsers = userRepository.countAssignedUsersByAdmin(adminId);
		long totalUsersToday = userRepository.countCreatedTodayByAdmin(adminId, start);
		long activeUsersToday = userRepository.countCreatedTodayByAdminAndStatus(adminId, StatusType.ACTIVE, start);
		long inactiveUsersToday = userRepository.countCreatedTodayByAdminAndStatus(adminId, StatusType.INACTIVE, start);
		long engineersToday = userRepository.countCreatedTodayByAdminAndRole(adminId, RoleType.SERVICE_ENGINEER, start);
		long normalUsersToday = userRepository.countCreatedTodayByAdminAndRole(adminId, RoleType.USER, start);
		long assignedUsersToday = userRepository.countAssignedUsersCreatedTodayByAdmin(adminId, start);
		return UserDashboardResponseDto.builder().totalUsers(totalUsers)
				.totalUsersPercentage(calculatePercentage(totalUsersToday, totalUsers)).activeUsers(activeUsers)
				.activeUsersPercentage(calculatePercentage(activeUsersToday, activeUsers)).inactiveUsers(inactiveUsers)
				.inactiveUsersPercentage(calculatePercentage(inactiveUsersToday, inactiveUsers)).engineers(engineers)
				.engineersPercentage(calculatePercentage(engineersToday, engineers)).normalUsers(normalUsers)
				.normalUsersPercentage(calculatePercentage(normalUsersToday, normalUsers)).assignedUsers(assignedUsers)
				.assignedUsersPercentage(calculatePercentage(assignedUsersToday, assignedUsers)).build();
	}

	private double calculatePercentage(long todayCount, long totalCount) {
		if (totalCount == 0) {
			return 0.0;
		}
		return Math.round(((double) todayCount / totalCount) * 100 * 100.0) / 100.0;
	}

	@Override
	@Transactional
	public BulkUploadResponseDto bulkUploadUsers(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		if (fileName == null) {
			throw new RuntimeException("Invalid file");
		}
		if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
			return processExcel(file);
		}
		if (fileName.endsWith(".csv")) {
			return processCsv(file);
		}
		if (fileName.endsWith(".pdf")) {
			return processPdf(file);
		}
		throw new RuntimeException("Only XLSX, XLS, CSV and PDF files are allowed");
	}

	private BulkUploadResponseDto processExcel(MultipartFile file) {

		List<String> errors = new ArrayList<>();

		int success = 0;
		int failed = 0;
		int total = 0;

		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

			Sheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				total++;

				try {

					Row row = sheet.getRow(i);

					CreateUserRequest request = mapExcelRow(row);

					createUser(request);

					success++;

				} catch (Exception e) {

					failed++;

					errors.add("Row " + (i + 1) + " : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to read Excel file");
		}

		return BulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private BulkUploadResponseDto processCsv(MultipartFile file) {

		List<String> errors = new ArrayList<>();

		int success = 0;
		int failed = 0;
		int total = 0;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

			for (CSVRecord record : csvParser) {
				total++;
				try {
					CreateUserRequest request = mapCsvRecord(record);
					createUser(request);
					success++;
				} catch (Exception e) {
					failed++;
					errors.add("Row " + record.getRecordNumber() + " : " + e.getMessage());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to read CSV file");
		}

		return BulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private BulkUploadResponseDto processPdf(MultipartFile file) {

		List<String> errors = new ArrayList<>();
		int success = 0;
		int failed = 0;
		int total = 0;

		try (PDDocument document = PDDocument.load(file.getInputStream())) {

			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			String[] lines = text.split("\\r?\\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();
				if (line.isBlank()) {
					continue;
				}
				// Skip header line
				if (line.toLowerCase().startsWith("firstname|lastname|email")) {
					continue;
				}
				total++;
				try {
					CreateUserRequest request = mapPdfLine(line);
					createUser(request);
					success++;

				} catch (Exception e) {
					failed++;
					errors.add("Line " + (i + 1) + " : " + e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to read PDF file");
		}
		return BulkUploadResponseDto.builder().totalRecords(total).successCount(success).failedCount(failed)
				.errors(errors).build();
	}

	private CreateUserRequest mapPdfLine(String line) {

		CreateUserRequest request = new CreateUserRequest();
		String[] values = line.split("\\|", -1);

		if (values.length < 11) {
			throw new RuntimeException("Invalid PDF row format. Expected 11 columns");
		}
		request.setFirstName(values[0].trim());
		request.setLastName(values[1].trim());
		// Username and email will be same
		String email = values[2].trim();
		request.setEmail(email);
		request.setUserName(email);
		request.setPassword(values[3].trim());
		request.setPhoneNo(values[4].trim());
		request.setAddress(values[5].trim());
		request.setState(values[6].trim());
		request.setCity(values[7].trim());
		request.setRole(RoleType.valueOf(values[8].trim().toUpperCase()));
		request.setStatus(StatusType.valueOf(values[9].trim().toUpperCase()));
		request.setAssignedSources(Arrays.stream(values[10].split(",")).map(String::trim).map(String::toUpperCase)
				.map(SourceType::valueOf).collect(Collectors.toSet()));
		return request;
	}

	private CreateUserRequest mapExcelRow(Row row) {

		CreateUserRequest request = new CreateUserRequest();

		request.setFirstName(row.getCell(0).getStringCellValue());
		request.setLastName(row.getCell(1).getStringCellValue());
		String email = row.getCell(2).getStringCellValue().trim();
		// Username and email will be same
		request.setEmail(email);
		request.setUserName(email);
		request.setPassword(row.getCell(3).getStringCellValue());
		request.setPhoneNo(row.getCell(4).getStringCellValue());
		request.setAddress(row.getCell(5).getStringCellValue());
		request.setState(row.getCell(6).getStringCellValue());
		request.setCity(row.getCell(7).getStringCellValue());
		request.setRole(RoleType.valueOf(row.getCell(8).getStringCellValue().trim().toUpperCase()));
		request.setStatus(StatusType.valueOf(row.getCell(9).getStringCellValue().trim().toUpperCase()));
		String sources = row.getCell(10).getStringCellValue();
		request.setAssignedSources(Arrays.stream(sources.split(",")).map(String::trim).map(String::toUpperCase)
				.map(SourceType::valueOf).collect(Collectors.toSet()));
		return request;
	}

	private CreateUserRequest mapCsvRecord(CSVRecord record) {

		CreateUserRequest request = new CreateUserRequest();
		String email = record.get("email").trim();

		request.setFirstName(record.get("firstName"));
		request.setLastName(record.get("lastName"));
		// Username and email will be same
		request.setEmail(email);
		request.setUserName(email);
		request.setPassword(record.get("password"));
		request.setPhoneNo(record.get("phoneNo"));
		request.setAddress(record.get("address"));
		request.setState(record.get("state"));
		request.setCity(record.get("city"));
		request.setRole(RoleType.valueOf(record.get("role").trim().toUpperCase()));
		request.setStatus(StatusType.valueOf(record.get("status").trim().toUpperCase()));
		request.setAssignedSources(Arrays.stream(record.get("assignedSources").split(",")).map(String::trim)
				.map(String::toUpperCase).map(SourceType::valueOf).collect(Collectors.toSet()));
		return request;
	}

	@Override
	public ExportFileResponseDto exportUsers(String fileType, String keyword, RoleType role, StatusType status,
			String fromDate, String toDate) {

		User loggedInUser = securityUtils.getLoggedInUser();

		LocalDate parsedFromDate = parseDate(fromDate, "fromDate");
		LocalDate parsedToDate = parseDate(toDate, "toDate");
		LocalDateTime fromDateTime = parsedFromDate != null ? parsedFromDate.atStartOfDay() : null;
		LocalDateTime toDateTime = parsedToDate != null ? parsedToDate.atTime(23, 59, 59) : null;

		List<User> users;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {

			users = userRepository.findUsersWithFiltersForExport(keyword, role, status, fromDateTime, toDateTime);

		} else if (loggedInUser.getRole() == RoleType.ADMIN) {

			users = userRepository.findUsersWithFiltersForAdminExport(loggedInUser.getId(), keyword, role, status,
					fromDateTime, toDateTime);

		} else {
			throw new RuntimeException("Access Denied");
		}

		if (fileType == null || fileType.isBlank()) {
			fileType = "csv";
		}

		fileType = fileType.trim().toLowerCase();

		return switch (fileType) {

		case "csv" -> ExportFileResponseDto.builder().file(exportUsersToCsv(users)).fileName("users.csv")
				.contentType("text/csv").build();

		case "xls" -> ExportFileResponseDto.builder().file(exportUsersToExcel(users, "xls")).fileName("users.xls")
				.contentType("application/vnd.ms-excel").build();

		case "xlsx" -> ExportFileResponseDto.builder().file(exportUsersToExcel(users, "xlsx")).fileName("users.xlsx")
				.contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").build();

		case "pdf" -> ExportFileResponseDto.builder().file(exportUsersToPdf(users)).fileName("users.pdf")
				.contentType("application/pdf").build();

		default -> throw new RuntimeException("Only csv, xls, xlsx and pdf formats are allowed");
		};
	}

	private byte[] exportUsersToCsv(List<User> users) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)) {

			csvPrinter.printRecord("id", "firstName", "lastName", "userName", "email", "phoneNo", "address", "state",
					"city", "role", "status", "assignedSources", "createdBy", "createdAt");

			for (User user : users) {

				csvPrinter
						.printRecord(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(),
								user.getEmail(), user.getPhoneNo(), user.getAddress(), user.getState(), user.getCity(),
								user.getRole(), user.getStatus(),
								user.getAssignedSources() != null ? user.getAssignedSources().stream().map(Enum::name)
										.collect(Collectors.joining(",")) : "",
								user.getCreatedBy() != null
										? user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName()
										: "",
								user.getCreatedAt());
			}

			csvPrinter.flush();

			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to export users CSV", e);
		}
	}

	private byte[] exportUsersToExcel(List<User> users, String fileType) {

		try (Workbook workbook = fileType.equals("xls") ? new HSSFWorkbook() : new XSSFWorkbook();

				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Users");

			String[] columns = { "id", "firstName", "lastName", "userName", "email", "phoneNo", "address", "state",
					"city", "role", "status", "assignedSources", "createdBy", "createdAt" };

			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < columns.length; i++) {
				headerRow.createCell(i).setCellValue(columns[i]);
			}

			int rowIndex = 1;

			for (User user : users) {

				Row row = sheet.createRow(rowIndex++);

				row.createCell(0).setCellValue(user.getId() != null ? user.getId() : 0);
				row.createCell(1).setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
				row.createCell(2).setCellValue(user.getLastName() != null ? user.getLastName() : "");
				row.createCell(3).setCellValue(user.getUserName() != null ? user.getUserName() : "");
				row.createCell(4).setCellValue(user.getEmail() != null ? user.getEmail() : "");
				row.createCell(5).setCellValue(user.getPhoneNo() != null ? user.getPhoneNo() : "");
				row.createCell(6).setCellValue(user.getAddress() != null ? user.getAddress() : "");
				row.createCell(7).setCellValue(user.getState() != null ? user.getState() : "");
				row.createCell(8).setCellValue(user.getCity() != null ? user.getCity() : "");
				row.createCell(9).setCellValue(user.getRole() != null ? user.getRole().name() : "");
				row.createCell(10).setCellValue(user.getStatus() != null ? user.getStatus().name() : "");

				row.createCell(11)
						.setCellValue(user.getAssignedSources() != null
								? user.getAssignedSources().stream().map(Enum::name).collect(Collectors.joining(","))
								: "");

				row.createCell(12)
						.setCellValue(user.getCreatedBy() != null
								? user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName()
								: "");

				row.createCell(13).setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);

			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to export users Excel", e);
		}
	}

	private byte[] exportUsersToPdf(List<User> users) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, out);

			document.open();
			document.add(new Paragraph("Users Export"));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(11);
			table.setWidthPercentage(100);

			table.addCell("ID");
			table.addCell("First Name");
			table.addCell("Last Name");
			table.addCell("Username");
			table.addCell("Email");
			table.addCell("Phone");
			table.addCell("City");
			table.addCell("State");
			table.addCell("Role");
			table.addCell("Status");
			table.addCell("Sources");

			for (User user : users) {
				table.addCell(user.getId() != null ? String.valueOf(user.getId()) : "");
				table.addCell(user.getFirstName() != null ? user.getFirstName() : "");
				table.addCell(user.getLastName() != null ? user.getLastName() : "");
				table.addCell(user.getUserName() != null ? user.getUserName() : "");
				table.addCell(user.getEmail() != null ? user.getEmail() : "");
				table.addCell(user.getPhoneNo() != null ? user.getPhoneNo() : "");
				table.addCell(user.getCity() != null ? user.getCity() : "");
				table.addCell(user.getState() != null ? user.getState() : "");
				table.addCell(user.getRole() != null ? user.getRole().name() : "");
				table.addCell(user.getStatus() != null ? user.getStatus().name() : "");
				table.addCell(user.getAssignedSources() != null
						? user.getAssignedSources().stream().map(Enum::name).collect(Collectors.joining(","))
						: "");
			}

			document.add(table);

			document.close();

			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to export users PDF", e);
		}
	}

	@Override
	public List<UserMapMarkerDto> getUserMapMarkers() {

		User loggedInUser = securityUtils.getLoggedInUser();

		Long adminId = null;

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			adminId = null;
		} else if (loggedInUser.getRole() == RoleType.ADMIN) {
			adminId = loggedInUser.getId();
		} else {
			throw new RuntimeException("Access Denied");
		}

		return userLocationRepository.findUserMapMarkers(adminId).stream().map(this::mapToUserMapMarker).toList();
	}

	private UserMapMarkerDto mapToUserMapMarker(UserLocation location) {

		User user = location.getUser();

		String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
				+ (user.getLastName() != null ? user.getLastName() : "")).trim();

		return UserMapMarkerDto.builder().userId(user.getId()).fullName(fullName).email(user.getEmail())
				.phoneNo(user.getPhoneNo()).address(location.getAddress()).city(location.getCity())
				.state(location.getState()).country(location.getCountry()).latitude(location.getLatitude())
				.longitude(location.getLongitude()).role(user.getRole()).status(user.getStatus()).build();
	}

}