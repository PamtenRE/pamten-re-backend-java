package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.ResumeResponse;
import com.careermatch.pamtenproject.exception.*;
import com.careermatch.pamtenproject.model.Candidate;
import com.careermatch.pamtenproject.model.Resume;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.CandidateRepository;
import com.careermatch.pamtenproject.repository.ResumeRepository;
import com.careermatch.pamtenproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final GcsService gcsService;

    @Transactional
    public ResumeResponse uploadResume(MultipartFile file, Boolean setAsDefault, String customName, String userEmail) throws IOException {
        log.info("Uploading resume for user email: {}", userEmail);

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File cannot be empty");
        }

        // Validate file type and size
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileValidationException("Only PDF, DOC, and DOCX files are allowed.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileValidationException("File size cannot exceed 5MB");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Candidate candidate = candidateRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Limit to 3 resumes per candidate
        List<Resume> resumes = resumeRepository.findByCandidateCandidateId(candidate.getCandidateId());
        if (resumes.size() >= 3) {
            throw new ResumeLimitExceededException("You can only have up to 3 resumes.");
        }

        // If setting as default, unset other default resumes
        if (Boolean.TRUE.equals(setAsDefault)) {
            resumes.stream().filter(Resume::getIsDefault).forEach(resume -> {
                resume.setIsDefault(false);
                resumeRepository.save(resume);
            });
        }

        try {
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

            log.info("Resume uploaded successfully: {} for user: {}", saved.getResumeId(), userEmail);

            return ResumeResponse.builder()
                    .resumeId(saved.getResumeId())
                    .fileName(saved.getFileName())
                    .filePath(saved.getFilePath())
                    .fileSize(saved.getFileSize())
                    .customName(saved.getCustomName())
                    .isDefault(saved.getIsDefault())
                    .uploadDate(saved.getUploadDate())
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload resume for user {}: {}", userEmail, e.getMessage());
            throw new FileStorageException("Failed to upload resume: " + e.getMessage(), e);
        }
    }

    public List<ResumeResponse> getCandidateResumes(String userEmail) {
        log.info("Fetching resumes for user email: {}", userEmail);

        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            Candidate candidate = candidateRepository.findByUserUserId(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

            List<Resume> resumes = resumeRepository.findByCandidateCandidateId(candidate.getCandidateId());

            log.info("Found {} resumes for user: {}", resumes.size(), userEmail);

            return resumes.stream()
                    .map(this::convertToResumeResponse)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to fetch resumes for user {}: {}", userEmail, e.getMessage());
            throw new BusinessRuleViolationException("Failed to fetch resumes: " + e.getMessage(), e);
        }
    }

    public void deleteResume(Integer resumeId, String userEmail) {
        log.info("Deleting resume: {} for user email: {}", resumeId, userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Candidate candidate = candidateRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        // Security check: ensure the resume belongs to the candidate
        if (!resume.getCandidate().getCandidateId().equals(candidate.getCandidateId())) {
            throw new UnauthorizedActionException("You are not authorized to delete this resume");
        }

        resumeRepository.delete(resume);

        log.info("Resume deleted successfully: {} for user: {}", resumeId, userEmail);
    }

    public ResumeResponse setDefaultResume(Integer resumeId, String userEmail) {
        log.info("Setting default resume: {} for user email: {}", resumeId, userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Candidate candidate = candidateRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        // Security check: ensure the resume belongs to the candidate
        if (!resume.getCandidate().getCandidateId().equals(candidate.getCandidateId())) {
            throw new UnauthorizedActionException("You are not authorized to modify this resume");
        }

        // Unset other default resumes
        List<Resume> allResumes = resumeRepository.findByCandidateCandidateId(candidate.getCandidateId());
        allResumes.stream().filter(Resume::getIsDefault).forEach(r -> {
            r.setIsDefault(false);
            resumeRepository.save(r);
        });

        // Set this resume as default
        resume.setIsDefault(true);
        Resume saved = resumeRepository.save(resume);

        log.info("Default resume set successfully: {} for user: {}", resumeId, userEmail);

        return convertToResumeResponse(saved);
    }

    private ResumeResponse convertToResumeResponse(Resume resume) {
        return ResumeResponse.builder()
                .resumeId(resume.getResumeId())  // Changed from .id() to .resumeId()
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .fileSize(resume.getFileSize())
                .customName(resume.getCustomName())
                .isDefault(resume.getIsDefault())
                .uploadDate(resume.getUploadDate())
                .build();
    }
}