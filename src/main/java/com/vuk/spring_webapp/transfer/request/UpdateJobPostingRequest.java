package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateJobPostingRequest {
    private String title;
    private String description;
    private Date dateOfExpiration;
    private JobPostingStatus status;
}
