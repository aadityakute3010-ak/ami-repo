package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDto {

	@NotBlank(message = "Token is required")
    private String token;

	@NotBlank(message = "New password is required")
	@Size(min = 8,message = "Password must be at least 8 characters")
    private String newPassword;

	@NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public ResetPasswordRequestDto() {
    }

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNewPassword(
            String newPassword
    ) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(
            String confirmPassword
    ) {
        this.confirmPassword = confirmPassword;
    }
}