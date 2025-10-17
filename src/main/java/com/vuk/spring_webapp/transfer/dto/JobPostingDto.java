package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobPostingDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate dateOfPublishing;
    private LocalDate dateOfExpiration;
    private JobPostingStatus status;
}
