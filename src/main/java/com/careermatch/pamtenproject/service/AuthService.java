package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.*;
import com.careermatch.pamtenproject.exception.*;
import com.careermatch.pamtenproject.model.Role;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.RoleRepository;
import com.careermatch.pamtenproject.repository.UserRepository;
import com.careermatch.pamtenproject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // Custom userId generation logic based on name, handling all edge cases
    private String generateCustomUserId(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = "User X";
        }
        String[] parts = fullName.trim().split("\\s+");
        String firstName = parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 0, parts.length - 1)) : parts[0];
        String lastName = parts.length > 1 ? parts[parts.length - 1] : "user";

        // Use up to 5 letters of last name, lowercase
        String lastNamePart = lastName.length() >= 5 ? lastName.substring(0, 5).toLowerCase() : lastName.toLowerCase();
        // Use first letter of first name, lowercase
        String firstInitial = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1).toLowerCase() : "x";

        // Find the next available number for this pattern
        int number = 1;
        String userId;
        do {
            userId = lastNamePart + number + firstInitial;
            number++;
        } while (userRepository.existsByUserId(userId));
        return userId;
    }

    public RegistrationResponse registerUser(SignupRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        // Validate role
        if (!"Candidate".equalsIgnoreCase(request.getRoleName()) &&
                !"Recruiter".equalsIgnoreCase(request.getRoleName())) {
            throw new InvalidRoleException("Invalid role. Must be 'Candidate' or 'Recruiter'.");
        }

        // Special validation for recruiters
        if ("Recruiter".equalsIgnoreCase(request.getRoleName())) {
            if (!request.getEmail().toLowerCase().endsWith("@pamten.com")) {
                throw new BusinessRuleViolationException("Recruiter registration is restricted to @pamten.com email addresses.");
            }
        }

        // Find role
        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new InvalidRoleException("Role not found: " + request.getRoleName()));

        // Generate unique user ID
        String userId = generateCustomUserId(request.getFullName());

        // Create user
        User user = User.builder()
                .userId(userId)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .fullName(request.getFullName())
                .profileCompleted(false) // Profile isn't completed yet
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Send welcome email with userId
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), userId, role.getRoleName());
        } catch (Exception e) {
            // Log the error but don't fail registration
            log.error("Failed to send email: {}", e.getMessage());
        }

        log.info("User registered successfully with ID: {}", userId);

        // Return response with user details
        return RegistrationResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleName(role.getRoleName())
                .profileCompleted(false)
                .message("Registration successful! Check your email for your User ID.")
                .build();
    }

    public LoginResponse loginUser(LoginRequest request) {
        log.info("Login attempt for user ID: {}", request.getUserId());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid userId or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid userId or password");
        }

        if (!user.getIsActive()) {
            throw new AccountDeactivatedException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName());

        log.info("User logged in successfully: {}", request.getUserId());

        return new LoginResponse(token, user.getRole().getRoleName(), user.getEmail(), user.getUserId(), user.getProfileCompleted());
    }

    public UserProfileResponse getUserProfile(String userId) {
        log.info("Fetching profile for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        log.info("Profile fetched successfully for user: {}", userId);

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roleName(user.getRole().getRoleName())
                .profileCompleted(user.getProfileCompleted())
                .isActive(user.getIsActive())
                .build();
    }

    public void updatePassword(UpdatePasswordRequest request) {
        log.info("Updating password for user: {}", request.getUserId());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", request.getUserId());
    }

    public void forgotPassword(String email) {
        log.info("Password reset requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 min
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        log.info("Password reset attempt with token: {}", token);

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new InvalidInputException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getUserId());
    }

    public void updateProfile(UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", request.getUserId());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Profile updated successfully for user: {}", request.getUserId());
    }
}