package com.careermatch.pamtenproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterResponse {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Role name is required")
    private String roleName;

    private boolean profileCompleted;

    // Employer details
    @NotBlank(message = "Employer number is required")
    private String employerNumber;

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "HR name is required")
    private String hrName;

    @NotBlank(message = "HR email is required")
    @Email(message = "Please provide a valid HR email address")
    private String hrEmail;

    private String endClient;

    private String vendorName;

    // Personal details
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String gender;

    // Industry
    private String industryName;

    private String industryDescription;

    // Timestamps
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;
} 