package com.ami.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ami.dto.responses.BulkUploadResponseDto;
import com.ami.dto.requests.AdminUpdateUserRequestDto;
import com.ami.dto.requests.CreateUserRequest;
import com.ami.dto.requests.UpdateProfileRequestDto;
import com.ami.dto.requests.UserFilterRequestDto;
import com.ami.dto.responses.AdminUpdateUserResponseDto;
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

public interface UserService {

	CreateUserResponseDto createUser(CreateUserRequest request);

	CreationOptionsResponse getCreationOptions();

	PagedUserResponseDto getUsers(UserFilterRequestDto request);

	UserDetailsResponseDto getUserDetails(Long userId);

	MyInfoResponseDto getMyInfo();

	AdminUpdateUserResponseDto getUserForAdminUpdate(Long userId);

	UserDetailsResponseDto updateProfile(UpdateProfileRequestDto request);

	AdminUpdateUserResponseDto adminUpdateUser(Long userId, AdminUpdateUserRequestDto request);

	String deleteUser(Long userId, DeleteType deleteType, StatusType status);

	List<UserListResponseDto> getEligibleAdminsBySource(SourceType sourceType, String search);

	List<UserListResponseDto> getEligibleUsers(Long adminId, SourceType sourceType);

	UserDashboardResponseDto getDashboard();

	BulkUploadResponseDto bulkUploadUsers(MultipartFile file);

	ExportFileResponseDto exportUsers(String fileType, String keyword, RoleType role, StatusType status,
			String fromDate, String toDate);

	List<UserMapMarkerDto> getUserMapMarkers();

}