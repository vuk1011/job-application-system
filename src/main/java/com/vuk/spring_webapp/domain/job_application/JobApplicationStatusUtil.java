package com.vuk.spring_webapp.domain.job_application;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;

/**
 * Stores business rules related to a job application's status.
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
 */
public class JobApplicationStatusUtil {

    /**
     * Checks if business rules allow for a job application to change status from one to another.
     *
     * @param before current application status
     * @param after  application status the caller wants to switch to
     * @return true if status change can happen, false otherwise
     * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
     */
    public static boolean isStatusChangeAllowed(JobApplicationStatus before, JobApplicationStatus after) {
        if (before == after) {
            return true;
        }
        if (before == UNDER_REVIEW && (after == INTERVIEW_SCHEDULED || after == REJECTED)) {
            return true;
        } else if (before == INTERVIEW_SCHEDULED && (after == OFFERED || after == REJECTED)) {
            return true;
        } else if (before == OFFERED && (after == ACCEPTED || after == REJECTED)) {
            return true;
        } else if (before == REJECTED && after == OFFERED) {
            return true;
        } else if (before == REJECTED && after == ACCEPTED) {
            return true;
        }
        return false;
    }
}
