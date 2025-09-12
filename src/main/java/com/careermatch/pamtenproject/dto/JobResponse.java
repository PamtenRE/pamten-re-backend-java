package com.careermatch.pamtenproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    @NotNull(message = "Job ID is required")
    @Min(value = 1, message = "Job ID must be a positive number")
    private Integer jobId;

    @NotBlank(message = "Employer number is required")
    private String employerNumber;

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Required skills are required")
    private String requiredSkills;

    @NotNull(message = "Posted date is required")
    @PastOrPresent(message = "Posted date cannot be in the future")
    private LocalDate postedDate;

    private String postedBy;

    @DecimalMin(value = "0.0", inclusive = false, message = "Bill rate must be greater than 0")
    private BigDecimal billRate;

    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationMonths;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    // Location details
    private String city;

    private String state;

    private String zipCode;

    private String country;

    // Industry details
    private List<String> industryNames;
}