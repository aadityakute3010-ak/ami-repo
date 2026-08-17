package com.ami.dto.responses; 

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import java.util.Set;

public class UserListResponseDto {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNo;

    private Set<SourceType> assignedSources;

    private StatusType status;

    private RoleType role;

    public UserListResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Set<SourceType> getAssignedSources() {
        return assignedSources;
    }

    public void setAssignedSources(Set<SourceType> assignedSources) {
        this.assignedSources = assignedSources;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}