package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.RecruiterProfileRequest;
import com.careermatch.pamtenproject.dto.RecruiterProfileResponse;
import com.careermatch.pamtenproject.exception.*;
import com.careermatch.pamtenproject.model.*;
import com.careermatch.pamtenproject.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final GenderRepository genderRepository;
    private final IndustryRepository industryRepository;

    // ===== EXISTING METHODS (unchanged) =====

    @Transactional
    public RecruiterProfileResponse completeRecruiterProfile(RecruiterProfileRequest request) {
        log.info("Completing recruiter profile for user: {}", request.getUserId());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!"Recruiter".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new InvalidRoleException("User is not a recruiter");
        }

        if (user.getProfileCompleted()) {
            throw new ProfileAlreadyCompletedException("Profile already completed");
        }

        String employerNumber = generateUniqueEmployerNumber();

        Gender gender = genderRepository.findByGenderName(request.getGender())
                .orElseThrow(() -> new InvalidInputException("Invalid gender"));

        try {
            Employer employer = Employer.builder()
                    .employerNumber(employerNumber)
                    .user(user)
                    .hrName(request.getHrName())
                    .hrEmail(request.getHrEmail())
                    .gender(gender)
                    .organizationName(request.getOrganizationName())
                    .endClient(request.getEndClient())
                    .vendorName(request.getVendorName())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            employerRepository.save(employer);

            Industry industry = null;
            if (request.getIndustryName() != null && !request.getIndustryName().trim().isEmpty()) {
                industry = industryRepository.findByIndustryName(request.getIndustryName()).orElse(null);
            }

            Recruiter recruiter = Recruiter.builder()
                    .user(user)
                    .employer(employer)
                    .industry(industry)
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(gender)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            recruiterRepository.save(recruiter);

            user.setProfileCompleted(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Recruiter profile completed successfully for user: {}", request.getUserId());

            return RecruiterProfileResponse.builder()
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roleName(user.getRole().getRoleName())
                    .profileCompleted(user.getProfileCompleted())
                    .employerNumber(employer.getEmployerNumber())
                    .organizationName(employer.getOrganizationName())
                    .hrName(employer.getHrName())
                    .hrEmail(employer.getHrEmail())
                    .endClient(employer.getEndClient())
                    .vendorName(employer.getVendorName())
                    .dateOfBirth(recruiter.getDateOfBirth())
                    .gender(gender.getGenderName())
                    .industryName(industry != null ? industry.getIndustryName() : null)
                    .industryDescription(industry != null ? industry.getDescription() : null)
                    .message("Recruiter profile completed successfully!")
                    .build();

        } catch (Exception e) {
            log.error("Failed to complete recruiter profile for user {}: {}", request.getUserId(), e.getMessage());
            throw new BusinessRuleViolationException("Failed to complete recruiter profile: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Recruiter createOrUpdateRecruiterProfile(String userId, Recruiter recruiterData) {
        log.info("Creating/updating recruiter profile for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Recruiter> existing = recruiterRepository.findByUser_UserId(userId);
        Recruiter recruiter;

        try {
            if (existing.isPresent()) {
                recruiter = existing.get();
                recruiter.setDateOfBirth(recruiterData.getDateOfBirth());
                recruiter.setGender(recruiterData.getGender());
                recruiter.setIndustry(recruiterData.getIndustry());

                // Update employer and its fields
                if (recruiterData.getEmployer() != null) {
                    if (recruiter.getEmployer() == null) {
                        recruiter.setEmployer(recruiterData.getEmployer());
                    } else {
                        recruiter.getEmployer().setOrganizationName(recruiterData.getEmployer().getOrganizationName());
                        recruiter.getEmployer().setHrName(recruiterData.getEmployer().getHrName());
                        recruiter.getEmployer().setHrEmail(recruiterData.getEmployer().getHrEmail());
                        recruiter.getEmployer().setEndClient(recruiterData.getEmployer().getEndClient());
                        recruiter.getEmployer().setVendorName(recruiterData.getEmployer().getVendorName());
                        recruiter.getEmployer().setGender(recruiterData.getEmployer().getGender());
                        // ...add any other employer fields you want to update
                    }
                }
            } else {
                recruiter = Recruiter.builder()
                        .user(user)
                        .dateOfBirth(recruiterData.getDateOfBirth())
                        .gender(recruiterData.getGender())
                        .industry(recruiterData.getIndustry())
                        .employer(recruiterData.getEmployer())
                        .build();
            }

            recruiter = recruiterRepository.save(recruiter);
            log.info("Recruiter profile created/updated successfully for user: {}", userId);

            return recruiter;

        } catch (Exception e) {
            log.error("Failed to create/update recruiter profile for user {}: {}", userId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to create/update recruiter profile: " + e.getMessage(), e);
        }
    }

    public RecruiterProfileResponse createOrUpdateRecruiterProfileResponse(String userId, Recruiter recruiterData) {
        log.info("Creating/updating recruiter profile response for user: {}", userId);

        Recruiter recruiter = createOrUpdateRecruiterProfile(userId, recruiterData);
        return convertToRecruiterProfileResponse(recruiter);
    }

    public Recruiter getRecruiterProfile(String userId) {
        log.info("Fetching recruiter profile for user: {}", userId);

        Recruiter recruiter = recruiterRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        log.info("Recruiter profile fetched successfully for user: {}", userId);
        return recruiter;
    }

    public RecruiterProfileResponse getRecruiterProfileResponse(String userId) {
        log.info("Fetching recruiter profile response for user: {}", userId);

        Recruiter recruiter = getRecruiterProfile(userId);
        return convertToRecruiterProfileResponse(recruiter);
    }

    // ===== NEW METHODS (moved from ProfileService) =====

    public String completeRecruiterProfileLegacy(RecruiterProfileRequest request) {
        log.info("Completing recruiter profile (legacy method) for user: {}", request.getUserId());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!"Recruiter".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new InvalidRoleException("User is not a recruiter");
        }

        if (user.getProfileCompleted()) {
            throw new ProfileAlreadyCompletedException("Profile already completed");
        }

        String employerNumber = generateUniqueEmployerNumber();
        Gender gender = genderRepository.findByGenderName(request.getGender())
                .orElseThrow(() -> new InvalidInputException("Invalid gender"));

        try {
            Employer employer = Employer.builder()
                    .employerNumber(employerNumber)
                    .user(user)
                    .hrName(request.getHrName())
                    .hrEmail(request.getHrEmail())
                    .gender(gender)
                    .organizationName(request.getOrganizationName())
                    .endClient(request.getEndClient())
                    .vendorName(request.getVendorName())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            employerRepository.save(employer);

            Industry industry = null;
            if (request.getIndustryName() != null && !request.getIndustryName().trim().isEmpty()) {
                industry = industryRepository.findByIndustryName(request.getIndustryName()).orElse(null);
            }

            Recruiter recruiter = Recruiter.builder()
                    .user(user)
                    .employer(employer)
                    .industry(industry)
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(gender)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            recruiterRepository.save(recruiter);

            user.setProfileCompleted(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Recruiter profile completed successfully (legacy method) for user: {}", request.getUserId());

            return "Recruiter profile completed successfully! Employer Number: " + employerNumber;

        } catch (Exception e) {
            log.error("Failed to complete recruiter profile (legacy method) for user {}: {}", request.getUserId(), e.getMessage());
            throw new BusinessRuleViolationException("Failed to complete recruiter profile: " + e.getMessage(), e);
        }
    }

    public boolean isProfileCompleted(String userId) {
        log.info("Checking if profile is completed for user: {}", userId);

        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            boolean completed = user.getProfileCompleted();
            log.info("Profile completion status for user {}: {}", userId, completed);
            return completed;

        } catch (Exception e) {
            log.error("Failed to check profile completion for user {}: {}", userId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to check profile completion: " + e.getMessage(), e);
        }
    }

    public String getEmployerNumber(String userId) {
        log.info("Getting employer number for user: {}", userId);

        try {
            Employer employer = employerRepository.findByUser_UserId(userId)
                    .orElse(null);

            String employerNumber = (employer != null) ? employer.getEmployerNumber() : null;
            log.info("Employer number for user {}: {}", userId, employerNumber);
            return employerNumber;

        } catch (Exception e) {
            log.error("Failed to get employer number for user {}: {}", userId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to get employer number: " + e.getMessage(), e);
        }
    }

    private RecruiterProfileResponse convertToRecruiterProfileResponse(Recruiter recruiter) {
        User user = recruiter.getUser();
        Employer employer = recruiter.getEmployer();

        return RecruiterProfileResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleName(user.getRole().getRoleName())
                .profileCompleted(user.getProfileCompleted())
                .employerNumber(employer != null ? employer.getEmployerNumber() : null)
                .organizationName(employer != null ? employer.getOrganizationName() : null)
                .hrName(employer != null ? employer.getHrName() : null)
                .hrEmail(employer != null ? employer.getHrEmail() : null)
                .endClient(employer != null ? employer.getEndClient() : null)
                .vendorName(employer != null ? employer.getVendorName() : null)
                .dateOfBirth(recruiter.getDateOfBirth())
                .gender(recruiter.getGender() != null ? recruiter.getGender().getGenderName() : null)
                .industryName(recruiter.getIndustry() != null ? recruiter.getIndustry().getIndustryName() : null)
                .industryDescription(recruiter.getIndustry() != null ? recruiter.getIndustry().getDescription() : null)
                .message("Recruiter profile retrieved successfully!")
                .build();
    }

    private String generateUniqueEmployerNumber() {
        log.info("Generating unique employer number");

        try {
            Integer maxNumber = employerRepository.findMaxEmployerNumber();
            int nextNumber = (maxNumber != null) ? maxNumber + 1 : 10000;
            if (nextNumber > 99999) {
                throw new BusinessRuleViolationException("No available employer numbers");
            }

            String employerNumber = String.format("%05d", nextNumber);
            log.info("Generated employer number: {}", employerNumber);
            return employerNumber;

        } catch (Exception e) {
            log.error("Failed to generate employer number: {}", e.getMessage());
            throw new BusinessRuleViolationException("Failed to generate employer number: " + e.getMessage(), e);
        }
    }
}