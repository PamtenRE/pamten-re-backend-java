    package com.careermatch.pamtenproject.dto;

    import lombok.Builder;
    import lombok.Data;
    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;

    import java.time.LocalDate;

    @Data
    @Builder
    public class JobListingResponse {
        @NotNull(message = "Job ID is required")
        @Min(value = 1, message = "Job ID must be a positive number")
        private Integer jobId;

        @NotBlank(message = "Job title is required")
        private String title;

        private String city;

        private String state;

        @NotBlank(message = "Organization name is required")
        private String organizationName;

        @NotNull(message = "Posted date is required")
        private LocalDate postedDate;
    }