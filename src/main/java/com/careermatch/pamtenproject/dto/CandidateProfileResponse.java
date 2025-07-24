package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CandidateProfileResponse {
    private Integer candidateId;
    private String userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;            // Return gender name to frontend
    private Integer experienceYears;
    private String linkedinUrl;
    private String githubUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}