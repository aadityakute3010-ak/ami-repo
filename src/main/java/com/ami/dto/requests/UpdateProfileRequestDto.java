package com.ami.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern; 

public class UpdateProfileRequestDto {

    private String firstName;

    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$",message = "Phone number must be 10 digits")
    private String phoneNo;  

    private String address;

    private String state;

    private String city;

    public UpdateProfileRequestDto() {
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

    public void setEmail(
            String email
    ) {
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
}