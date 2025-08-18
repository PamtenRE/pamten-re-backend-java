package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.CandidateProfileRequest;
import com.careermatch.pamtenproject.dto.CandidateProfileResponse;
import com.careermatch.pamtenproject.exception.UserNotFoundException;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.UserRepository;
import com.careermatch.pamtenproject.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate/v1")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final UserRepository userRepository;

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<CandidateProfileResponse> createOrUpdateProfile(
            Authentication authentication,
            @Valid @RequestBody CandidateProfileRequest request
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        request.setUserId(user.getUserId());

        CandidateProfileResponse response = candidateService.completeOrUpdateProfile(request);
        return ResponseEntity.ok(response);
    }
}