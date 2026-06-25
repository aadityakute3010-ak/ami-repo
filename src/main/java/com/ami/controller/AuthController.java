package com.ami.controller;

import com.ami.dto.requests.ChangePasswordRequestDto;
import org.springframework.security.core.Authentication;
import com.ami.entity.User;
import com.ami.repository.UserRepository;
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
	private final UserRepository userRepository;


	public AuthController(
	        AuthService authService,
	        UserRepository userRepository) {

	    this.authService = authService;
	    this.userRepository = userRepository;
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
    
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        LoginResponse response =
                new LoginResponse();

        response.setUserId(
                user.getId());

        response.setFirstName(
                user.getFirstName());

        response.setLastName(
                user.getLastName());

        response.setEmail(
                user.getEmail());

        response.setRole(
                user.getRole());

        response.setAssignedSources(
                user.getAssignedSources());

        return ResponseEntity.ok(
                response);
    }
    
}