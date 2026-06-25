package com.ami.controller;

import com.ami.dto.requests.ChangePasswordRequestDto;
import com.ami.dto.requests.ForgotPasswordRequestDto;
import com.ami.dto.requests.LoginRequest;
import com.ami.dto.requests.ResetPasswordRequestDto;
import com.ami.dto.responses.LoginResponse;
import com.ami.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }    

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }  
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    } 
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }  
    
}