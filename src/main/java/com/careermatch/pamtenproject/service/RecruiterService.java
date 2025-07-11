package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.model.Recruiter;
import com.careermatch.pamtenproject.model.User;
import com.careermatch.pamtenproject.repository.RecruiterRepository;
import com.careermatch.pamtenproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;

    public Recruiter createOrUpdateRecruiterProfile(String userId, Recruiter recruiterData) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Recruiter> existing = recruiterRepository.findByUser_UserId(userId);
        Recruiter recruiter;
        if (existing.isPresent()) {
            recruiter = existing.get();
            // Update only fields that exist in the new schema
            recruiter.setDateOfBirth(recruiterData.getDateOfBirth());
            recruiter.setGender(recruiterData.getGender());
        } else {
            recruiter = Recruiter.builder()
                    .user(user)
                    .dateOfBirth(recruiterData.getDateOfBirth())
                    .gender(recruiterData.getGender())
                    .build();
        }
        return recruiterRepository.save(recruiter);
    }

    public Recruiter getRecruiterProfile(String userId) {
        return recruiterRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }
}