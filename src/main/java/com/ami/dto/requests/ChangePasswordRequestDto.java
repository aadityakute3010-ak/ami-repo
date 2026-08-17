package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequestDto {

	@NotBlank(message = "Old password is required")
    private String oldPassword;

	@NotBlank(message = "New password is required")
	@Size(min = 8,message = "Password must be at least 8 characters") 
    private String newPassword;

	@NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public ChangePasswordRequestDto() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(
            String oldPassword
    ) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(
            String newPassword
    ) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(
            String confirmPassword
    ) {
        this.confirmPassword = confirmPassword;
    }
}