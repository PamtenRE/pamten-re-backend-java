package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Data
public class JobUpdateRequest {
    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Job title is required")
    @Size(min = 5, max = 255, message = "Job title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 50, message = "Job description must be at least 50 characters")
    private String description;

    @NotBlank(message = "Required skills are required")
    private String requiredSkills;

    @DecimalMin(value = "0.0", inclusive = false, message = "Bill rate must be greater than 0")
    private BigDecimal billRate;

    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 60, message = "Duration cannot exceed 60 months")
    private Integer durationMonths;

    // Location details
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid zip code format")
    private String zipCode;

    private String country;
    private String region;
    private String streetAddress;

    // Industry details
    private List<String> industryNames;
}