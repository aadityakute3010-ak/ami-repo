package com.ami.service.impl;

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
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import com.ami.repository.UserRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	private SecurityUtils securityUtils;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		// USERNAME VALIDATION
		if (request.getUserName() != null && userRepository.existsByUserName(request.getUserName())) {
			throw new RuntimeException("Username already exists");
		}

		// SOURCE VALIDATION
		validateSourceAssignment(creator, request.getAssignedSources());

		// CREATE USER
		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUserName(request.getUserName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhoneNo(request.getPhoneNo());
		user.setAddress(request.getAddress());
		user.setState(request.getState());
		user.setCity(request.getCity());
		user.setRole(request.getRole());
		user.setStatus(StatusType.ACTIVE);
		user.setAssignedSources(request.getAssignedSources());
		// TRACK WHO CREATED USER
		user.setCreatedBy(creator);
		User savedUser = userRepository.save(user);
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
		response.setActive(user.getActive());
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
			response.setAllowedSources(loggedInUser.getAssignedSources());
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

		if (creator.getAssignedSources().contains(SourceType.ALL)) {
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
	public PagedUserResponseDto getUsers(RoleType role, int page, int size) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage;

		// SUPER ADMIN -> SEE ALL USERS
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			if (role != null) {
				userPage = userRepository.findByRole(role, pageable);
			} else {
				userPage = userRepository.findAll(pageable);
			}
		}

		// ADMIN -> ONLY OWN CREATED USERS
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			if (role != null) {
				userPage = userRepository.findByRoleAndCreatedBy(role, loggedInUser, pageable);
			} else {
				userPage = userRepository.findByCreatedBy(loggedInUser, pageable);
			}
		}

		// USER / ENGINEER
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
		// PLACEHOLDER VALUES
		// UNTIL WALLET MODULE
		dto.setCurrentBalance(0.0);
		dto.setOutstandingAmount(0.0);
		// PLACEHOLDER
		// UNTIL DEVICE MODULE
		dto.setAssignedDevices(List.of());
		dto.setAssignedMeters(List.of());
		// ACTIVITY DETAILS
		if (user.getCreatedBy() != null) {
			dto.setCreatedBy(user.getCreatedBy().getFirstName() + " " + user.getCreatedBy().getLastName());
		}
		dto.setCreatedAt(user.getCreatedAt());
		dto.setActive(user.getActive());
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
		dto.setActive(user.getActive());
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

		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			if (userRepository.existsByEmail(request.getEmail())) {
				throw new RuntimeException("Email already exists");
			}
			user.setEmail(request.getEmail());
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

		// UPDATE FIELDS
		targetUser.setFirstName(request.getFirstName());
		targetUser.setLastName(request.getLastName());
		if (request.getEmail() != null && !request.getEmail().equals(targetUser.getEmail())) {
			if (userRepository.existsByEmail(request.getEmail())) {
				throw new RuntimeException("Email already exists");
			}
			targetUser.setEmail(request.getEmail());
		}
		targetUser.setPhoneNo(request.getPhoneNo());
		targetUser.setAddress(request.getAddress());
		targetUser.setState(request.getState());
		targetUser.setCity(request.getCity());

		if (request.getActive() != null) {
			targetUser.setActive(request.getActive());
		}

		if (request.getStatus() != null) {
			targetUser.setStatus(request.getStatus());
		}

		// ADMIN CAN ASSIGN ONLY OWN SOURCES
		if (request.getAssignedSources() != null && !request.getAssignedSources().isEmpty()) {

			if (admin.getRole() == RoleType.ADMIN
					&& !admin.getAssignedSources().containsAll(request.getAssignedSources())) {
				throw new RuntimeException("Cannot assign unauthorized sources");
			}

			targetUser.setAssignedSources(request.getAssignedSources());
		}

		User updatedUser = userRepository.save(targetUser);

		return mapToAdminUpdateUserResponse(updatedUser);
	}

	@Override
	public String softDeleteUser(Long userId) {

		User loggedInUser = securityUtils.getLoggedInUser();
		User targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Target user not found"));

		// SUPER ADMIN DELETE RULES
		if (targetUser.getRole() == RoleType.SUPER_ADMIN) {

			// ONLY SUPER ADMIN CAN DELETE SUPER ADMIN
			if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
				throw new RuntimeException("You cannot delete Super Admin");
			}

			// SUPER ADMIN CAN DELETE ONLY OWN CREATED SUPER ADMINS
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot delete this Super Admin");
			}
		}

		// SUPER ADMIN CAN DELETE ANYONE
		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {

			// ONLY ADMIN CAN DELETE
			if (loggedInUser.getRole() != RoleType.ADMIN) {
				throw new RuntimeException("Access Denied");
			}

			// ADMIN CAN DELETE ONLY OWN CREATED USERS
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot delete this user");
			}
		}

		// SOFT DELETE
		targetUser.setActive(false);
		targetUser.setStatus(StatusType.INACTIVE);

		userRepository.save(targetUser);

		return "User deactivated successfully";
	}

	@Override
	public String hardDeleteUser(Long userId) {

		User loggedInUser = securityUtils.getLoggedInUser();
		User targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Target user not found"));

		// SUPER ADMIN DELETE RULES
		if (targetUser.getRole() == RoleType.SUPER_ADMIN) {
			// ONLY SUPER ADMIN CAN DELETE SUPER ADMIN
			if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
				throw new RuntimeException("You cannot delete Super Admin");
			}

			// SUPER ADMIN CAN DELETE ONLY OWN CREATED SUPER ADMINS
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot delete this Super Admin");
			}
		}

		// SUPER ADMIN CAN DELETE ANYONE
		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {

			// ONLY ADMIN CAN DELETE
			if (loggedInUser.getRole() != RoleType.ADMIN) {
				throw new RuntimeException("Access Denied");
			}

			// ADMIN CAN DELETE ONLY OWN CREATED USERS
			if (targetUser.getCreatedBy() == null || !targetUser.getCreatedBy().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You cannot delete this user");
			}
		}

		// HARD DELETE
		userRepository.delete(targetUser);
		return "User deleted permanently";
	}

	@Override
	public PagedUserResponseDto searchUsers(String keyword, int page, int size) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage;

		// SUPER ADMIN -> SEARCH ALL USERS
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			userPage = userRepository.searchUsersForSuperAdmin(keyword, pageable);
		}

		// ADMIN -> SEARCH ONLY OWN USERS
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			userPage = userRepository.searchUsersForAdmin(keyword, loggedInUser.getId(), pageable);
		}

		// USER / ENGINEER
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

	@Override
	public PagedUserResponseDto getUsersByStatus(Boolean active, int page, int size) {

		User loggedInUser = securityUtils.getLoggedInUser();
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage;

		// SUPER ADMIN -> CAN SEE ALL USERS
		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			userPage = userRepository.findByActive(active, pageable);
		}

		// ADMIN -> ONLY OWN CREATED USERS
		else if (loggedInUser.getRole() == RoleType.ADMIN) {
			userPage = userRepository.findByActiveAndCreatedBy(active, loggedInUser, pageable);
		}

		// USER / SERVICE ENGINEER
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

	@Override
	public List<UserListResponseDto> getEligibleAdminsBySource(SourceType sourceType, String search) {

		User loggedInUser = securityUtils.getLoggedInUser();

		if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can view eligible admins");
		}

		List<User> admins = userRepository.findEligibleAdmins(RoleType.ADMIN, sourceType, search);

		return admins.stream().map(this::mapToUserListResponseDto).toList();
	} 

	private UserListResponseDto mapToUserListResponseDto(User user) {

		return UserListResponseDto.builder().id(user.getId())
				.fullName(user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""))
				.email(user.getEmail()).phoneNo(user.getPhoneNo()).assignedSources(user.getAssignedSources())
				.status(user.getStatus()).role(user.getRole()).build();
	} 

}