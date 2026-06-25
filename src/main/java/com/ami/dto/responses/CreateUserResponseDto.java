package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import java.time.LocalDateTime;
import java.util.Set;

public class CreateUserResponseDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String phoneNo;

    private String address;

    private String state;

    private String city;

    private RoleType role;

    private StatusType status;

    private Set<SourceType> assignedSources;

    private Boolean active;

    private LocalDateTime createdAt;

    public CreateUserResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Set<SourceType> getAssignedSources() {
        return assignedSources;
    }

    public void setAssignedSources(Set<SourceType> assignedSources) {
        this.assignedSources = assignedSources;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}