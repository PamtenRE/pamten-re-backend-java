package com.careermatch.pamtenproject.repository;

import com.careermatch.pamtenproject.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findByCandidateCandidateId(Integer candidateId);
    List<Resume> findByCandidateCandidateIdAndIsActiveTrue(Integer candidateId);
} 