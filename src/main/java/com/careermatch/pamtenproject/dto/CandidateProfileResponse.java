package com.careermatch.pamtenproject.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CandidateProfileResponse {
    @NotNull(message = "Candidate ID is required")
    @Min(value = 1, message = "Candidate ID must be a positive number")
    private Integer candidateId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;            // Return gender name to frontend

    @Min(value = 0, message = "Experience years cannot be negative")
    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYears;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*", message = "Invalid LinkedIn URL format")
    private String linkedinUrl;

    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Invalid GitHub username format")
    private String githubUsername;

    @NotNull(message = "Created at timestamp is required")
    @PastOrPresent(message = "Created at cannot be in the future")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at timestamp is required")
    @PastOrPresent(message = "Updated at cannot be in the future")
    private LocalDateTime updatedAt;
}