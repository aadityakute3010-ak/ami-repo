package com.ami.dto.responses; 

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserDetailsResponseDto {

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

    /*
        WALLET SECTION
     */
    private Double currentBalance;

    private Double outstandingAmount;

    /*
        ASSIGNED DEVICES
     */
    private List<String> assignedDevices;

    /*
        ASSIGNED METERS
     */
    private List<String> assignedMeters;

    /*
        ACTIVITY DETAILS
     */
    private String createdBy;

    private LocalDateTime createdAt;

    private Boolean active;

    public UserDetailsResponseDto() {
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

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(Double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public List<String> getAssignedDevices() {
		return assignedDevices;
	}

	public void setAssignedDevices(List<String> assignedDevices) {
		this.assignedDevices = assignedDevices;
	}

	public List<String> getAssignedMeters() {
		return assignedMeters;
	}

	public void setAssignedMeters(List<String> assignedMeters) {
		this.assignedMeters = assignedMeters;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    
}