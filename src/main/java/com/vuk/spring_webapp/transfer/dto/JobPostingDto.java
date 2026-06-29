package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.job_posting.JobPosting}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code id (Long)}</li>
 *     <li>{@code title (String)}</li>
 *     <li>{@code description (String)}</li>
 *     <li>{@code dateOfPublishing (LocalDate)}</li>
 *     <li>{@code dateOfExpiration (LocalDate)}</li>
 *     <li>{@code status (JobPostingStatus)}</li>
 *     <li>{@code companyName (String)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 * @see JobPostingStatus
 */
@Data
public class JobPostingDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate dateOfPublishing;
    private LocalDate dateOfExpiration;
    private JobPostingStatus status;
    private String companyName;
}
