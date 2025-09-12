package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class RecruiterProfileRequest {
    // User info (already available from registration)
    @NotBlank(message = "User ID is required")
    private String userId;

    // Employer details
    @NotBlank(message = "HR name is required")
    @Size(min = 2, max = 255, message = "HR name must be between 2 and 255 characters")
    private String hrName;

    @NotBlank(message = "HR email is required")
    @Email(message = "Please provide a valid email address")
    private String hrEmail;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 255, message = "Organization name must be between 2 and 255 characters")
    private String organizationName;

    private String endClient;
    private String vendorName;

    // Personal details
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    // Industry
    private String industryName;
}