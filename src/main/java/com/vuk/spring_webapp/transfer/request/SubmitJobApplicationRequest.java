package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

/**
 * Request object for submitting (creating) a {@link com.vuk.spring_webapp.domain.job_application.JobApplication}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code jobPostingId (Long)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class SubmitJobApplicationRequest {
    private Long jobPostingId;
}
