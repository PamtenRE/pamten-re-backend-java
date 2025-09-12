package com.careermatch.pamtenproject.service;

import com.careermatch.pamtenproject.dto.JobListingPageResponse;
import com.careermatch.pamtenproject.dto.JobListingResponse;
import com.careermatch.pamtenproject.dto.JobPostRequest;
import com.careermatch.pamtenproject.dto.JobResponse;
import com.careermatch.pamtenproject.dto.JobUpdateRequest;
import com.careermatch.pamtenproject.exception.*;
import com.careermatch.pamtenproject.model.*;
import com.careermatch.pamtenproject.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final LocationRepository locationRepository;
    private final EmployerRepository employerRepository;
    private final IndustryRepository industryRepository;
    private final UserRepository userRepository;

    @Transactional
    public JobResponse postJob(JobPostRequest request) {
        log.info("Posting new job for user: {}", request.getUserId());

        // Verify user is a recruiter with completed profile
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!"Recruiter".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new UnauthorizedActionException("Only recruiters can post jobs");
        }

        if (!user.getProfileCompleted()) {
            throw new BusinessRuleViolationException("Please complete your profile before posting jobs");
        }

        // Get employer details
        Employer employer = employerRepository.findByUser_UserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

        // Create or find location
        Location location = createOrFindLocation(request);

        // Create job
        Job job = Job.builder()
                .employer(employer)
                .location(location)
                .jobType(request.getJobType())
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .postedDate(LocalDate.now())
                .postedBy(request.getPostedBy())
                .billRate(request.getBillRate())
                .durationMonths(request.getDurationMonths())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        jobRepository.save(job);

        // Add industries if provided
        Set<Industry> industries = request.getIndustryNames().stream()
                .map(name -> industryRepository.findByIndustryName(name).orElse(null))
                .filter(industry -> industry != null)
                .collect(Collectors.toSet());
        job.setIndustries(industries);

        log.info("Job posted successfully with ID: {} for user: {}", job.getJobId(), request.getUserId());

        // Build response
        return JobResponse.builder()
                .jobId(job.getJobId())
                .employerNumber(employer.getEmployerNumber())
                .organizationName(employer.getOrganizationName())
                .jobType(job.getJobType())
                .title(job.getTitle())
                .description(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .postedDate(job.getPostedDate())
                .postedBy(job.getPostedBy())
                .billRate(job.getBillRate())
                .durationMonths(job.getDurationMonths())
                .isActive(job.getIsActive())
                .createdAt(job.getCreatedAt())
                .city(location.getCity())
                .state(location.getState())
                .zipCode(location.getZipCode())
                .country(location.getCountry())
                .industryNames(request.getIndustryNames())
                .build();
    }

    private Location createOrFindLocation(JobPostRequest request) {
        log.debug("Creating or finding location for city: {}, state: {}, zip: {}",
                request.getCity(), request.getState(), request.getZipCode());

        // Try to find existing location
        List<Location> existingLocations = locationRepository.findByCityAndState(
                request.getCity(), request.getState());

        for (Location existing : existingLocations) {
            if (existing.getZipCode().equals(request.getZipCode())) {
                log.debug("Found existing location: {}", existing.getLocationId());
                return existing;
            }
        }

        // Create new location
        Location newLocation = Location.builder()
                .region(request.getRegion())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry() != null ? request.getCountry() : "USA")
                .createdAt(LocalDateTime.now())
                .build();

        Location savedLocation = locationRepository.save(newLocation);
        log.debug("Created new location: {}", savedLocation.getLocationId());
        return savedLocation;
    }

    public JobListingPageResponse getAllJobs(int page, int size) {
        log.info("Fetching all jobs with page: {}, size: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Job> jobPage = jobRepository.findActiveJobsOrderByPostedDateDesc(pageable);

            JobListingPageResponse response = JobListingPageResponse.builder()
                    .jobs(jobPage.getContent().stream()
                            .map(this::convertToJobListingResponse)
                            .collect(Collectors.toList()))
                    .currentPage(page)
                    .totalPages(jobPage.getTotalPages())
                    .totalElements(jobPage.getTotalElements())
                    .pageSize(size)
                    .hasNext(jobPage.hasNext())
                    .hasPrevious(jobPage.hasPrevious())
                    .build();

            log.info("Found {} jobs on page {}", response.getJobs().size(), page);
            return response;

        } catch (Exception e) {
            log.error("Failed to fetch jobs: {}", e.getMessage());
            throw new BusinessRuleViolationException("Failed to fetch jobs: " + e.getMessage(), e);
        }
    }

    private JobListingResponse convertToJobListingResponse(Job job) {
        return JobListingResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .city(job.getLocation() != null ? job.getLocation().getCity() : null)
                .state(job.getLocation() != null ? job.getLocation().getState() : null)
                .organizationName(job.getEmployer().getOrganizationName())
                .postedDate(job.getPostedDate())
                .build();
    }

    public List<JobResponse> getJobsByEmployer(String userId) {
        log.info("Fetching jobs for employer: {}", userId);

        try {
            Employer employer = employerRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

            List<Job> jobs = jobRepository.findByEmployerEmployerId(employer.getEmployerId());

            log.info("Found {} jobs for employer: {}", jobs.size(), userId);

            return jobs.stream().map(this::convertToJobResponse).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to fetch jobs for employer {}: {}", userId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to fetch jobs: " + e.getMessage(), e);
        }
    }

    public JobResponse updateJob(Integer jobId, JobUpdateRequest request, String userId) {
        log.info("Updating job: {} for user: {}", jobId, userId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new JobNotFoundException("Job not found"));

            // SECURITY CHECK: Validate that this user owns the job
            if (!job.getEmployer().getUser().getUserId().equals(userId)) {
                throw new UnauthorizedActionException("You are not authorized to update this job");
            }

            // Update job fields
            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setRequiredSkills(request.getRequiredSkills());
            job.setJobType(request.getJobType());
            job.setBillRate(request.getBillRate());
            job.setDurationMonths(request.getDurationMonths());

            // Create or find updated location
            JobPostRequest tempRequest = new JobPostRequest();
            tempRequest.setCity(request.getCity());
            tempRequest.setState(request.getState());
            tempRequest.setZipCode(request.getZipCode());
            tempRequest.setCountry(request.getCountry());
            tempRequest.setRegion(request.getRegion());
            tempRequest.setStreetAddress(request.getStreetAddress());

            Location location = createOrFindLocation(tempRequest);
            job.setLocation(location);

            // Update industries
            Set<Industry> industries = request.getIndustryNames().stream()
                    .map(name -> industryRepository.findByIndustryName(name).orElse(null))
                    .filter(industry -> industry != null)
                    .collect(Collectors.toSet());
            job.setIndustries(industries);

            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

            log.info("Job updated successfully: {} for user: {}", jobId, userId);

            return convertToJobResponse(job);

        } catch (Exception e) {
            log.error("Failed to update job {}: {}", jobId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to update job: " + e.getMessage(), e);
        }
    }

    public void deleteJob(Integer jobId, String userId) {
        log.info("Deleting job: {} for user: {}", jobId, userId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new JobNotFoundException("Job not found"));

            // Validate that this user owns the job
            if (!job.getEmployer().getUser().getUserId().equals(userId)) {
                throw new UnauthorizedActionException("You are not authorized to delete this job");
            }

            jobRepository.delete(job);
            log.info("Job deleted successfully: {} for user: {}", jobId, userId);

        } catch (Exception e) {
            log.error("Failed to delete job {}: {}", jobId, e.getMessage());
            throw new BusinessRuleViolationException("Failed to delete job: " + e.getMessage(), e);
        }
    }

    private JobResponse convertToJobResponse(Job job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .employerNumber(job.getEmployer().getEmployerNumber())
                .organizationName(job.getEmployer().getOrganizationName())
                .jobType(job.getJobType())
                .title(job.getTitle())
                .description(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .postedDate(job.getPostedDate())
                .postedBy(job.getPostedBy())
                .billRate(job.getBillRate())
                .durationMonths(job.getDurationMonths())
                .isActive(job.getIsActive())
                .createdAt(job.getCreatedAt())
                .city(job.getLocation() != null ? job.getLocation().getCity() : null)
                .state(job.getLocation() != null ? job.getLocation().getState() : null)
                .zipCode(job.getLocation() != null ? job.getLocation().getZipCode() : null)
                .country(job.getLocation() != null ? job.getLocation().getCountry() : null)
                .industryNames(job.getIndustries() != null ?
                        job.getIndustries().stream().map(Industry::getIndustryName).collect(Collectors.toList()) : null)
                .build();
    }
}