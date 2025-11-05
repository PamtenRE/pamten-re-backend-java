package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.ApplicationApplyResponse;
import com.careermatch.pamtenproject.dto.ApplicationResponse;
import com.careermatch.pamtenproject.dto.ApplicationStatusUpdateRequest;
import com.careermatch.pamtenproject.dto.ApplicationStatusUpdateResponse;
import com.careermatch.pamtenproject.model.Application;
import com.careermatch.pamtenproject.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Apply to a job
    @PostMapping("/v1/apply")
    public ResponseEntity<ApplicationApplyResponse> apply(@RequestBody Application req) {
        // Call service and return ApplicationResponse DTO
        ApplicationApplyResponse response = applicationService.apply(
                req.getJobId(),
                req.getCandidateId(),
                req.getStatusId(),
                req.getNotes(),
                req.getResumeId(),
                req.getCoverLetter()
        );

        return ResponseEntity.ok(response);
    }

    // Get all applications for a candidate
    @GetMapping("/v1/candidate/{candidateId}")
    public ResponseEntity<List<ApplicationResponse>> getByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getByCandidate(candidateId));
    }

    // Get all applications for a specific job
    @GetMapping("/v1/job/{jobId}")
    public ResponseEntity<List<Application>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getByJob(jobId));
    }

    // Update application status
    @PutMapping("/v1/{applicationId}/status")
    public ResponseEntity<ApplicationStatusUpdateResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateRequest request) {

        Application updatedApp = applicationService.updateStatus(
                applicationId,
                request.getStatus(),
                request.getNotes()
        );

        return ResponseEntity.ok(
                ApplicationStatusUpdateResponse.builder()
                    .applicationId(updatedApp.getApplicationId())
                    .status(request.getStatus())
                    .updatedAt(updatedApp.getUpdatedAt())
                    .message("Status updated successfully")
                    .build()
        );
    }


    // Withdraw an application
    @DeleteMapping("/v1/{applicationId}/candidate/{candidateId}")
    public ResponseEntity<Void> withdraw(@PathVariable Long applicationId, @PathVariable Long candidateId) {
        applicationService.withdraw(applicationId, candidateId);
        return ResponseEntity.noContent().build();
    }
}
