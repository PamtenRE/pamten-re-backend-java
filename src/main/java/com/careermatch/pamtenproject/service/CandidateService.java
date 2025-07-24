package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.CandidateProfileRequest;
import com.careermatch.pamtenproject.dto.CandidateProfileResponse;
import com.careermatch.pamtenproject.dto.ResumeRequest;
import com.careermatch.pamtenproject.dto.ResumeResponse;
import com.careermatch.pamtenproject.model.Candidate;
import com.careermatch.pamtenproject.model.Gender;
import com.careermatch.pamtenproject.model.Resume;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.CandidateRepository;
import com.careermatch.pamtenproject.repository.GenderRepository;
import com.careermatch.pamtenproject.repository.ResumeRepository;
import com.careermatch.pamtenproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final ResumeRepository resumeRepository;
    private final GcsService gcsService;

    @Transactional
    public CandidateProfileResponse completeOrUpdateProfile(CandidateProfileRequest request) {
        // Validation
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if (request.getLinkedinUrl() != null && !request.getLinkedinUrl().startsWith("http")) {
            throw new IllegalArgumentException("LinkedIn URL must be valid");
        }
        if (request.getGithubUsername() != null && !request.getGithubUsername().matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("GitHub username is invalid");
        }

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Gender gender = null;
        if (request.getGenderId() != null) {
            gender = genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new RuntimeException("Invalid gender"));
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

        logger.info("Candidate profile created/updated for userId={}", request.getUserId());

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
    }

    public CandidateProfileResponse getProfileByUserId(String userId) {
        Candidate candidate = candidateRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

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
    }


    @Transactional
    public ResumeResponse uploadResume(MultipartFile file, Boolean setAsDefault, String customName, String userEmail) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");

        // Validate file type and size
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Only PDF, DOC, and DOCX files are allowed.");
        }
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("File size cannot exceed 5MB");

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Candidate candidate = candidateRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        // Limit to 3 resumes per candidate
        List<Resume> resumes = resumeRepository.findByCandidateCandidateId(candidate.getCandidateId());
        if (resumes.size() >= 3) throw new RuntimeException("You can only have up to 3 resumes.");

        // If setting as default, unset other default resumes
        if (Boolean.TRUE.equals(setAsDefault)) {
            resumes.stream().filter(Resume::getIsDefault).forEach(resume -> {
                resume.setIsDefault(false);
                resumeRepository.save(resume);
            });
        }

        String gcsPath = gcsService.uploadFile(file, candidate.getCandidateId());

        Resume resume = Resume.builder()
                .candidate(candidate)
                .fileName(file.getOriginalFilename())
                .filePath(gcsPath)
                .fileSize(file.getSize())
                .uploadDate(LocalDateTime.now())
                .isActive(true)
                .isDefault(setAsDefault != null ? setAsDefault : false)
                .customName(customName)
                .build();

        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.builder()
                .resumeId(saved.getResumeId())
                .fileName(saved.getFileName())
                .filePath(saved.getFilePath())
                .fileSize(saved.getFileSize())
                .customName(saved.getCustomName())
                .isDefault(saved.getIsDefault())
                .uploadDate(saved.getUploadDate())
                .build();
    }
}