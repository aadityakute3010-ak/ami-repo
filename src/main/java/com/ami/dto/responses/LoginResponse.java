package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import java.util.Set;

public class LoginResponse {

    private String token;

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private RoleType role;

    private Set<SourceType> assignedSources;

	public LoginResponse(String token, Long userId, String firstName, String lastName, String email, RoleType role,
			Set<SourceType> assignedSources) {
		super();
		this.token = token;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
		this.assignedSources = assignedSources;
	} 

	public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}

	public Set<SourceType> getAssignedSources() {
		return assignedSources;
	}

	public void setAssignedSources(Set<SourceType> assignedSources) {
		this.assignedSources = assignedSources;
	}
    
    

}