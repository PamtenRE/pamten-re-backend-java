package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.*;
import com.careermatch.pamtenproject.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;  // Add this import
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/v1")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<JobResponse> postJob(@Valid @RequestBody JobPostRequest request) {
        JobResponse response = jobService.postJob(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<JobListingPageResponse> getAllJobs(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        JobListingPageResponse response = jobService.getAllJobs(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employer/{userId}")
    public ResponseEntity<List<JobResponse>> getJobsByEmployer(
            @PathVariable @NotBlank String userId) {
        List<JobResponse> jobs = jobService.getJobsByEmployer(userId);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable @Min(1) Integer jobId,
            @Valid @RequestBody JobUpdateRequest request,
            Authentication authentication) {

        String userId = authentication.getName(); // Get userId from JWT token
        JobResponse response = jobService.updateJob(jobId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}/{userId}")
    @PreAuthorize("hasAuthority('Recruiter')")
    public ResponseEntity<String> deleteJob(
            @PathVariable @Min(1) Integer jobId,
            @PathVariable @NotBlank String userId) {
        jobService.deleteJob(jobId, userId);
        return ResponseEntity.ok("Job deleted successfully");
    }
}