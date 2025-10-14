package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.domain.position.Position;
import lombok.Data;

import java.util.Date;

@Data
public class JobPostingDto {
    private Long id;
    private String title;
    private String description;
    private Date dateOfPublishing;
    private Date dateOfExpiration;
    private JobPostingStatus status;
    private Position position;
}
