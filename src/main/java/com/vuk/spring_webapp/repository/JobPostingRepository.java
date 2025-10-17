package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
}
