package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import lombok.Data;

/**
 * Request object for updating {@link com.vuk.spring_webapp.domain.job_application.JobApplication}'s status.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code status (JobApplicationStatus)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 * @see JobApplicationStatus
 */
@Data
public class UpdateJobApplicationStatusRequest {
    private JobApplicationStatus status;
}
