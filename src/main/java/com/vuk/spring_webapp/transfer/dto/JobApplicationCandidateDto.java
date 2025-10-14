package com.vuk.spring_webapp.transfer.dto;


import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import lombok.Data;

import java.util.Date;

@Data
public class JobApplicationCandidateDto {
    private Long id;
    private Date dateOfSubmission;
    private JobApplicationStatus status;
    private JobPosting jobPosting;
}
