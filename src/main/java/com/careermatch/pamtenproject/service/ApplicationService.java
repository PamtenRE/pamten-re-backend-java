package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.model.Application;
import com.careermatch.pamtenproject.repository.ApplicationRepository;
import com.careermatch.pamtenproject.dto.ApplicationApplyResponse;
import com.careermatch.pamtenproject.dto.ApplicationResponse;
import com.careermatch.pamtenproject.dto.ApplicationStageResponse;
import com.careermatch.pamtenproject.model.ApplicationStatus;
import com.careermatch.pamtenproject.model.Job;
import com.careermatch.pamtenproject.model.Location;
import com.careermatch.pamtenproject.repository.ApplicationStatusRepository;
import com.careermatch.pamtenproject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repo;
    private final JobRepository jobRepository;
    private final ApplicationStatusRepository statusRepository;

    // APPLY — returns a full ApplicationResponse DTO
    public ApplicationApplyResponse apply(Long jobId, Long candidateId, Long statusId, String notes, Long resumeId, String coverLetter) {
        // Check if candidate already applied
        if (repo.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new IllegalStateException("Candidate already applied for this job");
        }

        if (!jobRepository.existsById(jobId)) {
            throw new NoSuchElementException("Job with ID " + jobId + " does not exist");
        }

        if (statusId == null) {
        statusId = 1L;
        }   

        // Create new application
        Application app = new Application();
        app.setJobId(jobId);
        app.setCandidateId(candidateId);
        app.setStatusId(statusId);
        app.setNotes(notes);
        app.setResumeId(resumeId);
        app.setCoverLetter(coverLetter);
        app.setApplicationDate(LocalDateTime.now());
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        Application savedApp = repo.save(app);

        // Fetch related job and status info
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("Job not found"));

        ApplicationStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("Status not found"));

        // Build response DTO to match frontend structure
        return ApplicationApplyResponse.builder()
                .id(savedApp.getApplicationId()) 
                .jobId(jobId)
                .candidateId(candidateId.toString())
                .status(status.getStatusName())
                .appliedDate(savedApp.getApplicationDate().toLocalDate())
                .notes(notes)
                .resumeId(resumeId)
                .coverLetter(coverLetter)
                .build();
    }

    // Get all applications by candidate
    public List<ApplicationResponse> getByCandidate(Long candidateId) {
        List<Application> applications = repo.findByCandidateId(candidateId);
        List<ApplicationStatus> allStatuses = statusRepository.findAll(); 

        return applications.stream().map(app -> {
            ApplicationResponse.ApplicationResponseBuilder dto = ApplicationResponse.builder()
                    .id(app.getApplicationId())
                    .jobId(app.getJobId())
                    .candidateId(app.getCandidateId().toString())
                    .appliedDate(app.getApplicationDate().toLocalDate())
                    .notes(app.getNotes())
                    .resumeId(app.getResumeId())
                    .coverLetter(app.getCoverLetter());

            // Add readable status
            ApplicationStatus status = statusRepository.findById(app.getStatusId()).orElse(null);
            if (status != null) {
                dto.status(status.getStatusName());
            }

            // Add job details
            Job job = jobRepository.findById(app.getJobId()).orElse(null);
            if (job != null) {
                String companyName = job.getEmployer() != null
                    ? job.getEmployer().getOrganizationName()
                    : job.getPostedBy();
                dto.company(companyName);
                dto.position(job.getTitle());
                dto.salaryRange("$" + job.getMinSalary() + " - $" + job.getMaxSalary());

                if (job.getLocation() != null) {
                    Location loc = job.getLocation();
                    String location = String.join(", ",
                        java.util.stream.Stream.of(
                            loc.getStreetAddress(),
                            loc.getCity(),
                            loc.getState(),
                            loc.getZipCode(),
                            loc.getCountry()
                        )
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isBlank())
                            .toArray(String[]::new)
                    );
                    dto.location(location);
                } else {
                    dto.location("Remote");
                }

                dto.department(job.getDepartment());
            }

            // ====== APPLICATION STAGES ======
            List<ApplicationStageResponse> stages = allStatuses.stream()
                    .map(st -> ApplicationStageResponse.builder()
                            .stage(st.getStatusName())
                            .completed(st.getStatusId() <= app.getStatusId())
                            .date(st.getStatusId().equals(app.getStatusId()) ? app.getUpdatedAt() : null)
                            .build())
                    .collect(Collectors.toList());

            dto.applicationStages(stages);

            return dto.build();
        }).collect(Collectors.toList());
    }

    // Get applications for a specific job
    public List<Application> getByJob(Long jobId) {
        return repo.findByJobId(jobId);
    }

    // Update status of an application
    public Application updateStatus(Long applicationId, String newStatusName, String notes) {
        Application app = repo.findById(applicationId)
            .orElseThrow(() -> new NoSuchElementException("Application not found"));

        // Find status by its name 
        ApplicationStatus newStatus = statusRepository.findByStatusNameIgnoreCase(newStatusName)
            .orElseThrow(() -> new NoSuchElementException("Status not found: " + newStatusName));

        // Update fields
        app.setStatusId(newStatus.getStatusId());
        if (notes != null && !notes.isBlank()) {
            app.setNotes(notes);
        }
        app.setUpdatedAt(LocalDateTime.now());

        return repo.save(app);
    }


    // Withdraw an application (only by the same candidate)
    public void withdraw(Long applicationId, Long candidateId) {
        Application app = repo.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("Application not found"));

        if (!app.getCandidateId().equals(candidateId)) {
            throw new SecurityException("You can only withdraw your own application");
        }

        repo.delete(app);
    }
}
