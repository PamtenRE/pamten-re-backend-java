package com.careermatch.pamtenproject.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.careermatch.pamtenproject.model.ApplicationStatus;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Long> {
    Optional<ApplicationStatus> findByStatusNameIgnoreCase(String statusName);
}
