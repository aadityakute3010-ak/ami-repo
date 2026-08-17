package com.ami.dto.requests;

import java.util.Set;

import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class AdminUpdateUserRequestDto {

    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNo;
    private String address;
    private String state;
    private String city; 

    private Boolean active;

    private Set<SourceType> assignedSources;
    private StatusType status;

    public AdminUpdateUserRequestDto() {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
    
    
}