package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * Request object for updating a {@link com.vuk.spring_webapp.domain.job_posting.JobPosting}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code title (String)}</li>
 *     <li>{@code description (String)}</li>
 *     <li>{@code dateOfExpiration (LocalDate)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class UpdateJobPostingRequest {
    private String title;
    private String description;
    private LocalDate dateOfExpiration;
}
