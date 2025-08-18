package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.ResumeResponse;
import com.careermatch.pamtenproject.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidate/resumes/v1")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(value = "setAsDefault", defaultValue = "true") Boolean setAsDefault,
            @RequestParam(value = "customName", required = false) String customName,
            @RequestParam("email") @Email String email) throws IOException {

        // Input validation
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        ResumeResponse response = resumeService.uploadResume(file, setAsDefault, customName, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/candidate")
    public ResponseEntity<List<ResumeResponse>> getCandidateResumes(
            @RequestParam("email") @Email String email) {
        List<ResumeResponse> resumes = resumeService.getCandidateResumes(email);
        return ResponseEntity.ok(resumes);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<String> deleteResume(
            @PathVariable @Min(1) Integer resumeId,
            @RequestParam("email") @Email String email) {
        resumeService.deleteResume(resumeId, email);
        return ResponseEntity.ok("Resume deleted successfully");
    }

    @PutMapping("/{resumeId}/default")
    public ResponseEntity<ResumeResponse> setDefaultResume(
            @PathVariable @Min(1) Integer resumeId,
            @RequestParam("email") @Email String email) {
        ResumeResponse response = resumeService.setDefaultResume(resumeId, email);
        return ResponseEntity.ok(response);
    }
}