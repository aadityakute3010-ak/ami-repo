package com.ami.service.impl;

import com.ami.dto.requests.ChangePasswordRequestDto;
import com.ami.dto.requests.ForgotPasswordRequestDto;
import com.ami.dto.requests.LoginRequest;
import com.ami.dto.requests.ResetPasswordRequestDto;
import com.ami.dto.responses.LoginResponse;
import com.ami.entity.BlacklistedToken;
import com.ami.entity.PasswordResetToken;
import com.ami.entity.User;
import com.ami.repository.BlacklistedTokenRepository;
import com.ami.repository.PasswordResetTokenRepository;
import com.ami.repository.UserRepository;
import com.ami.security.JwtUtil;
import com.ami.service.AuthService;
import com.ami.service.EmailService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; 
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;  
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService; 
    private final BlacklistedTokenRepository blacklistedTokenRepository; 

    public AuthServiceImpl(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            BlacklistedTokenRepository blacklistedTokenRepository) 
    {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.blacklistedTokenRepository = blacklistedTokenRepository; 
    }   

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        //ACCOUNT DEACTIVATED

        if (!user.getActive()) {
            throw new RuntimeException("Your account is deactivated");
        }

        //ACCOUNT LOCK CHECK

        if (Boolean.TRUE.equals(user.getAccountLocked())) {

            LocalDateTime unlockTime = user.getLockTime().plusMinutes(15);

            //LOCK STILL ACTIVE

            if (LocalDateTime.now().isBefore(unlockTime)) {

                long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(),unlockTime);

                throw new RuntimeException(
                        "Account locked. Try again after "
                                + minutesLeft
                                + " minutes"
                );
            } 

            //AUTO UNLOCK

            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);

            userRepository.save(user);
        }

        //PASSWORD CHECK
        boolean matches = passwordEncoder.matches(request.getPassword(),user.getPassword());

        //WRONG PASSWORD
        if (!matches) {

        	int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
            user.setFailedLoginAttempts(attempts);

            //LOCK ACCOUNT AFTER 5 ATTEMPTS
            if (attempts >= 5) {

                user.setAccountLocked(true);

                user.setLockTime(LocalDateTime.now());

                userRepository.save(user);

                throw new RuntimeException("Account locked for 15 minutes");
            } 

            userRepository.save(user);

            throw new RuntimeException("Invalid password. Attempts left: " + (5 - attempts));
        }

        //RESET ATTEMPTS AFTER SUCCESSFUL LOGIN
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);

        userRepository.save(user);

        //GENERATE TOKEN
        String token = jwtUtil.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAssignedSources()
        );
    }  
    
    @Override
    public String logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            BlacklistedToken blacklistedToken = new BlacklistedToken();

            blacklistedToken.setToken(token);

            blacklistedToken.setBlacklistedAt(LocalDateTime.now());

            blacklistedTokenRepository.save(blacklistedToken);
        } 
        return "Logged out successfully";
    } 
    
    @Override
    public String forgotPassword(ForgotPasswordRequestDto request)
    { 

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")); 
        
        passwordResetTokenRepository.findByUser(user).ifPresent(existingToken -> {
            passwordResetTokenRepository.delete(existingToken);
        });  

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setToken(token);

        resetToken.setUser(user);

        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); 

        passwordResetTokenRepository.save(resetToken); 

        String resetLink ="http://localhost:3000/reset-password?token="+ token;

        emailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getFirstName(),
                resetLink
        );  

        return "Password reset link sent to email";
    } 
    
    @Override
    public String resetPassword(ResetPasswordRequestDto request) {

        //NEW PASSWORD AND CONFIRM PASSWORD MATCH CHECK
        if (!request.getNewPassword().equals(request.getConfirmPassword())) 
        {
        	throw new RuntimeException("Passwords do not match");
        }  

        //FIND RESET TOKEN
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                        .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        //TOKEN EXPIRY CHECK
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) 
        {
            throw new RuntimeException("Reset token has expired");
        } 

        //GET USER FROM TOKEN
        User user = resetToken.getUser();

        //UPDATE PASSWORD
        user.setPassword(passwordEncoder.encode(request.getNewPassword())); 

        userRepository.save(user);

        //DELETE TOKEN AFTER SUCCESSFUL RESET
        passwordResetTokenRepository.delete(resetToken);

        return "Password reset successful";
    } 
     
    @Override
    public String changePassword(ChangePasswordRequestDto request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName(); 

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found or token not provided"));
  
        //OLD PASSWORD VALIDATION  
        if (!passwordEncoder.matches(request.getOldPassword(),user.getPassword())) 
        {
            throw new RuntimeException("Old password is incorrect");
        }  
        
        //PASSWORD MATCH VALIDATION  
        if (!request.getNewPassword().equals(request.getConfirmPassword())
        ) {
            throw new RuntimeException("Passwords do not match");
        } 

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "Password changed successfully";
    } 
    
}