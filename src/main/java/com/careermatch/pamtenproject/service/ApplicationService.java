package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.model.Application;
import com.careermatch.pamtenproject.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repo;

    public Application apply(Long jobId, Long candidateId, Long defaultStatusId, String notes) {
        if (repo.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new IllegalStateException("Candidate already applied for this job");
        }

        Application app = new Application();
        app.setJobId(jobId);
        app.setCandidateId(candidateId);
        app.setStatusId(defaultStatusId);
        app.setNotes(notes);
        app.setApplicationDate(LocalDateTime.now());
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        return repo.save(app);
    }

    public List<Application> getByCandidate(Long candidateId) {
        return repo.findByCandidateId(candidateId);
    }

    public List<Application> getByJob(Long jobId) {
        return repo.findByJobId(jobId);
    }

    public Application updateStatus(Long applicationId, Long newStatusId) {
        Application app = repo.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("Application not found"));
        app.setStatusId(newStatusId);
        return repo.save(app);
    }

    public void withdraw(Long applicationId, Long candidateId) {
        Application app = repo.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("Application not found"));
        if (!app.getCandidateId().equals(candidateId)) {
            throw new SecurityException("You can only withdraw your own application");
        }
        repo.delete(app);
    }
}
