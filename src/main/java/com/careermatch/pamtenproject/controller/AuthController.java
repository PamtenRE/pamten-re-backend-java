package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.*;
import com.careermatch.pamtenproject.service.AuthService;
import com.careermatch.pamtenproject.model.Gender;
import com.careermatch.pamtenproject.repository.GenderRepository;
import com.careermatch.pamtenproject.security.JwtBlacklistService;
import com.careermatch.pamtenproject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GenderRepository genderRepository;
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        RegistrationResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable @NotBlank String userId) {
        UserProfileResponse response = authService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
        authService.updatePassword(request);
        return ResponseEntity.ok("Password updated successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping("/genders")
    public ResponseEntity<List<Gender>> getAllGenders() {
        return ResponseEntity.ok(genderRepository.findAll());
    }

    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        authService.updateProfile(request);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") @NotBlank String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Add token to blacklist
            jwtBlacklistService.blacklistToken(token, jwtUtil.extractExpiration(token).getTime());
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid authorization header");
    }
}