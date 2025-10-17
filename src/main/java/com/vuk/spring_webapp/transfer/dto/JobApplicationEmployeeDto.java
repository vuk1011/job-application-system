package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationEmployeeDto {
    private Long id;
    private LocalDate dateOfSubmission;
    private JobApplicationStatus status;
    private JobPosting jobPosting;
    private Long candidateId;
}
