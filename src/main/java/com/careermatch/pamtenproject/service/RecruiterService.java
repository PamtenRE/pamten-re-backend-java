package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.RecruiterProfileRequest;
import com.careermatch.pamtenproject.dto.RecruiterProfileResponse;
import com.careermatch.pamtenproject.model.*;
import com.careermatch.pamtenproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final GenderRepository genderRepository;
    private final IndustryRepository industryRepository;

    @Transactional
    public RecruiterProfileResponse completeRecruiterProfile(RecruiterProfileRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"Recruiter".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new RuntimeException("User is not a recruiter");
        }
        if (user.getProfileCompleted()) {
            throw new RuntimeException("Profile already completed");
        }
        String employerNumber = generateUniqueEmployerNumber();
        Gender gender = genderRepository.findByGenderName(request.getGender())
                .orElseThrow(() -> new RuntimeException("Invalid gender"));
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
    }

    public Recruiter createOrUpdateRecruiterProfile(String userId, Recruiter recruiterData) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Recruiter> existing = recruiterRepository.findByUser_UserId(userId);
        Recruiter recruiter;
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
        return recruiterRepository.save(recruiter);
    }

    public RecruiterProfileResponse createOrUpdateRecruiterProfileResponse(String userId, Recruiter recruiterData) {
        Recruiter recruiter = createOrUpdateRecruiterProfile(userId, recruiterData);
        return convertToRecruiterProfileResponse(recruiter);
    }

    public Recruiter getRecruiterProfile(String userId) {
        return recruiterRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }

    public RecruiterProfileResponse getRecruiterProfileResponse(String userId) {
        Recruiter recruiter = getRecruiterProfile(userId);
        return convertToRecruiterProfileResponse(recruiter);
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
        Integer maxNumber = employerRepository.findMaxEmployerNumber();
        int nextNumber = (maxNumber != null) ? maxNumber + 1 : 10000;
        if (nextNumber > 99999) {
            throw new RuntimeException("No available employer numbers");
        }
        return String.format("%05d", nextNumber);
    }
}