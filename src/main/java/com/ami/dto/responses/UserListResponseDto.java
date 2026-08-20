package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponseDto {

	private Long id;

	private String fullName;

	private String email;

	private String phoneNo;

	private Set<SourceType> assignedSources;

	private StatusType status;

	private RoleType role;

	private String address;

	private String city;

	private String state;

}