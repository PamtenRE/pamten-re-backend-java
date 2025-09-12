package com.careermatch.pamtenproject.controller;

import com.careermatch.pamtenproject.model.Gender;
import com.careermatch.pamtenproject.model.Industry;
import com.careermatch.pamtenproject.model.Role;
import com.careermatch.pamtenproject.repository.GenderRepository;
import com.careermatch.pamtenproject.repository.IndustryRepository;
import com.careermatch.pamtenproject.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/v1")
@RequiredArgsConstructor
public class TestController {

    private final RoleRepository roleRepository;
    private final GenderRepository genderRepository;
    private final IndustryRepository industryRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        try {
            // Test database connection
            roleRepository.count();
            health.put("status", "UP");
            health.put("database", "connected");
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "disconnected");
            health.put("error", e.getMessage());
        }
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/genders")
    public ResponseEntity<?> getAllGenders() {
        try {
            List<Gender> genders = genderRepository.findAll();
            return ResponseEntity.ok(genders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/industries")
    public ResponseEntity<?> getAllIndustries() {
        try {
            List<Industry> industries = industryRepository.findAll();
            return ResponseEntity.ok(industries);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
} 