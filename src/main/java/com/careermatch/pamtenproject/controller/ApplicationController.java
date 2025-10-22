package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.ApplyRequest;
import com.careermatch.pamtenproject.model.Application;
import com.careermatch.pamtenproject.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications/v1")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    // Candidate applies to a job
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody ApplyRequest req) {
        // Default status_id = 1 (Applied)
        Application app = service.apply(req.getJobId(), req.getCandidateId(), 1L, req.getNotes());
        return ResponseEntity.ok(Map.of(
                "applicationId", app.getApplicationId(),
                "message", "Application submitted successfully!"
        ));
    }

    // Get all jobs applied by a candidate
    @GetMapping("/candidate/{candidateId}")
    public List<Application> getCandidateApplications(@PathVariable Long candidateId) {
        return service.getByCandidate(candidateId);
    }

    // Recruiter: view all applications for a specific job
    @GetMapping("/job/{jobId}")
    public List<Application> getApplicationsForJob(@PathVariable Long jobId) {
        return service.getByJob(jobId);
    }

    // Recruiter: update status of an application
    @PutMapping("/{applicationId}/status/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long applicationId, @PathVariable Long statusId) {
        service.updateStatus(applicationId, statusId);
        return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
    }

    // Candidate: withdraw their application
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> withdraw(@PathVariable Long applicationId, @RequestParam Long candidateId) {
        service.withdraw(applicationId, candidateId);
        return ResponseEntity.ok(Map.of("message", "Application withdrawn"));
    }
}
