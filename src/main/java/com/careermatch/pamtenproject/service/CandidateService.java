package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.CandidateProfileRequest;
import com.careermatch.pamtenproject.dto.CandidateProfileResponse;
import com.careermatch.pamtenproject.exception.*;
import com.careermatch.pamtenproject.model.Candidate;
import com.careermatch.pamtenproject.model.Gender;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.CandidateRepository;
import com.careermatch.pamtenproject.repository.GenderRepository;
import com.careermatch.pamtenproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;

    @Transactional
    public CandidateProfileResponse completeOrUpdateProfile(CandidateProfileRequest request) {
        log.info("Completing/updating candidate profile for user: {}", request.getUserId());

        // Validation
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new InvalidInputException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new InvalidInputException("Last name is required");
        }
        if (request.getDateOfBirth() == null) {
            throw new InvalidInputException("Date of birth is required");
        }
        if (request.getLinkedinUrl() != null && !request.getLinkedinUrl().startsWith("http")) {
            throw new InvalidInputException("LinkedIn URL must be valid");
        }
        if (request.getGithubUsername() != null && !request.getGithubUsername().matches("^[a-zA-Z0-9-]+$")) {
            throw new InvalidInputException("GitHub username is invalid");
        }

        try {
            User user = userRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            Gender gender = null;
            if (request.getGenderId() != null) {
                gender = genderRepository.findById(request.getGenderId())
                        .orElseThrow(() -> new InvalidInputException("Invalid gender"));
            }

            Candidate candidate = candidateRepository.findByUserUserId(request.getUserId())
                    .orElse(Candidate.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build());

            candidate.setFirstName(request.getFirstName());
            candidate.setLastName(request.getLastName());
            candidate.setDateOfBirth(request.getDateOfBirth());
            candidate.setGender(gender);
            candidate.setExperienceYears(request.getExperienceYears());
            candidate.setLinkedinUrl(request.getLinkedinUrl());
            candidate.setGithubUsername(request.getGithubUsername());
            candidate.setUpdatedAt(LocalDateTime.now());

            Candidate saved = candidateRepository.save(candidate);

            // Set profile completed flag
            if (user.getProfileCompleted() == null || !user.getProfileCompleted()) {
                user.setProfileCompleted(true);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }

            log.info("Candidate profile created/updated successfully for userId: {}", request.getUserId());

            return CandidateProfileResponse.builder()
                    .candidateId(saved.getCandidateId())
                    .userId(saved.getUser().getUserId())
                    .firstName(saved.getFirstName())
                    .lastName(saved.getLastName())
                    .dateOfBirth(saved.getDateOfBirth())
                    .gender(saved.getGender() != null ? saved.getGender().getGenderName() : null)
                    .experienceYears(saved.getExperienceYears())
                    .linkedinUrl(saved.getLinkedinUrl())
                    .githubUsername(saved.getGithubUsername())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();

        } catch (Exception e) {
            log.error("Failed to complete/update candidate profile for user {}: {}", request.getUserId(), e.getMessage());
            throw new BusinessRuleViolationException("Failed to complete/update candidate profile: " + e.getMessage(), e);
        }
    }

    public CandidateProfileResponse getProfileByUserId(String userId) {
        log.info("Fetching candidate profile for user: {}", userId);

        try {
            Candidate candidate = candidateRepository.findByUserUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

            log.info("Candidate profile fetched successfully for user: {}", userId);

            return CandidateProfileResponse.builder()
                    .candidateId(candidate.getCandidateId())
                    .userId(candidate.getUser().getUserId())
                    .firstName(candidate.getFirstName())
                    .lastName(candidate.getLastName())
                    .dateOfBirth(candidate.getDateOfBirth())
                    .gender(candidate.getGender() != null ? candidate.getGender().getGenderName() : null)
                    .experienceYears(candidate.getExperienceYears())
                    .linkedinUrl(candidate.getLinkedinUrl())
                    .githubUsername(candidate.getGithubUsername())
                    .createdAt(candidate.getCreatedAt())
                    .updatedAt(candidate.getUpdatedAt())
                    .build();

        } catch (Exception e) {
            log.error("Failed to fetch candidate profile for user {}: {}", userId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to fetch candidate profile: " + e.getMessage(), e);
        }
    }
}