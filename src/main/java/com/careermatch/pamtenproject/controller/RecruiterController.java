package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.RecruiterProfileRequest;
import com.careermatch.pamtenproject.dto.RecruiterProfileResponse;
import com.careermatch.pamtenproject.exception.UserNotFoundException;
import com.careermatch.pamtenproject.model.Recruiter;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.UserRepository;
import com.careermatch.pamtenproject.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/recruiter/v1")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final UserRepository userRepository;

    // ===== EXISTING ENDPOINTS =====

    // Get the current recruiter's profile
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<RecruiterProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        RecruiterProfileResponse response = recruiterService.getRecruiterProfileResponse(user.getUserId());
        return ResponseEntity.ok(response);
    }

    // Create or update the current recruiter's profile
    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<RecruiterProfileResponse> createOrUpdateProfile(
            Authentication authentication,
            @Valid @RequestBody Recruiter recruiterData
    ) {
        if (recruiterData == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        RecruiterProfileResponse response = recruiterService.createOrUpdateRecruiterProfileResponse(user.getUserId(), recruiterData);
        return ResponseEntity.ok(response);
    }

    // ===== NEW ENDPOINTS (moved from ProfileController) =====

    // Complete recruiter profile (legacy endpoint)
    @PostMapping("/complete-profile")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<String> completeRecruiterProfile(
            @Valid @RequestBody RecruiterProfileRequest request) {
        String result = recruiterService.completeRecruiterProfileLegacy(request);
        return ResponseEntity.ok(result);
    }

    // Check if profile is completed
    @GetMapping("/profile-status/{userId}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<Boolean> isProfileCompleted(
            @PathVariable @NotBlank String userId) {
        Boolean isCompleted = recruiterService.isProfileCompleted(userId);
        return ResponseEntity.ok(isCompleted);
    }

    // Get employer number
    @GetMapping("/employer-number/{userId}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<String> getEmployerNumber(
            @PathVariable @NotBlank String userId) {
        String employerNumber = recruiterService.getEmployerNumber(userId);
        if (employerNumber != null) {
            return ResponseEntity.ok(employerNumber);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}