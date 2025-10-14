package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findAllByStatus(JobPostingStatus status);

    boolean existsByIdAndStatus(Long id, JobPostingStatus status);
}
