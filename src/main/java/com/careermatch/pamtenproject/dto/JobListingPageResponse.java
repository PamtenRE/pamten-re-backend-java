package com.careermatch.pamtenproject.dto;

import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class JobListingPageResponse {
    @NotNull(message = "Jobs list is required")
    @Size(min = 0, message = "Jobs list cannot be null")
    private List<JobListingResponse> jobs;

    @Min(value = 0, message = "Current page cannot be negative")
    private int currentPage;

    @Min(value = 0, message = "Total pages cannot be negative")
    private int totalPages;

    @Min(value = 0, message = "Total elements cannot be negative")
    private long totalElements;

    @Min(value = 1, message = "Page size must be at least 1")
    private int pageSize;

    private boolean hasNext;

    private boolean hasPrevious;
}