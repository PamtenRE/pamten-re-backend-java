package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class CandidateProfileRequest {
//    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    @Min(value = 1, message = "Gender ID must be a positive number")
    private Integer genderId;         // Accept genderId from frontend

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    private Integer experienceYears;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*", message = "Invalid LinkedIn URL format")
    private String linkedinUrl;

    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Invalid GitHub username format")
    private String githubUsername;
}