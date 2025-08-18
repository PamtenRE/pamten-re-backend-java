//package com.careermatch.pamtenproject.controller;
//
//import com.careermatch.pamtenproject.dto.RecruiterProfileRequest;
//import com.careermatch.pamtenproject.service.ProfileService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import jakarta.validation.constraints.NotBlank;
//
//@RestController
//@RequestMapping("/api/profile/v1")
//@RequiredArgsConstructor
//public class ProfileController {
//
//    private final ProfileService profileService;
//
//    @PostMapping("/recruiter/complete")
//    public ResponseEntity<String> completeRecruiterProfile(
//            @Valid @RequestBody RecruiterProfileRequest request) {
//        try {
//            String result = profileService.completeRecruiterProfile(request);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/status/{userId}")
//    public ResponseEntity<?> isProfileCompleted(
//            @PathVariable @NotBlank String userId) {
//        try {
//            if (userId == null || userId.trim().isEmpty()) {
//                return ResponseEntity.badRequest().body("User ID is required");
//            }
//            boolean isCompleted = profileService.isProfileCompleted(userId);
//            return ResponseEntity.ok(isCompleted);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/employer-number/{userId}")
//    public ResponseEntity<?> getEmployerNumber(
//            @PathVariable @NotBlank String userId) {
//        try {
//            if (userId == null || userId.trim().isEmpty()) {
//                return ResponseEntity.badRequest().body("User ID is required");
//            }
//            String employerNumber = profileService.getEmployerNumber(userId);
//            if (employerNumber != null) {
//                return ResponseEntity.ok(employerNumber);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//}