package com.ami.service;

import com.ami.dto.requests.ChangePasswordRequestDto;
import com.ami.dto.requests.ForgotPasswordRequestDto;
import com.ami.dto.requests.LoginRequest;
import com.ami.dto.requests.ResetPasswordRequestDto;
import com.ami.dto.responses.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String logout(HttpServletRequest request);

    String forgotPassword(ForgotPasswordRequestDto request);

    String resetPassword(ResetPasswordRequestDto request);

    String changePassword(ChangePasswordRequestDto request);
}