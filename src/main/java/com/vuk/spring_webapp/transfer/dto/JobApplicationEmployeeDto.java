package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.job_application.JobApplication}.
 *
 * <p>Intended for communication with an employee.</p>
 *
 * @author Vuk Perovic
 */
@Data
public class JobApplicationEmployeeDto {
    private Long id;
    private LocalDate dateOfSubmission;
    private JobApplicationStatus status;
    private JobPostingDto jobPosting;
    private Long candidateId;
}
