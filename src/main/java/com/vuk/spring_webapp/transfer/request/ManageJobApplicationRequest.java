package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

/**
 * Request object for adding a {@link com.vuk.spring_webapp.domain.job_application.JobApplication} to the
 * {@link com.vuk.spring_webapp.domain.user.Employee}'s managed collection.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code applicationId (Long)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class ManageJobApplicationRequest {
    private Long applicationId;
}
