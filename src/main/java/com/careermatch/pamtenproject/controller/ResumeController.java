package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.dto.ResumeResponse;
import com.careermatch.pamtenproject.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test/candidate")
@RequiredArgsConstructor
public class ResumeController {

    private final CandidateService candidateService;

    @PostMapping("/upload-resume")
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "setAsDefault", defaultValue = "true") Boolean setAsDefault,
            @RequestParam(value = "customName", required = false) String customName,
            @RequestParam("email") String email) {

        try {
            ResumeResponse response = candidateService.uploadResume(file, setAsDefault, customName, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
