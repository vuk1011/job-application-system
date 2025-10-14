package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import com.vuk.spring_webapp.domain.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPosting);

    List<JobApplication> findByEmployeeAndJobPostingId(Employee employee, Long jobPostingId);

    List<JobApplication> findByEmployeeId(Long employeeId);
}
