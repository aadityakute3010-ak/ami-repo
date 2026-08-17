package com.ami.dto.requests;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class CreateUserRequest {

	@NotBlank(message = "First name is required")
    private String firstName;

	@NotBlank(message = "Last name is required")
    private String lastName;

	@NotBlank(message = "Username is required")
    private String userName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format") 
    private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8,message = "Password must be at least 8 characters") 
    private String password;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") 
    private String phoneNo;

	@NotBlank(message = "Address is required")
    private String address;

	@NotBlank(message = "State is required")
    private String state;

	@NotBlank(message = "City is required")
    private String city; 

    @NotNull(message = "Role is required") 
    private RoleType role;

    @NotEmpty(message = "At least one source is required") 
    private Set<SourceType> assignedSources;

    public CreateUserRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<SourceType> getAssignedSources() {
        return assignedSources;
    }

    public void setAssignedSources(Set<SourceType> assignedSources) {
        this.assignedSources = assignedSources;
    }
}